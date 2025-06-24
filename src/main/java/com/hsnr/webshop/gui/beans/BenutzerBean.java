package com.hsnr.webshop.gui.beans;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsnr.webshop.gui.dto.BenutzerDTO;
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
public class BenutzerBean implements Serializable {

    private List<BenutzerDTO> benutzerListe;
    private BenutzerDTO neuerBenutzer = new BenutzerDTO();
    private BenutzerDTO bearbeiteterBenutzer;

    @Inject
    private LoginBean loginBean;

    @PostConstruct
    public void init() {
        ladeBenutzerListe();
    }

    public void ladeBenutzerListe() {
        try {
            URL url = new URL("http://localhost:8080/webshop/api/benutzer");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            setAuthHeader(conn, "GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            ObjectMapper mapper = new ObjectMapper();
            benutzerListe = mapper.readValue(json.toString(), new TypeReference<List<BenutzerDTO>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void benutzerSpeichern() {
        try {
            URL url = new URL("http://localhost:8080/webshop/api/benutzer");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            setAuthHeader(conn, "POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(neuerBenutzer);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 201) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Benutzer gespeichert", "Benutzer erfolgreich angelegt"));
                ladeBenutzerListe();
                neuerBenutzer = new BenutzerDTO();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Benutzer konnte nicht gespeichert werden (Code: " + responseCode + ")"));
            }

        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Speichern", e.getMessage()));
        }
    }

    public void loeschen(BenutzerDTO benutzer) {
        try {
            URL url = new URL("http://localhost:8080/webshop/api/benutzer/" + benutzer.getBenutzerkennung());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            setAuthHeader(conn, "DELETE");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 204) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Benutzer gelöscht", "Benutzer erfolgreich entfernt"));
                ladeBenutzerListe();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", "Benutzer konnte nicht gelöscht werden (Code: " + responseCode + ")"));
            }

        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler beim Löschen", e.getMessage()));
        }
    }

    public String bearbeiten(BenutzerDTO benutzer) {
        this.bearbeiteterBenutzer = new BenutzerDTO();
        this.bearbeiteterBenutzer.setBenutzerkennung(benutzer.getBenutzerkennung());
        this.bearbeiteterBenutzer.setName(benutzer.getName());
        this.bearbeiteterBenutzer.setTelefonnummer(benutzer.getTelefonnummer());
        this.bearbeiteterBenutzer.setRolle(benutzer.getRolle());

        return "benutzerBearbeiten.xhtml?faces-redirect=true";
    }

    public String benutzerAktualisieren() {
        try {
            String benutzerkennung = bearbeiteterBenutzer.getBenutzerkennung();
            URL url = new URL("http://localhost:8080/webshop/api/benutzer/" + benutzerkennung);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            setAuthHeader(conn, "PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(bearbeiteterBenutzer);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200 || responseCode == 204) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Benutzer aktualisiert", "Änderungen erfolgreich gespeichert"));
                ladeBenutzerListe();
                return "nutzer.xhtml?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Fehler", "Änderung fehlgeschlagen (Code: " + responseCode + ")"));
                return null;
            }

        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Fehler beim Aktualisieren", e.getMessage()));
            return null;
        }
    }

    private void setAuthHeader(HttpURLConnection conn, String method) throws ProtocolException {
        String auth = loginBean.getUsername() + ":" + loginBean.getPassword();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
        conn.setRequestMethod(method);
    }

    public List<BenutzerDTO> getBenutzerListe() {
        return benutzerListe;
    }

    public BenutzerDTO getNeuerBenutzer() {
        return neuerBenutzer;
    }

    public void setNeuerBenutzer(BenutzerDTO neuerBenutzer) {
        this.neuerBenutzer = neuerBenutzer;
    }

    public BenutzerDTO getBearbeiteterBenutzer() {
        return bearbeiteterBenutzer;
    }

    public void setBearbeiteterBenutzer(BenutzerDTO bearbeiteterBenutzer) {
        this.bearbeiteterBenutzer = bearbeiteterBenutzer;
    }
}