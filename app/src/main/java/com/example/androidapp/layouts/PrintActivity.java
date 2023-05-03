package com.example.androidapp.layouts;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.R;
import com.example.androidapp.beans.Client;
import com.example.androidapp.beans.Commercial;
import com.example.androidapp.beans.Product;
import com.example.androidapp.dbcontroller.DatabaseAccess;

import java.text.DecimalFormat;


public class PrintActivity extends AppCompatActivity {

    private Button bt_add;
    private TextView ref,article,paht,pattc,tva1,pvht,pvttc,colisage,stockg,modele,taille,couleur;
    private EditText quantite;
    private GridLayout grid;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        ref=findViewById(R.id.ref_a);
        article=findViewById(R.id.article);
        stockg= findViewById(R.id.stockg);
        bt_add=findViewById(R.id.add);
        grid=findViewById(R.id.grid);

        paht= findViewById(R.id.pa_ht);
        pattc= findViewById(R.id.pattc);
        tva1= findViewById(R.id.tva1);

        pvht= findViewById(R.id.pvht);
        pvttc= findViewById(R.id.pvttc);
        colisage= findViewById(R.id.colisage);
        modele=findViewById(R.id.modele);
        taille=findViewById(R.id.taille);
        couleur=findViewById(R.id.couleur);

        quantite=findViewById(R.id.qnt);


        if(Product.PA) grid.setVisibility(View.VISIBLE);
        else grid.setVisibility(View.GONE);

        DecimalFormat df = new DecimalFormat("0.00");
        //Set message

        ref.setText(Product.instance.getID());
        article.setText(Product.instance.getArticle());
        stockg.setText(String.valueOf(Product.instance.getSTOCKG()));
        paht.setText(String.valueOf(df.format(Product.instance.getPA_HT())));
        pattc.setText(String.valueOf(df.format(Product.instance.getPA_TTC())));
        tva1.setText(String.valueOf(Product.instance.getTVA1()));
        pvht.setText(String.valueOf(df.format(Product.instance.getPV_HT())));
        pvttc.setText(String.valueOf(df.format(Product.instance.getPV_TTC())));
        colisage.setText(String.valueOf(Product.instance.getCOLISAGE()));
        modele.setText(Product.instance.getModele());
        taille.setText(Product.instance.getTaille());
        couleur.setText(Product.instance.getCouleur());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                openMainActivity(PrintActivity.this);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantite.getText().toString().trim().isEmpty())
                Toast.makeText(getApplicationContext(), "ENTREZ UNE QUANTITE !!", Toast.LENGTH_SHORT).show();
                else{
                    Product.setProducts(Integer.parseInt(String.valueOf(quantite.getText())));
                    Toast.makeText(getApplicationContext(), "Produit enregistré", Toast.LENGTH_SHORT).show();
                    DatabaseAccess.getInstance(getApplicationContext()).insertScan(String.valueOf(Product.instance.getID()),Integer.parseInt(quantite.getText().toString()),
                            Product.instance.getArticle(),
                            Product.instance.getPV_HT(),
                            Product.instance.getPV_TTC(),
                            Product.instance.getTVA1(),
                            Product.instance.getRemise(),
                            Commercial.instance.getId(),
                            Commercial.instance.getNom(),
                            Client.instance.getCode(),
                            Client.instance.getNom());

                    quantite.setText("");
                    openMainActivity(PrintActivity.this);
                    finish();
                }
            }
        });
    }

    private static void openMainActivity(Activity activity) {
        Intent intent=new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
