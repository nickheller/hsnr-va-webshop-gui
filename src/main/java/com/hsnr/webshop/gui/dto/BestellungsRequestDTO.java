package com.hsnr.webshop.gui.dto;

import java.io.Serializable;
import java.util.List;

public class BestellungsRequestDTO implements Serializable {
    private Long kundennummer;
    private String lieferadresse;
    private String zahlungsmethode;
    private List<BestellpositionDTO> positionen;

    // Getter & Setter
    public Long getKundennummer() {
        return kundennummer;
    }

    public void setKundennummer(Long kundennummer) {
        this.kundennummer = kundennummer;
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

    public List<BestellpositionDTO> getPositionen() {
        return positionen;
    }

    public void setPositionen(List<BestellpositionDTO> positionen) {
        this.positionen = positionen;
    }
}