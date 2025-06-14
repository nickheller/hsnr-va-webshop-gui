package com.hsnr.webshop.gui.beans;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hsnr.webshop.gui.dto.BestellpositionDTO;
import com.hsnr.webshop.gui.dto.BestellungsRequestDTO;
import com.hsnr.webshop.gui.dto.ProduktDTO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Named("produktKundeBean")
@SessionScoped
public class ProduktKundeBean implements Serializable {

    private List<ProduktDTO> produkte;
    private String suchbegriff = "";

    @Inject private LoginBean  loginBean;
    @Inject private KundeBean  kundeBean;

    private ObjectMapper mapper;

    @PostConstruct
    public void init() {
        mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        loadAll();
    }

    public void loadAll() {
        try {
            HttpURLConnection con = openGet("http://localhost:8080/webshop/api/produkte/verfuegbar");
            if (con.getResponseCode() == 200) {
                produkte = mapper.readValue(
                    con.getInputStream(),
                    new TypeReference<List<ProduktDTO>>() {});
            } else {
                showMessage("Fehler beim Laden (Status: "+con.getResponseCode()+")");
                logError(con);
            }
        } catch (Exception e) {
            showMessage("Fehler: " + e.getMessage());
        }
    }

    public void suchen() {
        if (suchbegriff == null || suchbegriff.isBlank()) {
            loadAll();
            return;
        }
        produkte.removeIf(p ->
            !(p.getName().toLowerCase().contains(suchbegriff.toLowerCase())
            || (p.getBeschreibung()!=null 
                    && p.getBeschreibung().toLowerCase().contains(suchbegriff.toLowerCase()))
            || (p.getKategorie()!=null 
                    && p.getKategorie().toLowerCase().contains(suchbegriff.toLowerCase())))
        );
    }

    /**
     * Neue Variante: Bestellung für den aktuell eingeloggten Kunden,
     * ohne Kundennummer im JSON–Payload (nutzt /bestellungen/me).
     */
    public void bestellenMe(ProduktDTO produkt) {
        try {
            // Kundendaten laden, um Adresse/Zahlungsmethode zu holen
            var kd = kundeBean.getKunde();
            if (kd == null) {
                kundeBean.ladeKundendaten();
                kd = kundeBean.getKunde();
            }
            if (kd == null) {
                showMessage("Kundendaten fehlen. Bitte neu einloggen.");
                return;
            }

            // Request-DTO OHNE kundennummer
            BestellpositionDTO pos = new BestellpositionDTO();
            pos.setProduktnummer(produkt.getProduktnummer());
            pos.setMenge(1);

            BestellungsRequestDTO req = new BestellungsRequestDTO();
            req.setLieferadresse(kd.getAdresse());
            req.setZahlungsmethode(kd.getZahlungsmethode());
            req.setPositionen(List.of(pos));

            // POST /api/bestellungen/me
            HttpURLConnection con = (HttpURLConnection)
                new URL("http://localhost:8080/webshop/api/bestellungen/me")
                .openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Basic " + encodeCreds());
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String json = mapper.writeValueAsString(req);
            try (OutputStream os = con.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int status = con.getResponseCode();
            if (status < 300) {
                showMessage("Produkt erfolgreich bestellt!");
                loadAll();  // Bestand aktualisieren
            } else {
                logError(con);
                showMessage("Fehler beim Bestellen (Status: " + status + ")");
            }
        } catch (Exception e) {
            showMessage("Fehler: " + e.getMessage());
        }
    }

    // ─── Hilfsmethoden ──────────────────────────
    private HttpURLConnection openGet(String urlStr) throws Exception {
        var con = (HttpURLConnection)new URL(urlStr).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic " + encodeCreds());
        con.setRequestProperty("Accept", "application/json");
        return con;
    }

    private String encodeCreds() {
        String auth = loginBean.getUsername() + ":" + loginBean.getPassword();
        return Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }

    private void showMessage(String msg) {
        FacesContext.getCurrentInstance()
            .addMessage(null, new FacesMessage(msg));
    }

    private void logError(HttpURLConnection con) {
        try (var br = new BufferedReader(
               new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
            br.lines().forEach(System.err::println);
        } catch (Exception ignored) {}
    }

    // ─── Getter/Setter ─────────────────────────
    public List<ProduktDTO> getProdukte()       { return produkte; }
    public String getSuchbegriff()              { return suchbegriff; }
    public void setSuchbegriff(String s)        { this.suchbegriff = s; }
}