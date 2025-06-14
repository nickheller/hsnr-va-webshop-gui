package com.hsnr.webshop.gui.dto;

import java.util.List;

public class BestellungDTO {
    private Long bestellnummer;
    private String bestelldatum;
    private String status;
    private String lieferadresse;
    private String zahlungsmethode;
    private double gesamtpreis;
    private List<BestellpositionDTO> positionen;

    public Long getBestellnummer() {
        return bestellnummer;
    }

    public void setBestellnummer(Long bestellnummer) {
        this.bestellnummer = bestellnummer;
    }

    public String getBestelldatum() {
        return bestelldatum;
    }

    public void setBestelldatum(String bestelldatum) {
        this.bestelldatum = bestelldatum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLieferadresse() {
        return lieferadresse;
    }

    public void setLieferadresse(String lieferadresse) {
        this.lieferadresse = lieferadresse;
    }

    public String getZahlungsmethode() {
        return zahlungsmethode;
    }

    public void setZahlungsmethode(String zahlungsmethode) {
        this.zahlungsmethode = zahlungsmethode;
    }

    public double getGesamtpreis() {
        return gesamtpreis;
    }

    public void setGesamtpreis(double gesamtpreis) {
        this.gesamtpreis = gesamtpreis;
    }

    public List<BestellpositionDTO> getPositionen() {
        return positionen;
    }

    public void setPositionen(List<BestellpositionDTO> positionen) {
        this.positionen = positionen;
    }
}