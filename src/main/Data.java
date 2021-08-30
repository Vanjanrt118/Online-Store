package main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import models.Kategorija;
import models.Proizvod;

import java.io.*;
import java.util.ArrayList;

public class Data {
    public static boolean writeToJSONProizvod(ArrayList<Proizvod> proizvodi, String path){
        try {
            Writer writer=new FileWriter(path);
            Gson gson = new Gson();
            gson.toJson(proizvodi,writer);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<Proizvod> readFromJsonProizvod(String path){
        if(!new File(path).exists()){
            return null;
        }
        try {
            JsonReader reader=new JsonReader(new FileReader(path));
            Gson gson = new Gson();
           ArrayList<Proizvod> lista=gson.fromJson(reader,new TypeToken<ArrayList<Proizvod>>(){}.getType());
           if(lista==null){
               return new ArrayList<Proizvod>();
           }
           else{
               return lista;
           }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static boolean writeToJSONKategorija(ArrayList<Kategorija> kategorije, String path){
        try {
            Writer writer=new FileWriter(path);
            Gson gson = new Gson();
            gson.toJson(kategorije,writer);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static ArrayList<Kategorija> readFromJsonKategorija(String path){
        if(!new File(path).exists()){
            return null;
        }
        try {
            JsonReader reader=new JsonReader(new FileReader(path));
            Gson gson = new Gson();
            ArrayList<Kategorija> lista=gson.fromJson(reader,new TypeToken<ArrayList<Kategorija>>(){}.getType());
            if(lista==null){
                return new ArrayList<Kategorija>();
            }
            else{
                return lista;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
