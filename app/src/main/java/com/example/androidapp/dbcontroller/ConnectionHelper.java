package com.example.androidapp.dbcontroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionHelper {
    public static String ip;
    public static String dbname;
    public static String user;
    public static String pwd;

    public static String error="";
    public static void setIp(String a) {
        ConnectionHelper.ip = a;
    }

    public static void setDbname(String a) {
        ConnectionHelper.dbname = a;
    }

    public static String getDbname() {
        return dbname;
    }

    public static void setUser(String a) {
        ConnectionHelper.user = a;
    }

    public static void setPwd(String a) {
        ConnectionHelper.pwd = a;
    }

    @SuppressLint("NewApi")
    public Connection connectionclass(Context c) throws ClassNotFoundException, SQLException {

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection connection=null;
        String ConnectionURL;
        Class.forName("net.sourceforge.jtds.jdbc.Driver");
        String[] strings=DatabaseAccess.getInstance(c).connexionpath();
        ConnectionURL= "jdbc:jtds:sqlserver://"+strings[1]+";databaseName="+strings[0]+";user="+strings[2]+";password="+strings[3]+"; ";
        connection= DriverManager.getConnection(ConnectionURL);

        return connection;

    }
}
