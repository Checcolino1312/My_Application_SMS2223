package com.example.animalapp.Model;

import java.io.Serializable;

public class Segnalazioni implements Serializable {
    public String id;
    public String descrizione;
    public String destinatarioEnte;
    public String destinatarioUtente;
    public String destinatarioVeterionario;
    public String idMittente;
    public String posizione;
    public String tipologiaSegnalazione;
    public String imgSegnalazione;
    public String idPresaInCarico;
    public String presaInCarico;

    public double lattitudine;

    public double longitudine;


    public Segnalazioni(){

    }
}
