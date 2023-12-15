package com.example.animalapp.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.animalapp.Adapter.Stallo_Adapter;
import com.example.animalapp.Home_Ente_Activity;
import com.example.animalapp.Home_Veterinario_Activity;
import com.example.animalapp.Model.Animali;

import com.example.animalapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Animali_Veterinario_Fragment extends Fragment {

    private RecyclerView recyclerView;

    private FloatingActionButton btn_qrcode;

    private Stallo_Adapter animalAdapter;
    private List<Animali> mAnimal;


    private FirebaseUser firebaseUser;



    public Animali_Veterinario_Fragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Activity activity = getActivity();
        if (activity instanceof Home_Veterinario_Activity) {
            ((Home_Veterinario_Activity) activity).setCustomBackEnabled(true);
        } else if (activity instanceof Home_Ente_Activity) {
            ((Home_Ente_Activity) activity).setCustomBackEnabled(true);
        }

        return inflater.inflate(R.layout.fragment_pets_veterinario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view_pet_stallo);
        btn_qrcode = view.findViewById(R.id.btn_Qr_Code_NewStallo);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAnimal = new ArrayList<>();
        animalAdapter = new InStalloAdapter(getContext(), mAnimal);


        recyclerView.setAdapter(animalAdapter);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Animals").
                        orderByChild("idStallo").equalTo(firebaseUser.getUid().toString());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAnimal.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Animal animal = snapshot.getValue(Animal.class);
                        mAnimal.add(animal);
                    }
                    animalAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Errore nel recupero degli animali preferiti", Toast.LENGTH_SHORT).show();
            }
        });

        btn_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new QrcodeScanFragment("Stallo")).addToBackStack(null).commit();
            }
        });

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