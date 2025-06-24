package com.hsnr.webshop.gui.beans;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsnr.webshop.gui.dto.FeedbackResponseDTO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Named("feedbackBean")
@SessionScoped
public class FeedbackBean implements Serializable {

    private List<FeedbackResponseDTO> feedbackListe;

    @PostConstruct
    public void init() {
        ladeFeedbacks();
    }

    public void ladeFeedbacks() {
        try {
            URL url = new URL("http://localhost:8080/webshop/api/feedback");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String encoded = java.util.Base64.getEncoder()
                    .encodeToString("mitarbeiter:passwort123".getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encoded);
            conn.setRequestProperty("Accept", "application/json");

            try (InputStream is = conn.getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                feedbackListe = mapper.readValue(is, new TypeReference<>() {});
            }

        } catch (IOException e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Fehler beim Laden der Feedbacks", e.getMessage()));
        }
    }

    public List<FeedbackResponseDTO> getFeedbackListe() {
        return feedbackListe;
    }
}