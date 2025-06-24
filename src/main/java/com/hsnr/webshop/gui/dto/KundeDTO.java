package com.hsnr.webshop.gui.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KundeDTO implements Serializable {
    private Long kundennummer;
    private String vorname;
    private String nachname;
    private String adresse;
    private String telefonnummer;
    private String email;
    private LocalDate geburtsdatum;
    private String zahlungsmethode;
    private String benutzerkennung;

    public Long getKundennummer() { return kundennummer; }
    public void setKundennummer(Long kundennummer) { this.kundennummer = kundennummer; }

    public String getVorname() { return vorname; }
    public void setVorname(String vorname) { this.vorname = vorname; }

    public String getNachname() { return nachname; }
    public void setNachname(String nachname) { this.nachname = nachname; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelefonnummer() { return telefonnummer; }
    public void setTelefonnummer(String telefonnummer) { this.telefonnummer = telefonnummer; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getGeburtsdatum() { return geburtsdatum; }
    public void setGeburtsdatum(LocalDate geburtsdatum) { this.geburtsdatum = geburtsdatum; }

    public String getZahlungsmethode() { return zahlungsmethode; }
    public void setZahlungsmethode(String zahlungsmethode) { this.zahlungsmethode = zahlungsmethode; }

    public String getBenutzerkennung() { return benutzerkennung; }
    public void setBenutzerkennung(String benutzerkennung) { this.benutzerkennung = benutzerkennung; }
}