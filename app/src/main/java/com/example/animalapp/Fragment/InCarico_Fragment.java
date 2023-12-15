package com.example.animalapp.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.animalapp.Adapter.Segnalazioni_Adapter;
import com.example.animalapp.Adapter.Utente_Adapter;
import com.example.animalapp.Home_Ente_Activity;
import com.example.animalapp.Home_Veterinario_Activity;
import com.example.animalapp.Model.Animali;

import com.example.animalapp.Model.Segnalazioni;
import com.example.animalapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class  InCarico_Fragment extends Fragment {

    private RecyclerView recyclerView;
    //ANIMALE ADAPTER
    private List<Animali> mUtente;
    private Utente_Adapter adapter;
    DatabaseReference dbUtente;

    //SEGNALAZIONI ADAPTER
    private Segnalazioni_Adapter segnalazioniAdapter;
    com.getbase.floatingactionbutton.FloatingActionButton floatingButtonNuovaSegnalazione;
    private List<Segnalazioni> mSegnalazioni;
    DatabaseReference db;
    FirebaseAuth auth = FirebaseAuth.getInstance();


    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mSegnalazioni.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Segnalazioni segnalazioni = snapshot.getValue(Segnalazioni.class);
                    mSegnalazioni.add(segnalazioni);
                }
                segnalazioniAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_in_carico_veterinario, container, false);
        Activity activity = getActivity();
        if (activity instanceof Home_Veterinario_Activity) {
            ((Home_Veterinario_Activity) activity).setCustomBackEnabled(true);
        } else if (activity instanceof Home_Ente_Activity) {
            ((Home_Ente_Activity) activity).setCustomBackEnabled(true);
        }


        //LOGICA SEGNALAZIONI ADAPTER
        recyclerView = view.findViewById(R.id.recycler_view_incarico);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mSegnalazioni = new ArrayList<>();
        segnalazioniAdapter = new Segnalazioni_Adapter(this.getContext(), mSegnalazioni);

        recyclerView.setAdapter(segnalazioniAdapter);

        Query db= FirebaseDatabase.getInstance().getReference("Segnalazioni").orderByChild("idPresaInCarico").equalTo(auth.getCurrentUser().getUid());
        db.addValueEventListener(valueEventListener);

        // Inflate the layout for this fragment
        return view;
    }


    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Sei sicuro di voler effettuare il logout?");
        builder.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Effettua il logout da Firebase
                FirebaseAuth.getInstance().signOut();

                // Chiudi l'activity o esegui altre azioni di logout se necessario
                requireActivity().finish();
            }
        });
        builder.setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

