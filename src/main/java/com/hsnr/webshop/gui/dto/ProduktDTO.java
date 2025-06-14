package com.hsnr.webshop.gui.dto;

import java.io.Serializable;

public class ProduktDTO implements Serializable {
    private Long produktnummer;
    private String name;
    private String beschreibung;
    private Double preis;
    private String kategorie;
    private Integer bestand;
    private String lieferzeit;

    // Standard-Konstruktor
    public ProduktDTO() {}

    // Konstruktor mit allen Feldern
    public ProduktDTO(Long produktnummer, String name, String beschreibung, Double preis,
                      String kategorie, Integer bestand, String lieferzeit) {
        this.produktnummer = produktnummer;
        this.name = name;
        this.beschreibung = beschreibung;
        this.preis = preis;
        this.kategorie = kategorie;
        this.bestand = bestand;
        this.lieferzeit = lieferzeit;
    }

    // Getter & Setter
    public Long getProduktnummer() {
        return produktnummer;
    }

    public void setProduktnummer(Long produktnummer) {
        this.produktnummer = produktnummer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public Double getPreis() {
        return preis;
    }

    public void setPreis(Double preis) {
        this.preis = preis;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public Integer getBestand() {
        return bestand;
    }

    public void setBestand(Integer bestand) {
        this.bestand = bestand;
    }

    public String getLieferzeit() {
        return lieferzeit;
    }

    public void setLieferzeit(String lieferzeit) {
        this.lieferzeit = lieferzeit;
    }
}