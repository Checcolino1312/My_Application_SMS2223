package com.example.animalapp.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.animalapp.Home_Ente_Activity;
import com.example.animalapp.Home_Veterinario_Activity;
import com.example.animalapp.MainActivity;
import com.example.animalapp.Update_Activity;
import com.example.animalapp.Model.Utente;
import com.example.animalapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Profilo_Veterinario_Fragment extends Fragment {

    private Context context;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    FirebaseAuth dbAuth = FirebaseAuth.getInstance();
    Query query;
    private List<Utente> nUser = new ArrayList<Utente>();
    ImageView profileImg;
    TextView profileNameVeterinario, profileEmailVeterinario, profileUsernameVeterinario,profilePasswordVeterinario;
    TextView titleName, titleUsername;

     Button modificaProfilo;
     FloatingActionButton caricaImagineProfilo;

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            nUser.clear();

            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Utente user = snapshot.getValue(Utente.class);
                    nUser.add(user);
                }
                showUserData();
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();




        caricaImagineProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Update_Activity.class);
                intent.putExtra("Utente", nUser.get(0));
                intent.putExtra("Posizione", "profilo");
                startActivity(intent);
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_veterinario, container, false);


        Activity activity = getActivity();
        if (activity instanceof Home_Veterinario_Activity) {
            ((Home_Veterinario_Activity) activity).setCustomBackEnabled(false);
        } else if (activity instanceof Home_Ente_Activity) {
            ((Home_Ente_Activity) activity).setCustomBackEnabled(false);
        }else if (activity instanceof MainActivity) {
            ((MainActivity) activity).setCustomBackEnabled(false);
        }

        profileNameVeterinario = view.findViewById(R.id.profileName);
        profileEmailVeterinario = view.findViewById(R.id.profileEmail);
        profileUsernameVeterinario = view.findViewById(R.id.profileUsername);
        profilePasswordVeterinario = view.findViewById(R.id.profilePassword);
        titleName = view.findViewById(R.id.titleName);
        titleUsername = view.findViewById(R.id.titleUsername);
        profileImg= view.findViewById(R.id.profileImg);
        modificaProfilo = view.findViewById(R.id.editButton);
        caricaImagineProfilo= view.findViewById(R.id.btn_nuava_foto);

        modificaProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Modifica_Profilo_Veterinario_Fragment(nUser.get(0))).addToBackStack(null).commit();
            }
        });


        String userId = dbAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ioandroid-57364-default-rtdb.firebaseio.com");
        query = database.getReference().child("Users").orderByChild("Id").equalTo(userId);
        query.addValueEventListener(valueEventListener);
        return view;
    }

    public void showUserData(){

        String nameUser = nUser.get(0).Nome;
        String emailUser = nUser.get(0).Email;
        String usernameUser = nUser.get(0).Cognome;
        String passwordUser = nUser.get(0).Password;

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(nUser.get(0).ImgUrl);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(profileImg);
            }
        });

        titleName.setText(nameUser+" "+usernameUser);
        titleUsername.setText(emailUser);
        profileNameVeterinario.setText(nameUser);
        profileEmailVeterinario.setText(emailUser);
        profileUsernameVeterinario.setText(usernameUser);
        profilePasswordVeterinario.setText(passwordUser);

    }

    public void passUserData(){
        String userId = dbAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://ioandroid-57364-default-rtdb.firebaseio.com");
        query = database.getReference().child("Users").orderByChild("Id").equalTo(userId);

    }






}