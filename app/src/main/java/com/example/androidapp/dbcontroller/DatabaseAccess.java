package com.example.androidapp.dbcontroller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.androidapp.beans.Product;
import com.example.androidapp.beans.TXT;

import java.util.ArrayList;


public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    Cursor c;
    public static String id;

    //private constructor so that object creation from outside the class is avoided
    private DatabaseAccess(Context context){
        this.openHelper=new DatabaseOpenHelper(context);
    }

    //to return the single instance of database
    public static DatabaseAccess getInstance(Context context){
        if(instance==null){
            instance=new DatabaseAccess(context);
        }
        return instance;
    }

    //to open the database
    public void open(){
        this.db=openHelper.getWritableDatabase();
    }

    //closing the database connection

    public void close(){
        if(db!=null){
            this.db.close();
        }
    }

    //GET PRODUCT
    public Product getProduct(String code){
        id=code;
        c = db.rawQuery("SELECT ARTICLE,PA_HT,PA_TTC,TVA1,PV_HT,PV_TTC,COLISAGE,STOCKG,MODELE,COULEUR,TAILLE FROM ARTICLE WHERE REF_ART='"+code+"'", null);
        Product produit=new Product();
        while (c.moveToNext()) {
            produit.setArticle(c.getString(0));
            produit.setPA_HT(c.getDouble(1));
            produit.setPA_TTC(c.getDouble(2));
            produit.setTVA1(c.getDouble(3));
            produit.setPV_HT(c.getDouble(4));
            produit.setPV_TTC(c.getDouble(5));
            produit.setCOLISAGE(c.getDouble(6));
            produit.setSTOCKG(c.getDouble(7));
            produit.setID(code);
            produit.setModele(c.getString(8));
            produit.setCouleur(c.getString(9));
            produit.setTaille(c.getString(10));
        }
        return produit;
    }

    public ArrayList<Product> getProducts(){
        c = db.rawQuery("SELECT ARTICLE,REF_ART FROM ARTICLE", null);
        ArrayList<Product> produits=new ArrayList<>();
        while (c.moveToNext()) {
            Product produit=new Product();
            produit.setArticle(c.getString(0));
            produit.setID(c.getString(1));
            produits.add(produit);
        }
        return produits;
    }

    //INSERER LES PRODUITS DANS LA BASE INTERNE
    public void createTable(String refArt,String lib,double col, double paht,double pattc,double pvht,double pvttc,double stock,double tva,String mod,String taille,String couleur) {
        String s=lib.replace("'","''");
        db.execSQL("INSERT INTO ARTICLE(REF_ART,ARTICLE,PA_HT,PA_TTC,TVA1,PV_HT,PV_TTC,COLISAGE,STOCKG,MODELE,TAILLE,COULEUR) VALUES " +
                    "('" +refArt + "','" + s + "','" + paht + "','" + pattc + "','" + tva + "','" + pvht + "','" + pvttc + "','"
                    + col + "','"+ stock+"','" +mod+ "','"+ taille +"','" + couleur+"')");
    }

    public void createCatTab(String refArt,int refcat,float pvteremise,float remise,float pvttc,float pvht,float pvhtremise) {
        db.execSQL("INSERT INTO ARTICLE_CATEGORIE(REF_ART,ref_categ,pvteremise,remise,pv_ttc,pv_ht,pv_htremise) VALUES " +
                    "('" +refArt + "','" + refcat + "','" + pvteremise + "','" + remise + "','" + pvttc +
                    "','" + pvht + "','" + pvhtremise+"')");

    }

    public void insertpath(String a,String b,String c,String d,String e,String f,String g){
        db.execSQL("INSERT INTO DBCONNECTION(DBNAME,IPADD,USER,PWD,NOM,ICE,MSG) VALUES " +
                "('" +a+ "','"+b+"','"+c+"','"+d+"','"+e+"','"+f+"','"+g+"')");

    }

    public void delART(){
        db.execSQL("DELETE FROM ARTICLE ");
        db.execSQL("DELETE FROM ARTICLE_CATEGORIE ");
    }

    public void delTXT(){
        db.execSQL("DELETE FROM Scans");
    }

    public String[] connexionpath(){
        String[] string= new String[7];
        c = db.rawQuery("SELECT DBNAME,IPADD,USER,PWD,NOM,ICE,MSG FROM DBCONNECTION ", null);
        while (c.moveToNext() ) {
            for(int i=0;i<7;i++) {
                string[i]=c.getString(i);
            }
        }
        return string;
    }

    //INSERTION
    public void insertScan(String id, int quantity,String designation,double pvht,double pvttc,double tva,int remise,String comID, String nom, String codeClt, String client){
        db.execSQL("INSERT INTO Scans(id,quantite,designation,pv_ht,pv_ttc,tva1,remise,comId,commer,codeclt,client) VALUES ('"+id+"','"+quantity+"','"+designation+"','"+pvht+"','"+pvttc+"','"+tva+"','"+remise+"','"+comID+"','"+nom+"','"+codeClt+"','"+client+"')");
    }

    public ArrayList<double[]> getCommands(){
        ArrayList<double[]> co=new ArrayList<>();
        c = db.rawQuery("SELECT tva1,sum(pv_ht*quantite),sum(pv_ttc*quantite)-sum(pv_ht*quantite),sum(pv_ttc*quantite) FROM Scans group by tva1", null);
        while (c.moveToNext() ) {
            double[] doubles=new double[4];
            doubles[0]=c.getDouble(0);
            doubles[1]=c.getDouble(1);
            doubles[2]=c.getDouble(2);
            doubles[3]=c.getDouble(3);
            co.add(doubles);
        }

        return co;

    }

    public double getTotal(){
        double a = 0;
        c = db.rawQuery("SELECT sum(quantite*pv_ttc) FROM Scans ", null);
        while (c.moveToNext() ) {
            a=c.getDouble(0);
        }
        return a;
    }

    public ArrayList<TXT> getScans(){
        ArrayList<TXT> scans=new ArrayList<>();
        c = db.rawQuery("SELECT id,quantite,designation,pv_ht,pv_ttc,tva1,remise,comId,commer,codeclt,client" +
                " FROM Scans", null);
        while (c.moveToNext() ) {
                TXT txt=new TXT(c.getString(0),c.getInt(1),c.getString(2),c.getDouble(3),c.getDouble(4),
                        c.getDouble(5),c.getFloat(6),c.getString(7),c.getString(8),
                        c.getString(9),c.getString(10));
                scans.add(txt);
        }
        return scans;
    }
    public void deletescan(String id){
        db.execSQL("DELETE FROM Scans WHERE id = '"+id+"'");
    }
    public void updatescan(String id, int quantity){
        db.execSQL("UPDATE Scans " +
                "SET quantite ="+quantity+" WHERE id = '"+id+"'");
    }

    public float[] getPV(String code,int ref){
        float[] pv=new float[3];
        if(ref!=0) {
            c = db.rawQuery("SELECT pvteremise,pv_htremise,remise FROM article_categorie where ref_art='" + code + "' and ref_categ=" + ref, null);
            while (c.moveToNext()) {
                pv[0] = c.getFloat(0);
                pv[1] = c.getFloat(1);
                pv[2] = c.getInt(2);
            }
        }else {
            c = db.rawQuery("SELECT PV_TTC,PV_HT FROM article where ref_art='" + code + "'", null);
            while (c.moveToNext()) {
                pv[0] = c.getFloat(0);
                pv[1] = c.getFloat(1);
                pv[2] = 0;
            }
        }
        return pv;
    }

}
