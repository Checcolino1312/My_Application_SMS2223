package com.example.animalapp.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.animalapp.Adapter.Animali_Adapter;
import com.example.animalapp.Home_Ente_Activity;
import com.example.animalapp.Home_Veterinario_Activity;
import com.example.animalapp.MainActivity;
import com.example.animalapp.Model.Animali;
import com.example.animalapp.Recycler_Item_click_Listener;


import com.example.animalapp.Model.Utente;
import com.example.animalapp.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;


public class Profilo_Fragment extends Fragment {

    private RecyclerView recyclerView;

    private Animali_Adapter animalAdapter;
    FloatingActionButton inserire;

    com.getbase.floatingactionbutton.FloatingActionButton nuovoanimaleqrcode;
    com.getbase.floatingactionbutton.FloatingActionButton nuovoanimalebluetooth;
    com.getbase.floatingactionbutton.FloatingActionButton nuovoanimalemanualmente;

    private List<Animali> mAnimal;
    Query db;
    FirebaseAuth auth;

    DatabaseReference reference;
    String userid;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        auth= FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();
        String id = Aggiungi_Animale.generacodiceid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://provalogin-65cb5-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = database.getReference().child("Animals").child(id);

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mAnimal.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Animali animal = snapshot.getValue(Animali.class);
                    mAnimal.add(animal);
                }
                animalAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Activity activity = getActivity();
        if (activity instanceof Home_Veterinario_Activity) {
            ((Home_Veterinario_Activity) activity).setCustomBackEnabled(true);
        } else if (activity instanceof Home_Ente_Activity) {
            ((Home_Ente_Activity) activity).setCustomBackEnabled(true);
        }else if (activity instanceof MainActivity) {
            ((MainActivity) activity).setCustomBackEnabled(true);
        }

        recyclerView = view.findViewById(R.id.mieianimali_recyclerview);

        nuovoanimalemanualmente = view.findViewById(R.id.nuovomanuale);
        nuovoanimaleqrcode = view.findViewById(R.id.add_qr_code_pet);
        //inserire = view.findViewById(R.id.inserire);
        //String current = Animal.padrone;



            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



            mAnimal = new ArrayList<>();
            animalAdapter = new Animali_Adapter(getContext(), mAnimal);

//        if(current.equals(auth.getUid())) {

            recyclerView.setAdapter(animalAdapter);

       // }
            db = FirebaseDatabase.getInstance().getReference("Animals").orderByChild("padrone").equalTo(userid);
            db.addValueEventListener(valueEventListener);



        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //rendiVisibileView();




        //BOTTONE NUOVA SEGNALAZIONE
        nuovoanimalemanualmente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rendiInvisibileView();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Aggiungi_Animale()).addToBackStack(null).commit();


            }
        });

        nuovoanimaleqrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rendiInvisibileView();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Qr_code_Scan_Fragment("NuovoAnimale")).addToBackStack(null).commit();


            }
        });

        //CLICK ITEM RECYCLERVIEW
        recyclerView.addOnItemTouchListener(
                new Recycler_Item_click_Listener(getContext(), recyclerView ,new Recycler_Item_click_Listener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Animali tmp = mAnimal.get(position);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new Animali_In_possesso(tmp)).addToBackStack(null).commit();

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

    }


    private void rendiInvisibileView(){
        recyclerView.setVisibility(View.INVISIBLE);

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
