package main;

import com.google.gson.Gson;
import models.Kategorija;
import models.Proizvod;
import org.apache.commons.lang3.ObjectUtils;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import static spark.Spark.*;

public class Launcher {
    public static void main(String[] args) {
        staticFiles.location("/public");
        port(5000);
        String pathProizvod="proizvodi.json";
        String pathKategorija="kategorije.json";
        HashMap<String,Object> polja=new HashMap<>();
        ArrayList<Kategorija> kategorije=Data.readFromJsonKategorija(pathKategorija);
        ArrayList<Proizvod>proizvodi= Data.readFromJsonProizvod(pathProizvod);

        get("/",(request, response) -> {
            if(request.session().attribute("korIme")!=null && request.session().attribute("lozinka")!=null){
            polja.put("sesija","Odjavite se");
        }
            polja.put("proizvodi", proizvodi);
            polja.remove("dodataKategorija");
            polja.remove("izbrisanaKategorija");
            polja.remove("izmenjenaKategorija");
            polja.remove("unetProizvod");
            polja.remove("izbrisanProizvod");
            polja.remove("izmenjenProizvod");
            polja.remove("prazno");
            polja.remove("praznoProizvod");
        return new ModelAndView(polja,"index.hbs");
    },new HandlebarsTemplateEngine());
    post("/sortiraj",(request, response) -> {
        response.type("aplication/json");
        String sortiraj=request.queryParams("sortiraj");
        if(sortiraj.equals("1")){
            proizvodi.sort(Comparator.comparing(Proizvod::getNaziv));
        }
        else if(sortiraj.equals("2")){
            proizvodi.sort(Comparator.comparing(Proizvod::getNaziv).reversed());
        }
        else if(sortiraj.equals("3")){
            proizvodi.sort(Comparator.comparing(Proizvod::getCena));
        }
        else if(sortiraj.equals("4")){
            proizvodi.sort(Comparator.comparing(Proizvod::getCena).reversed());
        }
        Gson g=new Gson();
        return g.toJson(proizvodi);
    });
    post("/pretrazi",(request, response) -> {
        response.type("aplication/json");
        String termin=request.queryParams("termin");
        ArrayList<Proizvod> pronadjeni=new ArrayList<>();
        for(Proizvod p:proizvodi){
            if(p.toString().toLowerCase().contains(termin)){
                pronadjeni.add(p);
            }
        }
        Gson g=new Gson();
        return g.toJson(pronadjeni);
    });
    get("/loginForma",(request, response) -> {

        if(request.session().attribute("korIme")!=null && request.session().attribute("lozinka")!=null){
            polja.put("sesija","Odjavite se");
        }
        return new ModelAndView(polja,"login.hbs") ;
    },new HandlebarsTemplateEngine());
    post("/login",(request, response) -> {
        String korIme=request.queryParams("korIme");
        String lozinka=request.queryParams("lozinka");
        if(korIme.equals("admin") && lozinka.equals("admin")){
             request.session().attribute("korIme",korIme);
             request.session().attribute("lozinka",lozinka);
             response.redirect("/adminPanel");
             return null;
        }
        polja.put("poruka","Pogresno korisnicko ime ili lozinka!");
        return new ModelAndView(polja,"login.hbs");
    },new HandlebarsTemplateEngine());
    get("/adminPanel",(request, response) -> {
        if(request.session().attribute("korIme")==null && request.session().attribute("lozinka")==null){
           polja.put("greska","Morate se prijaviti da bi nastavili!");
           return new ModelAndView(polja,"greska.hbs");
            }
        else{
            polja.put("sesija","Odjavite se");
            polja.put("kategorije",kategorije);
            polja.put("proizvodi",proizvodi);
            return new ModelAndView(polja,"adminPanel.hbs");
        }

    },new HandlebarsTemplateEngine());
    post("/unosKategorije",(request, response) -> {
        response.redirect("/adminPanel");
        String naziv= request.queryParams("naziv")   ;
         if(naziv.equals("")){
             polja.put("prazno","Morate uneti naziv kategorije!");
            return new ModelAndView(polja,"adminPanel.hbs");
     }
     else {
         Kategorija k = new Kategorija(naziv);
         kategorije.add(k);
         Data.writeToJSONKategorija(kategorije, pathKategorija);
         polja.put("dodataKategorija", "Uspesno uneta kategorija!");
         return new ModelAndView(polja, "adminPanel.hbs");
     }
    },new HandlebarsTemplateEngine());
    post("/izbrisiKategoriju",(request, response) -> {
        response.redirect("/adminPanel");
        boolean proveri=false;
        int id= Integer.parseInt(request.queryParams("kategorija"));
        Kategorija k=new Kategorija("neka kategorija");
        for(Kategorija kategorija:kategorije){
        if(kategorija.getId()==id) {
            k=kategorija;
            break;
        }
    }
        for(Proizvod p:proizvodi){
        if(p.getKategorija().getId()==(k.getId())) proveri=true;
    }
        if(!proveri){
            kategorije.remove(k);
            polja.put("izbrisanaKategorija","Kategorija uspesno izbrisana!");
            polja.put("boja","success");
            Data.writeToJSONKategorija(kategorije,pathKategorija);
    }
        else {
            polja.put("izbrisanaKategorija","Nije moguce izbrisati kategoriju koja ima proizvode!");
             polja.put("boja","danger");
    }
        return polja;
    });
   get("/izmeniKategoriju/:id",(request, response) -> {
       int id=Integer.parseInt(request.params("id"));
       for(Kategorija k:kategorije){
           if(k.getId()==id){
               polja.put("kategorija",k);
           }
       }
       return new ModelAndView(polja,"izmeni.hbs");

   },new HandlebarsTemplateEngine());
   post("/sacuvajIzmenuKategorija",(request, response) -> {
       response.redirect("/adminPanel");
    String naziv=request.queryParams("naziv");
    int id=Integer.parseInt(request.queryParams("id"));
    for(Kategorija k:kategorije){
        if(k.getId()==id){
            k.setNaziv(naziv);
        }
    }
        Data.writeToJSONKategorija(kategorije,pathKategorija);
       polja.put("izmenjenaKategorija","Uspesno ste izmenili podatke");
       return  polja;
   });
   post("/unosProizvoda",(request, response) -> {
       response.redirect("/adminPanel");
        if(request.queryParams("naziv").equals("") || request.queryParams("cena").equals("") ||request.queryParams("kategorijaProizvoda").equals("") || request.queryParams("opis").equals("") || request.queryParams("putanja").equals("")){
            polja.put("praznoProizvod","Morate uneti sve podatke!");
            return polja;
        }
        else {
            String naziv=request.queryParams("naziv");
            double cena=Double.parseDouble(request.queryParams("cena"));
            int id=Integer.parseInt(request.queryParams("kategorijaProizvoda"));
            String opis=request.queryParams("opis");
            String putanja=request.queryParams("putanja");
            Kategorija k=null;
            for(Kategorija kat:kategorije){
                if(kat.getId()==id){
                    k=kat;
                    break;
                }
            }
            Proizvod p = new Proizvod(naziv, cena, k, opis, putanja);
            proizvodi.add(p);
            Data.writeToJSONProizvod(proizvodi, pathProizvod);
            polja.put("unetProizvod", "Proizvod uspesno dodat!");
            return polja;
        }
   });
   post("/izbrisiProizvod",(request, response) -> {
       response.redirect("/adminPanel");
       int id=Integer.parseInt(request.queryParams("proizvod"));
        for(Proizvod p:proizvodi){
         if(p.getId()==id){
            proizvodi.remove(p);
            break;
        }
    }
    Data.writeToJSONProizvod(proizvodi,pathProizvod);
    polja.put("izbrisanProizvod","Proizvod uspesno obrisan!");
    return polja;

   });
        get("/izmeniProizvod/:id",(request, response) -> {
            int id=Integer.parseInt(request.params("id"));
            for(Proizvod p:proizvodi){
                if(p.getId()==id){
                    polja.put("proizvod",p);
                }
            }
            return new ModelAndView(polja,"izmeniProizvod.hbs");

        },new HandlebarsTemplateEngine());
        post("/sacuvajIzmenuProizvod",(request, response) -> {
            response.redirect("/adminPanel");
            String naziv=request.queryParams("naziv");
            double cena=Double.parseDouble(request.queryParams("cena"));
            int id=Integer.parseInt(request.queryParams("id"));
            String opis=request.queryParams("opis");
            int kategorija=Integer.parseInt(request.queryParams("kategorijaProizvoda"));
            Kategorija k=null;
            for(Kategorija kat:kategorije){
                if(kat.getId()==kategorija){
                    k=kat;
                    break;
                }
            }
            for(Proizvod p:proizvodi){
                if(p.getId()==id){
                    p.setNaziv(naziv);
                    p.setCena(cena);
                    p.setKategorija(k);
                    p.setOpis(opis);
                }
            }
            Data.writeToJSONProizvod(proizvodi,pathProizvod);
            polja.put("izmenjenProizvod","Uspesno ste izmenili proizvod");
            return  polja;
        });
        get("/odjava",(request, response) -> {
            response.redirect("/");
            request.session().removeAttribute("korIme");
            request.session().removeAttribute("lozinka");
            polja.remove("sesija");
            polja.remove("poruka");
return null;
        });
    }
}
