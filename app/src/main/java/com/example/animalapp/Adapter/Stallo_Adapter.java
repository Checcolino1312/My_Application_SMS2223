package com.example.animalapp.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animalapp.Model.Animali;
import com.example.animalapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class Stallo_Adapter extends RecyclerView.Adapter<Stallo_Adapter.AnimalViewHolder> {
    final private Context mCtx;

    final private List<Animali> animalList;

    FirebaseUser firebaseUser;


    public Stallo_Adapter(Context mCtx, List<Animali> aList){
        this.mCtx = mCtx;
        this.animalList = aList;
    }

    @NonNull
    @Override
    public Stallo_Adapter.AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.installo_item,parent,false);
        return new Stallo_Adapter.AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Stallo_Adapter.AnimalViewHolder holder, int position) {
        final Animali animali = animalList.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        holder.nome_item.setText(animali.nomeAnimale);
        holder.specie_item.setText(animali.specie);
        holder.btn_cancella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(holder.btn_cancella.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("ELIMINAZIONE STALLO").setMessage("Sei sicuro di voler eliminare?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference reference;
                                FirebaseDatabase database = FirebaseDatabase.getInstance("https://provalogin-65cb5-default-rtdb.europe-west1.firebasedatabase.app/");
                                reference = database.getReference().child("Animals").child(animali.id).child("idStallo");
                                reference.setValue("no Stallo");
                                notifyItemRemoved(holder.getAdapterPosition());

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }

        });



        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(animali.imgAnimale);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.img.getContext())
                        .load(uri)
                        /*.placeholder(com.firebase.ui.storage.R.drawable.common_google_signin_btn_icon_dark)
                        .circleCrop()
                        .error(com.firebase.ui.storage.R.drawable.common_google_signin_btn_icon_dark_normal)*/
                        .into(holder.img);
            }
        });

    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }

    static class AnimalViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView nome_item, specie_item;
        Button btn_cancella;

        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img2);
            nome_item =  itemView.findViewById(R.id.nome_animale_item);
            specie_item =  itemView.findViewById(R.id.specie_animale_item);
            btn_cancella = itemView.findViewById(R.id.btn_cancella_stallo);

        }
    }

}
