package com.example.androidapp.layouts;


import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidapp.R;
import com.example.androidapp.beans.Capture;
import com.example.androidapp.beans.Client;
import com.example.androidapp.beans.Commercial;
import com.example.androidapp.beans.Product;
import com.example.androidapp.beans.TXT;
import com.example.androidapp.dbcontroller.ConnectionHelper;
import com.example.androidapp.dbcontroller.DBAccess;
import com.example.androidapp.dbcontroller.DatabaseAccess;
import com.google.android.material.textfield.TextInputLayout;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    Handler objHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg){
            super.handleMessage(msg);
            progress=findViewById(R.id.progress);
            text=findViewById(R.id.text);
            if(!ConnectionHelper.error.equals("")) {
                text.setText("Chargement non reussi !!");
            }else{
                text.setText("Chargement terminé !!");
            }
            progress.setVisibility(View.GONE);
        }
    };

    //Initialize variable
    EditText mdp,edit;
    Button login,delete,traiter;
    TextView error;
    Button btconf,charger,btn;
    ProgressBar progress;
    TextView text;
    ImageView img,imgScan,bluetooth,clear,search;
    Dialog myDialog;
    EditText dbname,ip,username,pwd,nom,ice,soustext;
    CheckBox csv;

    DatabaseAccess databaseAccess;

    TextInputLayout til,til2;
    AutoCompleteTextView act,act2;

    ArrayList<String> arrayList,arrayList2;
    ArrayAdapter<String> arrayAdapter,arrayAdapter2;
    SearchAdapter searchAdapter;
    ArrayList<Commercial> commercials=new ArrayList<>();
    ArrayList<Client> clients=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDialog=new Dialog(this);

        til=findViewById(R.id.til);
        act=findViewById(R.id.act);
        til2=findViewById(R.id.til2);
        act2=findViewById(R.id.act2);
        //Assign variable
        imgScan=findViewById(R.id.imageView);
        progress=findViewById(R.id.progress);
        text=findViewById(R.id.text);
        img=findViewById(R.id.param);
        charger=findViewById(R.id.charger);
        edit=findViewById(R.id.edit_query);
        btn= findViewById(R.id.print);
        csv=findViewById(R.id.checkboxCSV);
        clear=findViewById(R.id.clear);
        search=findViewById(R.id.search);

//
//
        databaseAccess=DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        bluetooth=findViewById(R.id.bluetooth);

        if(Product.zone!=null && Product.zone2!=null){
            act.setText(Product.zone,false);
            act2.setText(Product.zone2,false);
        }

