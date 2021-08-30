package models;

import main.Data;

import java.util.ArrayList;

public class Kategorija {
    private static int ID;
    private int id;
    private String naziv;

    public Kategorija(String naziv) {
        this.naziv = naziv;
        this.id=generateId();
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
    public int generateId(){
        int id;
        ArrayList<Kategorija> lista=null;

        lista= Data.readFromJsonKategorija("kategorije.json");
        if(lista.size()==0){
            return 1;
        }
        else{
            id=lista.get(lista.size()-1).getId();
            return id+1;
        }
    }
}
