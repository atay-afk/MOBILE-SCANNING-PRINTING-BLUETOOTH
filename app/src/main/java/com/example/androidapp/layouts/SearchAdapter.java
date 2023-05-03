package com.example.androidapp.layouts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.beans.Client;
import com.example.androidapp.beans.Commercial;
import com.example.androidapp.beans.Product;
import com.example.androidapp.beans.TXT;
import com.example.androidapp.dbcontroller.DBAccess;
import com.example.androidapp.dbcontroller.DatabaseAccess;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    Context context;
    double total=0;
    Activity a;

    public static ArrayList<Product> list;

    public SearchAdapter(Context context,ArrayList<Product> list,Activity a){
        this.context=context;
        this.list=list;
        this.a=a;

    }

    public void setFilteredList(ArrayList<Product> filteredList){
        this.list=filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.designation,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product article= list.get(position);

        holder.design.setText(article.getArticle());
        holder.code.setText(article.getID());

        holder.layout.setOnClickListener((view -> {
            DBAccess.list = new ArrayList<>();
            Product.setInstance(DatabaseAccess.getInstance(context).getProduct(article.getID()));
            float[] pv = DatabaseAccess.getInstance(context).getPV(article.getID(), Client.instance.getCategorie());
            Product.instance.setPV_TTC(pv[0]);
            Product.instance.setPV_HT(pv[1]);
            Product.instance.setRemise((int) pv[2]);
            openPrintActivity(a);
            a.finish();
        }));


    }

    private static void openPrintActivity(Activity activity) {
        Intent intent=new Intent(activity, PrintActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView code,design;
        LinearLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            design=itemView.findViewById(R.id.designation);
            code=itemView.findViewById(R.id.code);
            layout=itemView.findViewById(R.id.layout);
        }
    }

}
