package models;

import main.Data;

import java.util.ArrayList;

public class Proizvod {
    private int id;
    private String naziv;
    private double cena;
    private Kategorija kategorija;
    private String opis;
    private String putanja;

    public Proizvod(String naziv, double cena, Kategorija kategorija, String opis,String putanja) {
        this.naziv = naziv;
        this.cena = cena;
        this.kategorija = kategorija;
        this.opis = opis;
        this.id=generateId();
        this.putanja=putanja;
    }

    public String getPutanja() {
        return putanja;
    }

    public void setPutanja(String putanja) {
        this.putanja = putanja;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public double getCena() {
        return cena;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }

    public Kategorija getKategorija() {
        return kategorija;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }
    public int generateId(){
        int id;
        ArrayList<Proizvod> lista=null;

            lista= Data.readFromJsonProizvod("proizvodi.json");
            if(lista.size()==0){
                return 1;
            }
            else{
                id=lista.get(lista.size()-1).getId();
                return id+1;
            }
    }

    @Override
    public String toString() {
        return naziv +" "+kategorija.getNaziv()+" "+opis;
    }
}
