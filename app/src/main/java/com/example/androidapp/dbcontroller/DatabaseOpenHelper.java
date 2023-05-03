package com.example.androidapp.dbcontroller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="scanner.db";
    private static final int DATABASE_VERSION=1;
    public static final String TABLE_NAME="ARTICLE";


    public DatabaseOpenHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        SQLiteDatabase db=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE ARTICLE(" +
                "REF_ART TEXT," +
                "ARTICLE TEXT, " +
                "PA_HT REAL," +
                "PA_TTC REAL," +
                "TVA1 REAL," +
                "PV_HT REAL," +
                "PV_TTC REAL," +
                "COLISAGE REAL," +
                "STOCKG REAL," +
                "MODELE TEXT," +
                "TAILLE TEXT," +
                "COULEUR TEXT) ");

        db.execSQL("CREATE TABLE article_categorie(" +
                "ref_art TEXT," +
                "ref_categ int," +
                "pvteremise REAL," +
                "remise REAL," +
                "pv_ttc REAL," +
                "pv_ht REAL," +
                "pv_htremise REAL)");


        db.execSQL("CREATE TABLE Scans(" +
                "id TEXT," +
                "quantite int," +
                "designation TEXT," +
                "pv_ht REAL," +
                "pv_ttc REAL," +
                "remise REAL," +
                "tva1 REAL," +
                "comId TEXT," +
                "commer TEXT," +
                "codeclt TEXT," +
                "client TEXT) ");

        db.execSQL("CREATE TABLE DBCONNECTION(" +
                "DBNAME TEXT," +
                "IPADD TEXT," +
                "USER TEXT," +
                "PWD TEXT," +
                "NOM TEXT," +
                "ICE TEXT," +
                "MSG TEXT) ");

        db.execSQL("INSERT INTO DBCONNECTION(DBNAME,IPADD,USER,PWD,NOM,ice,msg) VALUES " +
                "('DATA_ARBO','192.168.1.122','ATTIJARI2013','VOIE_2013','TEST','ICE','TEXT')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);

        onCreate(db);
    }
}
