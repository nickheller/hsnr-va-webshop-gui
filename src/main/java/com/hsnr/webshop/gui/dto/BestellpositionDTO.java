package com.hsnr.webshop.gui.dto;

public class BestellpositionDTO {
    private Long produktnummer;
    private String produktname;
    private int menge;
    private double einzelpreis;

    public Long getProduktnummer() {
        return produktnummer;
    }

    public void setProduktnummer(Long produktnummer) {
        this.produktnummer = produktnummer;
    }

    public String getProduktname() {
        return produktname;
    }

    public void setProduktname(String produktname) {
        this.produktname = produktname;
    }

    public int getMenge() {
        return menge;
    }

    public void setMenge(int menge) {
        this.menge = menge;
    }

    public double getEinzelpreis() {
        return einzelpreis;
    }

    public void setEinzelpreis(double einzelpreis) {
        this.einzelpreis = einzelpreis;
    }
}