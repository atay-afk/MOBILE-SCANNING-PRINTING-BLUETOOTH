package com.example.androidapp.beans;

public class Commercial {
    private String id;
    private String nom;
    public static Commercial instance;

    public Commercial(String id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setId(String id) {
        this.id = id;
    }

}
