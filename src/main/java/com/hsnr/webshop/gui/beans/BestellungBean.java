package com.hsnr.webshop.gui.beans;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsnr.webshop.gui.dto.BestellungDTO;
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
import java.util.Base64;
import java.util.List;

@Named
@SessionScoped
public class BestellungBean implements Serializable {

    private List<BestellungDTO> bestellungen;

    @Inject
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        ladeAlleBestellungen();
    }

    private void setAuthHeader(HttpURLConnection conn, String method) throws Exception {
        String auth = loginBean.getUsername() + ":" + loginBean.getPassword();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        conn.setRequestMethod(method);
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
    }

    public void ladeAlleBestellungen() {
        try {
            URL url = new URL("http://localhost:8080/webshop/api/bestellungen/kunde/1");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            setAuthHeader(conn, "GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = reader.lines().reduce("", (a, b) -> a + b);

            ObjectMapper mapper = new ObjectMapper();
            bestellungen = mapper.readValue(json, new TypeReference<List<BestellungDTO>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Laden der Bestellungen", e.getMessage()));
        }
    }

    public void versende(Long bestellnummer) {
        try {
            URL url = new URL("http://localhost:8080/webshop/api/bestellungen/versenden/" + bestellnummer);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            setAuthHeader(conn, "PUT");

            int code = conn.getResponseCode();
            if (code == 200 || code == 204) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Bestellung versendet"));
                ladeAlleBestellungen();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Versenden", "HTTP-Code: " + code));
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Versenden", e.getMessage()));
        }
    }

    public List<BestellungDTO> getBestellungen() {
        return bestellungen;
    }
}