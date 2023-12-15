package com.example.animalapp.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.animalapp.Adapter.Preferiti_Adapter;
import com.example.animalapp.MainActivity;
import com.example.animalapp.Model.Animali;
import com.example.animalapp.Model.Follow;
import com.example.animalapp.Model.Segnalazioni;
import com.example.animalapp.R;

import com.example.animalapp.Recycler.RecyclerItemClickListener;
import com.example.animalapp.Recycler_Item_click_Listener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Preferiti_Fragment extends Fragment {
    private RecyclerView elencopref;

    TextView nomeanimale;
    ImageView immagine;
    Follow seguiti;
    // Button btn_follow;

    private Preferiti_Adapter prefAdapter;
    private List<Follow> listAnimal;

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    //FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((MainActivity) getActivity()).setCustomBackEnabled(true);

        View view = inflater.inflate(R.layout.fragment_favorietes, container, false);
        elencopref = view.findViewById(R.id.elencopref);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);
        elencopref.setLayoutManager(layoutManager);
        //immagine = view.findViewById(R.id.immagine);
        //btn_follow = view.findViewById(R.id.btn_follow);
        //elencopref.setHasFixedSize(true);
        //elencopref.setLayoutManager(new LinearLayoutManager(getContext()));

        listAnimal = new ArrayList<>();
        prefAdapter = new Preferiti_Adapter(this.getContext(), listAnimal);

        elencopref.setAdapter(prefAdapter);


        //db = FirebaseDatabase.getInstance().getReference("Follow");

        // db.addValueEventListener(valueEventListener);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listAnimal.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Follow f = snapshot.getValue(Follow.class);
                    listAnimal.add(f);
                }
                prefAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Errore nel recupero degli animali preferiti", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }





    public void onViewCreated(@androidx.annotation.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //CLICK ITEM RECYCLERVIEW
        elencopref.addOnItemTouchListener(
                new Recycler_Item_click_Listener(getContext(), elencopref ,new Recycler_Item_click_Listener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Follow tmp = listAnimal.get(position);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new Dettagli_Animale(tmp)).addToBackStack(null).commit();

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

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
