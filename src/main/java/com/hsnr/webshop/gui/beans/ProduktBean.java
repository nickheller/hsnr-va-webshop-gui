package com.hsnr.webshop.gui.beans;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsnr.webshop.gui.dto.ProduktDTO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.List;

@Named
@SessionScoped
public class ProduktBean implements Serializable {

    private List<ProduktDTO> produktListe;
    private ProduktDTO neuesProdukt = new ProduktDTO();
    private ProduktDTO bearbeitetesProdukt;

    @Inject
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        ladeAlleProdukte();
    }

    private void setAuthHeader(HttpURLConnection conn, String method) throws ProtocolException {
        String auth = loginBean.getUsername() + ":" + loginBean.getPassword();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
        conn.setRequestMethod(method);
    }

    public void ladeAlleProdukte() {
        try {
            URL url = new URL("http://localhost:8080/webshop/api/produkte");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            setAuthHeader(conn, "GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = reader.lines().reduce("", (a, b) -> a + b);

            ObjectMapper mapper = new ObjectMapper();
            produktListe = mapper.readValue(json, new TypeReference<List<ProduktDTO>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ladeVerfuegbareProdukte() {
        try {
            URL url = new URL("http://localhost:8080/webshop/api/produkte/verfuegbar");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            setAuthHeader(conn, "GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = reader.lines().reduce("", (a, b) -> a + b);

            ObjectMapper mapper = new ObjectMapper();
            produktListe = mapper.readValue(json, new TypeReference<List<ProduktDTO>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void produktSpeichern() {
        try {
            neuesProdukt.setProduktnummer(null);

            URL url = new URL("http://localhost:8080/webshop/api/produkte");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            setAuthHeader(conn, "POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(neuesProdukt);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

           int code = conn.getResponseCode();
            if (code == 200 || code == 201 || code == 204) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("Produkt gespeichert"));
                ladeAlleProdukte(); // Tabelle aktualisieren
                neuesProdukt = new ProduktDTO(); // Formular leeren
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Fehlercode: " + code));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String bearbeiten(ProduktDTO produkt) {
        this.bearbeitetesProdukt = new ProduktDTO(
                produkt.getProduktnummer(),
                produkt.getName(),
                produkt.getBeschreibung(),
                produkt.getPreis(),
                produkt.getKategorie(),
                produkt.getBestand(),
                produkt.getLieferzeit()
        );
        return "produktBearbeiten.xhtml?faces-redirect=true";
    }

    public String aktualisieren() {
        try {
            URL url = new URL("http://localhost:8080/webshop/api/produkte");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            setAuthHeader(conn, "PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(bearbeitetesProdukt);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int code = conn.getResponseCode();
            if (code == 200 || code == 204) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Produkt aktualisiert"));
                ladeAlleProdukte();
                return "produkte.xhtml?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Fehlercode: " + code));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    return null; // bleibt auf der Seite, wenn Fehler auftritt
}

    public void loeschen(ProduktDTO produkt) {
        try {
            URL url = new URL("http://localhost:8080/webshop/api/produkte/" + produkt.getProduktnummer());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            setAuthHeader(conn, "DELETE");

            int code = conn.getResponseCode();
            if (code == 200 || code == 204) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Produkt gelöscht"));
                ladeAlleProdukte();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Löschen, das Produkt ist noch mit einer Bestellung verknüpft.", "Fehlercode: " + code));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getter & Setter
    public List<ProduktDTO> getProduktListe() {
        return produktListe;
    }

    public ProduktDTO getNeuesProdukt() {
        return neuesProdukt;
    }

    public void setNeuesProdukt(ProduktDTO neuesProdukt) {
        this.neuesProdukt = neuesProdukt;
    }

    public ProduktDTO getBearbeitetesProdukt() {
        return bearbeitetesProdukt;
    }

    public void setBearbeitetesProdukt(ProduktDTO bearbeitetesProdukt) {
        this.bearbeitetesProdukt = bearbeitetesProdukt;
    }
}