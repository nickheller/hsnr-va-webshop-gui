package com.hsnr.webshop.gui.dto;

public class FeedbackResponseDTO {
    private Long id;
    private Long bestellnummer;
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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