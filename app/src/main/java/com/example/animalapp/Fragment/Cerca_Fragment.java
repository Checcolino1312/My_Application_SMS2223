package com.example.animalapp.Fragment;

import static android.system.Os.remove;

import android.content.DialogInterface;
import android.os.Bundle;

import com.example.animalapp.Adapter.Animali_Adapter;
import com.example.animalapp.MainActivity;
import com.example.animalapp.Model.Animali;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.arch.core.internal.FastSafeIterableMap;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.example.animalapp.R;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class Cerca_Fragment extends Fragment {

    private RecyclerView recyclerView;

    EditText edit;
    ImageView imm;
   // Button btn_follow;

    private Animali_Adapter animalAdapter;
    private List<Animali> listAnimal;
    DatabaseReference db;
    EditText search_bar;
    //FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        ((MainActivity) getActivity()).setCustomBackEnabled(true);
        edit = view.findViewById(R.id.searchbar);
        imm = view.findViewById(R.id.im);
        //btn_follow = view.findViewById(R.id.btn_follow);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search_bar = view.findViewById(R.id.searchbar);



        listAnimal = new ArrayList<>();
        animalAdapter = new Animali_Adapter(this.getContext(), listAnimal);

        recyclerView.setAdapter(animalAdapter);

        db = FirebaseDatabase.getInstance().getReference("Animals");
        db.addValueEventListener(valueEventListener);


       search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                readUsers();
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                searchUsers(charSequence.toString().toLowerCase());
                readUsers();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                readUsers();


            }
        });

        // Inflate the layout for this fragment

        return view;
    }





    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }


    private void searchUsers(String s){
        Query query = db.orderByChild("nomeAnimale")
                .startAt(s)
                .endAt(s+"\uf8ff");
        query.addValueEventListener(valueEventListener);
    }

    //lettura utenti


    private void readUsers (){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Animals");
        //String current = Animal.padrone;
        //if(!current.equals(listAnimal.equals("padrone"))){
            //foll.setVisibility(View.VISIBLE);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (search_bar.getText().toString().equals("")){
                    listAnimal.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Animali animal = snapshot.getValue(Animali.class);
                        listAnimal.add(animal);
                    }
                    animalAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            listAnimal.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Animali artist = snapshot.getValue(Animali.class);
                    listAnimal.add(artist);
                }
                animalAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


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

