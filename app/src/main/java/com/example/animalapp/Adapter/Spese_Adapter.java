package com.example.animalapp.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animalapp.Model.Spesa;
import com.example.animalapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Spese_Adapter extends RecyclerView.Adapter<Spese_Adapter.SpeseViewHolder>{
    final private Context mCtx;

    final private List<Spesa> speseList;


    public Spese_Adapter(Context mCtx, List<Spesa> aList){
        this.mCtx = mCtx;
        this.speseList = aList;
    }

    @NonNull
    @Override
    public SpeseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.spesa,parent,false);
        return new Spese_Adapter.SpeseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpeseViewHolder holder, int position) {
        final Spesa spesa = speseList.get(position);
        holder.nameView.setText(spesa.spesa);
        holder.prezzoSpesa.setText(spesa.prezzo);
        holder.dataSpesa.setText(spesa.date);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(holder.delete.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("ELIMINAZIONE SPESA").setMessage("Sei sicuro di voler eliminare?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference reference;
                                FirebaseDatabase database = FirebaseDatabase.getInstance("https://provalogin-65cb5-default-rtdb.europe-west1.firebasedatabase.app/");
                                reference = database.getReference().child("Spese").child(spesa.id);
                                reference.removeValue();
                                notifyItemRemoved(holder.getAdapterPosition());

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return speseList.size();
    }

    static class SpeseViewHolder extends RecyclerView.ViewHolder{
        TextView nameView;
        TextView prezzoSpesa;

        TextView dataSpesa;
        Button delete;

        public SpeseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.tipoSpesa);
            prezzoSpesa = itemView.findViewById(R.id.PrezzoSpesa);
            dataSpesa = itemView.findViewById(R.id.DataSpesa);
            delete = itemView.findViewById(R.id.cancellaSpesa);

        }
    }
}
