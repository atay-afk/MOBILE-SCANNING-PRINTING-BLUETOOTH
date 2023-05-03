package com.example.androidapp.layouts;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.beans.TXT;
import com.example.androidapp.dbcontroller.DatabaseAccess;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    static String string="appairer";
    double total=0;
    Dialog myDialog;
    EditText edit;
    DecimalFormat df = new DecimalFormat("0.00");

    public static ArrayList<TXT> list;

    public MyAdapter(Context context,ArrayList<TXT> list){
        this.context=context;
        this.list=list;
        this.myDialog=new Dialog(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.articles,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TXT article= list.get(position);

        holder.design.setText(article.getDesignation());
        holder.quantite.setText(String.valueOf(article.getQuantite()));
        holder.code.setText(article.getId());
        holder.prixUnit.setText(df.format(article.getPvttc()));
        holder.sum.setText(df.format(article.getPvttc()*article.getQuantite()));

        total=DatabaseAccess.getInstance(context).getTotal();
        PrintBluetooth.setTotal(total);

        holder.quantite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup(holder.quantite, article,holder.sum);
            }
        });

        holder.moins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a;
                a=Integer.parseInt(holder.quantite.getText().toString())-1;
                holder.quantite.setText(String.valueOf(a));
                DatabaseAccess.getInstance(context).updatescan(article.getId(),a);
                article.setQuantite(a);
                total-= article.getPvttc();
                PrintBluetooth.setTotal(total);
                holder.sum.setText(df.format(article.getPvttc()*article.getQuantite()));
            }
        });
        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a;
                a=Integer.parseInt(holder.quantite.getText().toString())+1;
                holder.quantite.setText(String.valueOf(a));
                DatabaseAccess.getInstance(context).updatescan(article.getId(),a);
                article.setQuantite(a);
                total+= article.getPvttc();
                PrintBluetooth.setTotal(total);
                holder.sum.setText(df.format(article.getPvttc()*article.getQuantite()));
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(),list.size());
                DatabaseAccess.getInstance(context).deletescan(article.getId());
            }
        });


    }

    private void popup(TextView textView,TXT article,TextView text) {
        myDialog.setContentView(R.layout.setquantity);
        edit=myDialog.findViewById(R.id.setquantity);

        showKeyboard();
        edit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String q=edit.getText().toString();
                    textView.setText(q);
                    article.setQuantite(Integer.parseInt(q));
                    text.setText(df.format(article.getPvttc()*article.getQuantite()));
                    DatabaseAccess.getInstance(context).updatescan(article.getId(),Integer.parseInt(q));
                    total=DatabaseAccess.getInstance(context).getTotal();
                    PrintBluetooth.setTotal(total);
                    myDialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        myDialog.show();
    }
    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView code,design,quantite,prixUnit,sum;
        ImageView delete,moins,plus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            design=itemView.findViewById(R.id.designation);
            code=itemView.findViewById(R.id.code);
            quantite=itemView.findViewById(R.id.qtn);
            delete=itemView.findViewById(R.id.delete);
            moins=itemView.findViewById(R.id.moins);
            plus=itemView.findViewById(R.id.plus);
            prixUnit=itemView.findViewById(R.id.prixUnit);
            sum=itemView.findViewById(R.id.sum);


        }
    }

}
