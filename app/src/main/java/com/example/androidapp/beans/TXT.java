package com.example.androidapp.beans;

public class TXT {
    private String id;
    private int quantite;
    private String designation,comId,commer,codeclt,client;
    private double pvttc,pvth,tva,remise;

    public double getRemise() {
        return remise;
    }


    public TXT(String id, int quantite, String designation, double pvht, double pvttc, double tva, double remise
            ,String comId,String commer,String codeclt,String client) {
        this.id = id;
        this.quantite = quantite;
        this.designation = designation;
        this.pvttc = pvttc;
        this.pvth = pvht;
        this.tva = tva;
        this.remise=remise;
        this.comId = comId;
        this.commer = commer;
        this.codeclt = codeclt;
        this.client=client;

    }

    public String getComId() {
        return comId;
    }

    public String getCommer() {
        return commer;
    }

    public String getCodeclt() {
        return codeclt;
    }

    public String getClient() {
        return client;
    }

    public String getId() {
        return id;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesignation() {
        return designation;
    }


    public double getPvth() {
        return pvth;
    }

    public double getPvttc() {
        return pvttc;
    }

    public double getTva() {
        return tva;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
