package com.hsnr.webshop.gui.beans;

import com.hsnr.webshop.gui.dto.FeedbackDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Named("feedbackKundeBean")
@SessionScoped
public class FeedbackKundeBean implements Serializable {

    @Inject private LoginBean           loginBean;
    @Inject private BestellHistorieBean historieBean;

    private Long   bestellnummer;   // per f:setPropertyActionListener gesetzt
    private String text;            // via inputTextarea gebunden

    /**
     * Sendet das Feedback und navigiert zurück zur Bestellhistorie.
     * 
     * @return Navigations-Outcome für JSF
     */
    public String submit() {
        if (bestellnummer == null || text == null || text.isBlank()) {
            showMessage("Bitte Bestellnummer und Feedback-Text angeben.");
            return null;
        }

        try {
            URL url = new URL("http://localhost:8080/webshop/api/feedback/abgeben/" + bestellnummer);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            String auth = loginBean.getUsername() + ":" + loginBean.getPassword();
            con.setRequestProperty("Authorization",
                "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8)));
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            FeedbackDTO dto = new FeedbackDTO();
            dto.setText(text);

            String json = new ObjectMapper().writeValueAsString(dto);
            try (OutputStream os = con.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int status = con.getResponseCode();
            if (status == 200 || status == 204) {
                showMessage("Danke für Ihr Feedback!");
                // Historie neu laden
                historieBean.reload();
                // Formular zurücksetzen
                bestellnummer = null;
                text = null;
                // Zurück zur Historie-Seite
                return "bestellHistorie.xhtml?faces-redirect=true";
            } else {
                showMessage("Fehler beim Abschicken (Status: " + status + ")");
            }

        } catch (Exception e) {
            showMessage("Fehler: " + e.getMessage());
        }
        return null;
    }

    private void showMessage(String msg) {
        FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(msg));
    }

    // ─── Getter/Setter ─────────────────────────────

    public Long getBestellnummer() {
        return bestellnummer;
    }
    public void setBestellnummer(Long bestellnummer) {
        this.bestellnummer = bestellnummer;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
}