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

import com.example.animalapp.Model.Cure;
import com.example.animalapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Cure_Adapter extends RecyclerView.Adapter<Cure_Adapter.CureViewHolder> {
    final private Context mCtx;

    final private List<Cure> cureList;


    public Cure_Adapter(Context mCtx, List<Cure> aList){
        this.mCtx = mCtx;
        this.cureList = aList;
    }

    @NonNull
    @Override
    public Cure_Adapter.CureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.cure,parent,false);
        return new Cure_Adapter.CureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CureViewHolder holder, int position) {
        final Cure cure = cureList.get(position);
        holder.nameView.setText(cure.cura);
        holder.prezzoSpesa.setText(cure.prezzo);
        holder.dataSpesa.setText(cure.date);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(holder.delete.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("ELIMINAZIONE CURA").setMessage("Sei sicuro di voler eliminare?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference reference;
                                FirebaseDatabase database = FirebaseDatabase.getInstance("https://provalogin-65cb5-default-rtdb.europe-west1.firebasedatabase.app/");
                                reference = database.getReference().child("Cure").child(cure.id);
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
        return cureList.size();
    }

    static class CureViewHolder extends RecyclerView.ViewHolder{
        TextView nameView;
        TextView prezzoSpesa;
        TextView dataSpesa;
        Button delete;

        public CureViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.tipoSpesa);
            prezzoSpesa = itemView.findViewById(R.id.PrezzoSpesa);
            dataSpesa = itemView.findViewById(R.id.DataSpesa);
            delete = itemView.findViewById(R.id.cancellaSpesa);

        }
    }

}
