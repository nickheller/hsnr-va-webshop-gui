package com.hsnr.webshop.gui.beans;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hsnr.webshop.gui.dto.BestellungDTO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Named("bestellHistorieBean")
@SessionScoped
public class BestellHistorieBean implements Serializable {

    private List<BestellungDTO> bestellungen;

    @Inject
    private LoginBean loginBean;

    private ObjectMapper mapper;

    @PostConstruct
    public void init() {
        mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ladeHistorie();
    }

    public void ladeHistorie() {
        try {
            // Nutzt den neuen /api/bestellungen/me Endpunkt
            String urlStr = "http://localhost:8080/webshop/api/bestellungen/me";
            HttpURLConnection con = (HttpURLConnection) new URL(urlStr).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Basic " + encodeCreds());
            con.setRequestProperty("Accept", "application/json");

            int status = con.getResponseCode();
            if (status == 200) {
                bestellungen = mapper.readValue(
                    con.getInputStream(),
                    new TypeReference<List<BestellungDTO>>() {}
                );
            } else {
                FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage("Fehler beim Laden der Bestellhistorie (Status: " + status + ")")
                );
                logError(con);
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage("Fehler beim Laden der Bestellhistorie: " + e.getMessage())
            );
        }
    }

    public List<BestellungDTO> getBestellungen() {
        return bestellungen;
    }

    public void reload() {
        ladeHistorie();
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Hilfsmethoden
    // ────────────────────────────────────────────────────────────────────────────
    private String encodeCreds() {
        String auth = loginBean.getUsername() + ":" + loginBean.getPassword();
        return Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }

    private void logError(HttpURLConnection con) {
        try (BufferedReader br = new BufferedReader(
                 new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
            br.lines().forEach(System.err::println);
        } catch (Exception ignored) {}
    }
}