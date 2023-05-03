package com.example.androidapp.dbcontroller;

import android.content.Context;
import android.util.Log;

import com.example.androidapp.beans.Client;
import com.example.androidapp.beans.Commercial;
import com.example.androidapp.dbcontroller.ConnectionHelper;
import com.example.androidapp.dbcontroller.DatabaseAccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBAccess {

    public static ArrayList<String> list = new ArrayList<>();
    public static Context a;


    public static void setContext(Context c){
        a=c;
    }

    public static void DBConnect1() {
        try {
            Connection connect;
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.connectionclass(a);
            if (connect != null) {
                String query1 = "Select ARTICLE.REF_ART,ARTICLE.LIBCAISSE,ARTICLE.PA_HT," +
                        "ARTICLE.PA_TTC,ARTICLE.TVA1,ARTICLE.PV_HT,ARTICLE.PV_TTC," +
                        "ARTICLE.COLISAGE,ARTICLE.STOCKG,ARTICLE.MODELE,ARTICLE.TAILLE,ARTICLE.COULEUR FROM ARTICLE INNER JOIN " +
                        "ARTICLE_CODEBARRE ON ARTICLE.REF_ART = ARTICLE_CODEBARRE.REF_ART ";
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query1);
                DatabaseAccess databaseAccess=DatabaseAccess.getInstance(a);
                databaseAccess.open();
                databaseAccess.delART();
                while (rs.next()) {
                    databaseAccess.createTable(rs.getString("REF_ART"),rs.getString("LIBCAISSE"),
                            rs.getDouble("COLISAGE"),rs.getDouble("PA_HT"),rs.getDouble("PA_TTC"),
                            rs.getDouble("PV_HT"),rs.getDouble("PV_TTC"),rs.getDouble("STOCKG"),
                            rs.getDouble("TVA1"),rs.getString("MODELE"),rs.getString("TAILLE"),
                            rs.getString("COULEUR"));

                }

                String q = "Select REF_ART from ARTICLE";
                st = connect.createStatement();
                rs = st.executeQuery(q);
                while (rs.next()) {
                    list.add(rs.getString(1));
                }

            }
        } catch (Exception ex) {
            Log.e("error", ex.getMessage());
            ConnectionHelper.error="error";
        }
    }


    public static void DBConnect2() {
        try {
            Connection connect;
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.connectionclass(a);
            if (connect != null) {
                String query1 = "Select ref_art,ref_categ,PVTEREMISE,remise,pv_ttc,pv_ht,pv_htremise FROM article_categorie";
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query1);
                while (rs.next()) {
                    DatabaseAccess databaseAccess=DatabaseAccess.getInstance(a);
                    databaseAccess.open();
                    databaseAccess.createCatTab(rs.getString("ref_art"), rs.getInt("ref_categ"),
                            rs.getFloat("pvteremise"), rs.getFloat("remise"),rs.getFloat("pv_ttc"),
                            rs.getFloat("pv_ht"),rs.getFloat("pv_htremise"));

                }

            }
        } catch (Exception ex) {
            Log.e("error", ex.getMessage());
            ConnectionHelper.error="error";
        }
    }

    public static ArrayList<Commercial> getCommer() throws SQLException, ClassNotFoundException {
        ArrayList<Commercial> commercials = new ArrayList<>();
            Connection connect;
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.connectionclass(a);
            commercials = new ArrayList<>();
            if (connect != null) {
                String query1 = "Select ref_commercial,Nom FROM Commercial";
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query1);
                while (rs.next()) {
                    commercials.add(new Commercial(rs.getString("ref_commercial"), rs.getString("Nom")));

                }
            }

        return commercials;
    }


    public static ArrayList<Client> getClient() throws SQLException, ClassNotFoundException {
        ArrayList<Client> clients = new ArrayList<>();
            Connection connect;
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.connectionclass(a);
            clients = new ArrayList<>();
            if (connect != null) {
                String query1 = "Select code_clt,client,tel,ref_categ from client";
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query1);
                while (rs.next()) {
                    clients.add(new Client(rs.getString("code_clt"),rs.getString("client"),rs.getString("tel"),rs.getInt("ref_categ")));

                }
            }

        return clients;
    }


    public static void insertCommande(String nucmd, String comID, String nom, String codeClt, String client, String refArt,
                                      String lib, int qnt, double pvht, double pvttc, double tva, float mntht, float mntttc, double remise) throws SQLException, ClassNotFoundException {
            Connection connect;
            ConnectionHelper connectionHelper = new ConnectionHelper();
            String dbname = ConnectionHelper.getDbname();
            ConnectionHelper.setDbname("DataCaisse");
            connect = connectionHelper.connectionclass(a);
            if (connect != null) {
                String query1 = "INSERT INTO Tab_Commande(Num_Cmd,Commercial_ID,Nom,Date_cmd,Code_clt," +
                        "Client,Ref_art,libcaisse,qte,Pv_ht,Pv_ttc,Tva,Mnt_ht,Mnt_ttc,Remise) VALUES('"+nucmd+"','"+
                        comID+"','"+nom+"',getdate(),'"+codeClt+"','"+client+"','"+refArt+"','"+lib+"',"+qnt+","+pvht+
                        ","+pvttc+","+tva+","+mntht+","+mntttc+","+remise+")";
                Statement st = connect.createStatement();
                st.executeUpdate(query1);

            }
    }

}