// Commercial
        arrayList=new ArrayList<>();
        try{
            commercials= DBAccess.getCommer();
            for(Commercial commercial:commercials){
                arrayList.add(commercial.getNom());
            }
            arrayAdapter=new ArrayAdapter<>(getApplicationContext(),R.layout.tv_entity,arrayList);
            act.setAdapter(arrayAdapter);
// Client
            arrayList2=new ArrayList<>();
            clients=DBAccess.getClient();
            for(Client client:clients){
            arrayList2.add(client.getNom());
        }
            arrayAdapter2=new ArrayAdapter<>(getApplicationContext(),R.layout.tv_entity,arrayList2);
            act2.setAdapter(arrayAdapter2);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"CONNEXION AU SERVEUR ECHOUEE",Toast.LENGTH_SHORT).show();
        }

        edit.requestFocus();

        if(!DatabaseAccess.getInstance(getApplicationContext()).getScans().isEmpty() && Product.zone==null){
            popupCheck(this);
        }

        search.setOnClickListener(view -> {
                    if (!(act.getText().length() == 0) && !(act2.getText().length() == 0)) {
                            Product.zone = act.getText().toString();
                            Product.zone2 = act2.getText().toString();
                            // selection commer/client
                            for (Commercial commercial : commercials) {
                                if (commercial.getNom().equals(act.getText().toString())) {
                                    Commercial.instance = commercial;
                                }
                            }
                            for (Client client : clients) {
                                if (client.getNom().equals(act2.getText().toString())) {
                                    Client.instance = client;
                                }
                            }
                        popupSearch(this);
                    } else
                        Toast.makeText(getApplicationContext(), "SELECTIONNEZ CLIENT OU COMMERCIALE !!", Toast.LENGTH_LONG).show();

        });

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product.zone=act.getText().toString();
                Product.zone2=act2.getText().toString();
                for(Commercial commercial:commercials){
                    if(commercial.getNom().equals(act.getText().toString())){
                        Commercial.instance=commercial;
                    }
                }
                for(Client client:clients){
                    if(client.getNom().equals(act2.getText().toString())){
                        Client.instance=client;
                    }
                }
                openPrintBluetooth(MainActivity.this);
                finish();
            }
        });

        // effacer text
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit.setText("");
            }
        });

        edit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    String a = edit.getText().toString().trim();
                    if(!(act.getText().length() ==0) && !(act2.getText().length() ==0)) {
                        if (a.isEmpty()) {
                            edit.setError("Saisissez un code!");
                        } else {
                            Product.zone = act.getText().toString();
                            Product.zone2 = act2.getText().toString();
                            // selection commer/client
                            for (Commercial commercial : commercials) {
                                if (commercial.getNom().equals(act.getText().toString())) {
                                    Commercial.instance = commercial;
                                }
                            }
                            for (Client client : clients) {
                                if (client.getNom().equals(act2.getText().toString())) {
                                    Client.instance = client;
                                }
                            }


                            DBAccess.list = new ArrayList<>();
                            Product.setInstance(databaseAccess.getProduct(a));

                            if (Product.getInstance().getArticle() != null) {
                                float[] pv = databaseAccess.getPV(a, Client.instance.getCategorie());
                                Product.instance.setPV_TTC(pv[0]);
                                Product.instance.setPV_HT(pv[1]);
                                Product.instance.setRemise((int) pv[2]);
                                openPrintActivity(MainActivity.this);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "ARTICLE N'EXISTE PAS", Toast.LENGTH_LONG).show();
                            }
                        }
                    }else
                        Toast.makeText(getApplicationContext(),"SELECTIONNEZ CLIENT OU COMMERCIALE !!", Toast.LENGTH_LONG).show();


                    return true;
                }
                return false;
            }

        });

        imgScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(act.getText().length() ==0) && !(act2.getText().length() ==0)) {
                    Product.zone = act.getText().toString();
                    Product.zone2 = act2.getText().toString();
                    scanCode();
                }
                else
                    Toast.makeText(getApplicationContext(),"SELECTIONNEZ CLIENT OU COMMERCIALE !!", Toast.LENGTH_LONG).show();

            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popuplogin(MainActivity.this);
            }
        });



        Dexter.withActivity(this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(csv.isChecked()){
                                saveToCSVFile();}
                                saveToTxtFile();

                                databaseAccess.delTXT();
                                Product.products.clear();

                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    private void scanCode() {
        ScanOptions options=new ScanOptions();
        options.setPrompt("Volum up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(Capture.class);
        barlauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barlauncher=registerForActivityResult(new ScanContract(), result ->{
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);


        DBAccess.list=new ArrayList<>();

        Product.setInstance(databaseAccess.getProduct(result.getContents()));

        for(Commercial commercial:commercials){
            if(commercial.getNom().equals(act.getText().toString())){
                Commercial.instance=commercial;
            }
        }
        for(Client client:clients){
            if(client.getNom().equals(act2.getText().toString())){
                Client.instance=client;
            }
        }

        float[] pv=databaseAccess.getPV(result.getContents(),Client.instance.getCategorie());
        Product.instance.setPV_TTC(pv[0]);
        Product.instance.setPV_HT(pv[1]);
        Product.instance.setRemise((int) pv[2]);

        if (Product.getInstance().getArticle()!=null) {
            openPrintActivity(MainActivity.this);
            finish();
        } else {
            builder.setTitle("ARTICLE N'EXISTE PAS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });

    private void openPrintBluetooth(Activity activity) {
        Intent intent=new Intent(activity, PrintBluetooth.class);
        activity.startActivity(intent);
    }

    public void buttonClicked(View v) {
        Runnable objRunnable = new Runnable() {
            @Override
            public void run() {

                String[] strings=databaseAccess.connexionpath();
                ConnectionHelper.setDbname(strings[0]);
                ConnectionHelper.setIp(strings[1]);
                ConnectionHelper.setPwd(strings[3]);
                ConnectionHelper.setUser(strings[2]);
                try {
                    DBAccess.setContext(getApplicationContext());
                    DBAccess.DBConnect1();
                    DBAccess.DBConnect2();
                }catch (Exception e){
                    System.out.println("hcdkyhdykjjkgfjhhhhjjjjjjjjjjjjjjjjjjjjjjjjjj");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                objHandler.sendEmptyMessage(0);
            }
        };
        progress.setVisibility(View.VISIBLE);
        text.setVisibility(View.VISIBLE);
        text.setText("Chargement de la base ");
        Thread objThread = new Thread(objRunnable);
        objThread.start();
    }

// POPUPS
    private void popupSearch(Activity a) {
        myDialog.setContentView(R.layout.searchpopup);
        ArrayList<Product> list=databaseAccess.getProducts();
        RecyclerView recyclerView=myDialog.findViewById(R.id.list);
        SearchView searchView=myDialog.findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText,list);
                return true;
            }
        });

        searchAdapter= new SearchAdapter(this, list,a);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchAdapter);
        myDialog.show();
    }

    private void filterList(String text, ArrayList<Product> list) {
        ArrayList<Product> filteredList=new ArrayList<>();
        for(Product p:list){
            System.out.println("*******"+p.getArticle());
            if(p.getArticle().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(p);
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(this,"Aucunne Article trouvée",Toast.LENGTH_SHORT).show();
        }else{
            searchAdapter.setFilteredList(filteredList);
        }
    }

    private void popupCheck(Activity a) {
        myDialog.setContentView(R.layout.check);
        delete=myDialog.findViewById(R.id.supp);
        traiter=myDialog.findViewById(R.id.traiter);

        delete.setOnClickListener((view) ->{
            DatabaseAccess.getInstance(getApplicationContext()).delTXT();
            myDialog.dismiss();
        });

        traiter.setOnClickListener((view) ->{
            openPrintBluetooth(a);
            finish();
        });

        myDialog.show();
    }


    private void popuplogin(Activity a) {
        myDialog.setContentView(R.layout.popuplogin);
        mdp=myDialog.findViewById(R.id.mdp);
        login=myDialog.findViewById(R.id.login);
        error=myDialog.findViewById(R.id.error);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mdp.getText().toString().equals("V0ie-123")) {
                    error.setVisibility(View.INVISIBLE);
                    popup(a);
                } else error.setVisibility(View.VISIBLE);
            }
        });

        myDialog.show();
    }


    private void popup(Activity a) {
        myDialog.setContentView(R.layout.popup);
        CheckBox checkBox;

        checkBox=myDialog.findViewById(R.id.checkbox);
        dbname=myDialog.findViewById(R.id.databasename);
        ip=myDialog.findViewById(R.id.ipadd);
        username=myDialog.findViewById(R.id.username);
        pwd=myDialog.findViewById(R.id.pwd);
        nom=myDialog.findViewById(R.id.nom);
        ice=myDialog.findViewById(R.id.ice);
        soustext=myDialog.findViewById(R.id.sous);
        btconf=myDialog.findViewById(R.id.conf);

        String[] strings=databaseAccess.connexionpath();
        ConnectionHelper.setDbname(strings[0]);
        ConnectionHelper.setIp(strings[1]);
        ConnectionHelper.setPwd(strings[3]);
        ConnectionHelper.setUser(strings[2]);

        dbname.setText(ConnectionHelper.dbname);
        ip.setText(ConnectionHelper.ip);
        username.setText(ConnectionHelper.user);
        pwd.setText(ConnectionHelper.pwd);
        nom.setText(strings[4]);
        ice.setText(strings[5]);
        soustext.setText(strings[6]);

        if(Product.PA) checkBox.setChecked(true);
        else checkBox.setChecked(false);


        btconf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox.isChecked()) Product.PA=true;
                else Product.PA=false;

                databaseAccess.insertpath(dbname.getText().toString().trim(),ip.getText().toString().trim(),
                        username.getText().toString().trim(),pwd.getText().toString().trim(),nom.getText().toString().trim(),
                        ice.getText().toString().trim(),soustext.getText().toString().trim());


                reload(a);
            }
        });


        myDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private static void reload(Activity activity) {
        Intent intent=new Intent(activity,MainActivity.class);
        activity.startActivity(intent);
    }

    private static void openPrintActivity(Activity activity) {
        Intent intent=new Intent(activity, PrintActivity.class);
        activity.startActivity(intent);
    }

    private void saveToTxtFile() {
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(System.currentTimeMillis());
        try{
            //path tp storage
            File path = Environment.getExternalStorageDirectory();
            //create folder named "My Files"
            File dir=new File(path+"/My Fils/");
            dir.mkdirs();
            //file name
            Product.zone=act.getText().toString();
            String fileName=Product.zone+"-TXT"+timeStamp+".txt";

            File file=new File(dir,fileName);

            //FileWriter class is used to store characters in file
            FileWriter fw=new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw=new BufferedWriter(fw);
            for(TXT txts : databaseAccess.getInstance(getApplicationContext()).getScans()) {
                bw.write(txts.getId());
                bw.write(";" + txts.getQuantite());
                bw.newLine();
            }
            bw.close();

            //show file name and path where file is saved
            Toast.makeText(this,fileName+" is saved to\n "+dir,Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    private void saveToCSVFile() {
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault())
                .format(System.currentTimeMillis());
        try{
            //path tp storage
            File path = Environment.getExternalStorageDirectory();
            //create folder named "My Files"
            File dir=new File(path+"/My Fils/");
            dir.mkdirs();
            //file name
            String fileName=Product.zone+"-CSV"+timeStamp+".csv";

            File file=new File(dir,fileName);


            //FileWriter class is used to store characters in file
            FileWriter fw=new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw=new BufferedWriter(fw);

            for(TXT txts : databaseAccess.getInstance(getApplicationContext()).getScans()) {
                bw.write(txts.getDesignation());
                bw.write(";");
                bw.write(String.valueOf(txts.getTva()));
                bw.write(";");
                bw.write(String.valueOf(txts.getPvth()));
                bw.write(";");
                bw.write(String.valueOf(txts.getPvttc()));
                bw.write(";");
                bw.write(String.valueOf(txts.getRemise()));
                bw.write(";");
                bw.write(String.valueOf(txts.getQuantite()));
                bw.write(";");

                bw.newLine();
            }
            bw.close();

            //show file name and path where file is saved
            Toast.makeText(this,fileName+" is saved to\n "+dir,Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }



}