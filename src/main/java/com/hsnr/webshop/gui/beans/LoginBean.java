package com.hsnr.webshop.gui.beans;

import com.hsnr.webshop.gui.dto.BenutzerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private String username;
    private String password;
    private BenutzerDTO aktuellerBenutzer;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public BenutzerDTO getAktuellerBenutzer() { return aktuellerBenutzer; }

    public String login() {
        try {
            URL url = new URL("http://localhost:8080/webshop/api/benutzer/me");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    json.append(line);
                }
                in.close();

                ObjectMapper mapper = new ObjectMapper();
                aktuellerBenutzer = mapper.readValue(json.toString(), BenutzerDTO.class);

                return "landing.xhtml";
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Login fehlgeschlagen", "Benutzername oder Passwort ungültig"));
                return null;
            }

        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Fehler beim Login", e.getMessage()));
            return null;
        }
    }

    public String logout() {
        aktuellerBenutzer = null;
        username = null;
        password = null;
        return "login.xhtml?faces-redirect=true";
    }
}