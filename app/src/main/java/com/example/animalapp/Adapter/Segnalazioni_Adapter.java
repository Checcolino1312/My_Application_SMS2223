package com.example.animalapp.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animalapp.Model.Segnalazioni;
import com.example.animalapp.Model.Utente;
import com.example.animalapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class Segnalazioni_Adapter extends RecyclerView.Adapter<Segnalazioni_Adapter.SegnalazioniViewHolder> {
    final private Context mCtx;
    final private List<Segnalazioni> segnalazioniList;



    public Segnalazioni_Adapter(Context mCtx, List<Segnalazioni> segnalazioniList){
        this.mCtx = mCtx;
        this.segnalazioniList = segnalazioniList;
    }

    @NonNull
    @Override
    public SegnalazioniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.segnalazioni_item,parent,false);
        return new SegnalazioniViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SegnalazioniViewHolder holder, int position) {
        final Segnalazioni segnalazioni = segnalazioniList.get(position);



        holder.descrizione.setText(segnalazioni.descrizione);
        holder.tipologiaSegnalazione.setText(segnalazioni.tipologiaSegnalazione);
        trovaNomeCognomeUtente(segnalazioni.idMittente, holder.mittente);

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(segnalazioni.imgSegnalazione);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.img.getContext())
                        .load(uri).circleCrop()
                        /*.placeholder(com.firebase.ui.storage.R.drawable.common_google_signin_btn_icon_dark)
                        .circleCrop()
                        .error(com.firebase.ui.storage.R.drawable.common_google_signin_btn_icon_dark_normal)*/
                        .into(holder.img);
            }
        });

    }

    @Override
    public int getItemCount() {
        return segnalazioniList.size();
    }




    static class SegnalazioniViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView tipologiaSegnalazione,descrizione,mittente;

        public SegnalazioniViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img1);
            mittente =  itemView.findViewById(R.id.txt_email);
            descrizione =  itemView.findViewById(R.id.txt_città);
            tipologiaSegnalazione =  itemView.findViewById(R.id.txt_tipo_segnalazione);
        }
    }

    private void trovaNomeCognomeUtente(String id, TextView mittente){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        Query query = db.getReference("Users").orderByChild("Id").equalTo(id);


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Utente utentetmp = snapshot.getValue(Utente.class);
                        mittente.setText(utentetmp.Nome+" "+utentetmp.Cognome);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);


    }

}
