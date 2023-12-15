package com.example.animalapp.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.animalapp.Model.Animal;

import com.example.animalapp.Model.Animali;
import com.example.animalapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class Animali_Adapter extends RecyclerView.Adapter<Animali_Adapter.AnimalViewHolder> {
    final private Context mCtx;

    final private List<Animali> animalList;

    FirebaseUser firebaseUser;


    public Animali_Adapter(Context mCtx, List<Animali> aList){
        this.mCtx = mCtx;
        this.animalList = aList;
    }

    @NonNull
    @Override
    public Animali_Adapter.AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.animal_item,parent,false);
        return new Animali_Adapter.AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Animali_Adapter.AnimalViewHolder holder, int position) {
        final Animali animali = animalList.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        if(animali.padrone.equals(firebaseUser.getUid())){
            // Se l'animale appartiene al proprietario, rendi il pulsante invisibile
            holder.btn_follow.setVisibility(View.INVISIBLE);
        }else{
            holder.btn_follow.setVisibility(View.VISIBLE);
        }

        holder.nome_item.setText(animali.nomeAnimale);
        holder.specie_item.setText(animali.specie);


        isFollowing(animali.id, holder.btn_follow);


        //following tiene traccia degli animali che l'utente sta seguendo
        //followers tiene traccia degli utenti che seguono l'animale
        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.btn_follow.getText().toString().equals("Follow")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(animali.id).child("id").setValue(animali.id);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(animali.id).child("nome").setValue(animali.nomeAnimale);

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(animali.id).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(animali.id).child("nome").removeValue();

                }
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
        static Button btn_follow;

        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img1);
            nome_item =  itemView.findViewById(R.id.nome_item);
            specie_item =  itemView.findViewById(R.id.specie_item);
            btn_follow = itemView.findViewById(R.id.btn_follow);

        }
    }


    private void isFollowing(String id, Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://provalogin-65cb5-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists()){
                    button.setText("Following");
                } else {
                    button.setText("Follow");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
