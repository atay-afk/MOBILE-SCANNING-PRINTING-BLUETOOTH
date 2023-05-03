package com.example.androidapp.beans;


import java.util.ArrayList;

public class Product {
    private String ID;
    private String Article,modele,taille,couleur;
    private double PA_HT,PA_TTC,TVA1,PV_HT,PV_TTC,COLISAGE,STOCKG;
    private int quantite,remise;

    public static boolean PA=false;

    public static String zone,zone2;

    public static ArrayList<Product> products=new ArrayList<>();
    public static Product instance;

    public Product(){
    }

    public int getRemise() {
        return remise;
    }

    public void setRemise(int remise) {
        this.remise = remise;
    }

    public String getCouleur() {
        return couleur;
    }

    public String getModele() {
        return modele;
    }

    public String getTaille() {
        return taille;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public void setTaille(String taille) {
        this.taille = taille;
    }

    public void setQuantite(int q) {
        this.quantite = q;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public String getArticle() {
        return Article;
    }

    public double getPA_HT() {
        return PA_HT;
    }

    public double getPA_TTC() {
        return PA_TTC;
    }

    public double getTVA1() {
        return TVA1;
    }

    public double getPV_HT() {
        return PV_HT;
    }

    public double getPV_TTC() {
        return PV_TTC;
    }

    public double getCOLISAGE() {
        return COLISAGE;
    }

    public double getSTOCKG() {
        return STOCKG;
    }

    public static Product getInstance() {
        return instance;
    }

    public void setArticle(String article) {

        int index = article.indexOf("'");

        if(index == - 1) {
            Article = article;
        } else {
            Article = article.replace("'", "''");
        }

    }

    public void setPA_HT(double PA_HT) {
        this.PA_HT = PA_HT;
    }

    public void setPA_TTC(double PA_TTC) {
        this.PA_TTC = PA_TTC;
    }

    public void setTVA1(double TVA1) {
        this.TVA1 = TVA1;
    }

    public void setPV_HT(double PV_HT) {
        this.PV_HT = PV_HT;
    }

    public void setPV_TTC(double PV_TTC) {
        this.PV_TTC = PV_TTC;
    }

    public void setCOLISAGE(double COLISAGE) {
        this.COLISAGE = COLISAGE;
    }

    public void setSTOCKG(double STOCKG) {
        this.STOCKG = STOCKG;
    }

    public static void setInstance(Product p) {
        instance = p;
    }


    public static void setProducts(int q){
        Product product=instance;
        product.setQuantite(q);
        products.add(product);

    }

}
