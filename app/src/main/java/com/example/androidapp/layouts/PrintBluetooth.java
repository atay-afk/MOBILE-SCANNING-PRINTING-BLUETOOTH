package com.example.androidapp.layouts;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidapp.R;
import com.example.androidapp.beans.Product;
import com.example.androidapp.beans.TXT;
import com.example.androidapp.dbcontroller.DBAccess;
import com.example.androidapp.dbcontroller.DatabaseAccess;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.printable.ImagePrintable;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.RawPrintable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;
import com.mazenrashed.printooth.utilities.PrintingCallback;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PrintBluetooth extends AppCompatActivity implements PrintingCallback {


    static Printing printing;
    Button bluetooth,save,back,pair;
    RecyclerView recyclerView;
    static TextView total;
    MyAdapter myAdapter;
    float a,b;
    String timeStamp=new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            .format(System.currentTimeMillis());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_bluetooth);
        initView();

        total=findViewById(R.id.total);
        recyclerView=findViewById(R.id.list);
        save=findViewById(R.id.save);
        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity(PrintBluetooth.this);
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product.zone2="";
                try {
                for(TXT txts : DatabaseAccess.getInstance(getApplicationContext()).getScans()) {
                    DBAccess.insertCommande(timeStamp, txts.getComId(), txts.getCommer(), txts.getCodeclt(), txts.getClient(), txts.getId(),
                            txts.getDesignation(), txts.getQuantite(), txts.getPvth(), txts.getPvttc(), txts.getTva(), b, a, txts.getRemise());
                    }
                    DatabaseAccess.getInstance(getApplicationContext()).delTXT();
                    Product.products.clear();
                    openMainActivity(PrintBluetooth.this);
                    finish();
                }catch (Exception e){
                    System.out.println("Enter first number: ");
                    Toast.makeText(getApplicationContext(),"CONNEXION AU SERVEUR ECHOUEE",Toast.LENGTH_SHORT).show();
                }
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                openMainActivity(PrintBluetooth.this);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        DatabaseAccess.getInstance(getApplicationContext()).open();
        if(DatabaseAccess.getInstance(getApplicationContext()).getScans().isEmpty()){
            Toast.makeText(this,"VOUS AVEZ RIEN SCANNE !!",Toast.LENGTH_SHORT).show();
        }
        else {
            ArrayList<TXT> list = DatabaseAccess.getInstance(getApplicationContext()).getScans();
            Product.zone=list.get(0).getCommer();
            Product.zone2=list.get(0).getClient();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            myAdapter = new MyAdapter(this, list);
            recyclerView.setAdapter(myAdapter);
        }
    }

    public static void setTotal(double a){
        DecimalFormat df = new DecimalFormat("0.00");
        total.setText(df.format(a));
    }

    private void initView() {
        bluetooth=findViewById(R.id.imprimer);
        pair=findViewById(R.id.pair);
        pair.setText(myAdapter.string);
        if(pair.getText().equals("desappairer"))
            pair.setBackgroundColor(Color.RED);

        if(printing!=null)
            printing.setPrintingCallback(this);

        pair.setOnClickListener(view -> {
            if(Printooth.INSTANCE.hasPairedPrinter()) {
                Printooth.INSTANCE.removeCurrentPrinter();
                startActivityForResult(new Intent(PrintBluetooth.this, ScanningActivity.class), ScanningActivity.SCANNING_FOR_PRINTER);
                changePairAndUnpair();
            }
            else
            {
                startActivityForResult(new Intent(PrintBluetooth.this,ScanningActivity.class),ScanningActivity.SCANNING_FOR_PRINTER);
                changePairAndUnpair();
            }
        });
        bluetooth.setOnClickListener(view ->{
            if(printing!=null)
                printText();
            else
                Toast.makeText(this,"CONNECTEZ UNE IMPRIMANTE !!",Toast.LENGTH_SHORT).show();

        });
    }

    private static void openMainActivity(Activity activity) {
        Intent intent=new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    private void changePairAndUnpair() {
        if(Printooth.INSTANCE.hasPairedPrinter()){
            myAdapter.string="desappairer";
            pair.setBackgroundColor(Color.RED);
            pair.setText(new StringBuilder("desappairer"));
        }
        //nom d imprimante Printooth.INSTANCE.getPairedPrinter().getName()).toString()
        /*else
            pair.setText("Pair with Printer");*/
    }

    private void printText() {
        ArrayList<Printable> printables=new ArrayList<>();
        printables.add(new RawPrintable.Builder(new byte[]{27,100,4}).build());
        // add text
        double sum=0,sum1=0,sum2=0;
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat dff = new DecimalFormat("0");

        String[] strings=DatabaseAccess.getInstance(getApplicationContext()).connexionpath();

        printables.add(new TextPrintable.Builder()
                .setText(strings[4]).setFontSize((byte) 1)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .build());
        printables.add(new TextPrintable.Builder()
                .setText("ICE :"+strings[5])
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("COMMANDE")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());
//
        printables.add(new TextPrintable.Builder()
                .setText(" Commerciale: "+Product.zone)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText(" Client: "+Product.zone2)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText(" Date: "+new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                        .format(System.currentTimeMillis()))
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());
//
        printables.add(new TextPrintable.Builder()
                .setText("--------------------------------")
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());

        for(TXT txts : DatabaseAccess.getInstance(getApplicationContext()).getScans()) {
            printables.add(new TextPrintable.Builder()
                    .setText(" "+txts.getId()+". "+txts.getDesignation())
                    .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                    .setNewLinesAfter(1)
                    .build());
            a= (float) (txts.getPvttc()* txts.getQuantite());
            b=(float)(txts.getPvth()*txts.getQuantite());
            sum+=a;
            printables.add(new TextPrintable.Builder()
                    .setText(" "+String.format("%02d",txts.getQuantite()) +" *     "+df.format(txts.getPvttc())+" =    "+df.format(a))
                    .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                    .setNewLinesAfter(1)
                    .build());


        }
//
        printables.add(new TextPrintable.Builder()
                .setText("--------------------------------")
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText("TOTAL       "+df.format(sum)).setFontSize((byte) 1)
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        //barcode
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(timeStamp, BarcodeFormat.CODE_128, 300, 100);
            Bitmap bitmap = Bitmap.createBitmap(300, 100, Bitmap.Config.RGB_565);
            for (int i = 0; i < 300; i++) {
                for (int j = 0; j < 100; j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            printables.add(new ImagePrintable.Builder(bitmap)
                    .setNewLinesAfter(1)
                    .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                    .build());

        } catch (WriterException e) {
            e.printStackTrace();
        }

        printables.add(new TextPrintable.Builder()
                .setText(timeStamp)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("--------------------------------")
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText(" Taux   H.T     TVA.     TTC")
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText(" ----   ----    ----    ----")
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());

        ArrayList<double[]> doubles=DatabaseAccess.getInstance(getApplicationContext()).getCommands();
        for(double[] d:doubles) {
            if(d[0]==0){
                printables.add(new TextPrintable.Builder()
                        .setText(" " + dff.format(d[0]) + "  " + df.format(d[1]) + "  " + df.format(d[2]) + "   " + df.format(d[3]))
                        .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                        .setNewLinesAfter(1)
                        .build());
            }
            else {
                printables.add(new TextPrintable.Builder()
                        .setText(" " + dff.format(d[0]) + "  " + df.format(d[1]) + "  " + df.format(d[2]) + "  " + df.format(d[3]))
                        .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                        .setNewLinesAfter(1)
                        .build());
            }
            sum1+=d[1];
            sum2+=d[3];
        }

        printables.add(new TextPrintable.Builder()
                .setText("     "+df.format(sum1)+"  "+df.format(sum2-sum1)+"  "+df.format(sum2))
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("--------------------------------")
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText(strings[6])
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());

        printables.add(new TextPrintable.Builder()
                .setText("--------------------------------")
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setNewLinesAfter(1)
                .build());
        printables.add(new TextPrintable.Builder()
                .setText("").setNewLinesAfter(1)
                .build());


        printing.print(printables);
    }


    @Override
    public void connectingWithPrinter() {
        Toast.makeText(this,"Connexion a l'imprimante",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void connectionFailed(String s) {
        Toast.makeText(this,"CONNEXION A L'IMPRIMANTE ECHOUEE !!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String s) {
        Toast.makeText(this,"Error: "+s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessage(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void printingOrderSentSuccessfully() {
        Toast.makeText(this,"COMMANDE ENVOYEE A L'IMPRIMANTE",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ScanningActivity.SCANNING_FOR_PRINTER && resultCode== Activity.RESULT_OK)
            initPrinting();
        changePairAndUnpair();
    }
    private void initPrinting() {
        if(Printooth.INSTANCE.hasPairedPrinter())
            printing=Printooth.INSTANCE.printer();
        if(printing!=null)
            printing.setPrintingCallback(this);
    }
}