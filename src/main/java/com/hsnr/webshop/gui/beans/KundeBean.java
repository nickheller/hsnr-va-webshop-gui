package com.hsnr.webshop.gui.beans;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hsnr.webshop.gui.dto.KundeDTO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Named("kundeBean")
@SessionScoped
public class KundeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private KundeDTO kunde = new KundeDTO();
    private ObjectMapper mapper;

    @Inject
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        // Einmalige Konfiguration des ObjectMappers
        mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            // ignoriert unbekannte Felder (wie 'benutzer')
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ladeKundendaten();
    }

    public void ladeKundendaten() {
        try {
            URL url = new URL("http://localhost:8080/webshop/api/kunden/meinprofil");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Basic " + encodeCredentials());
            con.setRequestProperty("Accept", "application/json");

            int status = con.getResponseCode();
            if (status == 200) {
                try (InputStream in = con.getInputStream()) {
                    kunde = mapper.readValue(in, KundeDTO.class);
                }
            } else {
                showMessage("Fehler beim Laden der Kundendaten (Status: " + status + ")");
            }
        } catch (IOException e) {
            showMessage("Fehler: " + e.getMessage());
        }
    }

    public String speichern() {
        try {
            URL url = new URL("http://localhost:8080/webshop/api/kunden");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Authorization", "Basic " + encodeCredentials());
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String json = mapper.writeValueAsString(kunde);
            try (OutputStream os = con.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int status = con.getResponseCode();
            if (status >= 200 && status < 300) {
                showMessage("Änderungen gespeichert.");
            } else {
                showMessage("Fehler beim Speichern (Status: " + status + ")");
            }
        } catch (IOException e) {
            showMessage("Fehler: " + e.getMessage());
        }
        return null;
    }

    public KundeDTO getKunde() {
        return kunde;
    }

    private String encodeCredentials() {
        String auth = loginBean.getUsername() + ":" + loginBean.getPassword();
        return Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }

    private void showMessage(String msg) {
        FacesContext.getCurrentInstance()
            .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }
}