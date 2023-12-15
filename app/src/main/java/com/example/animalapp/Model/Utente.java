package com.example.animalapp.Model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

public class Utente implements Serializable {
    public String Cognome;
    public String Email;
    public String Nome;
    public String Password;
    public String TipoUtente;
    public String Id;
    public String ImgUrl;

    private Utente utente;

    public Utente getUtente(){
        return utente;
    }

    public Utente(){

    }

    public void getUtenteById(String currentId){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        Query query = db.getReference("Users").orderByChild("Id").equalTo(currentId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot tmpsnapshot : snapshot.getChildren()) {
                        utente = snapshot.getValue(Utente.class);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
