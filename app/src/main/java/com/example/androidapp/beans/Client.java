package com.example.androidapp.beans;

public class Client {
    private String code;
    private String nom;
    private String tel;
    private int categorie;
    public static Client instance;

    public Client(String code, String nom, String tel, int categorie) {
        this.code = code;
        this.nom = nom;
        this.tel = tel;
        this.categorie = categorie;
    }

    public String getCode() {
        return code;
    }

    public String getNom() {
        return nom;
    }


    public int getCategorie() {
        return categorie;
    }


}
