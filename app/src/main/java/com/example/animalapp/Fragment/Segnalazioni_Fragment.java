package com.example.animalapp.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.animalapp.Home_Ente_Activity;
import com.example.animalapp.Home_Veterinario_Activity;
import com.example.animalapp.MainActivity;
import com.example.animalapp.Model.Segnalazioni;
import com.example.animalapp.Model.Utente;
import com.example.animalapp.R;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
public class Segnalazioni_Fragment extends Fragment {
    Segnalazioni segnalazioni;
    Utente utente;
    TextView tipoSegnalazione, descrizione, posizione;
    CheckBox cbVeteterinario, cbEnte, cbUtente;
    ImageView imgSegnalzioni;
    Button button, btnMaps;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


  Segnalazioni_Fragment(){

    }

    Segnalazioni_Fragment(Segnalazioni s, Utente u){
        this.segnalazioni=s;
        this.utente=u;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dettagli_segnalazioni, container, false);

        Activity activity = getActivity();
        if (activity instanceof Home_Veterinario_Activity) {
            ((Home_Veterinario_Activity) activity).setCustomBackEnabled(false);
        } else if (activity instanceof Home_Ente_Activity) {
            ((Home_Ente_Activity) activity).setCustomBackEnabled(false);
        }else if (activity instanceof MainActivity) {
            ((MainActivity) activity).setCustomBackEnabled(false);
        }
        tipoSegnalazione = view.findViewById(R.id.txt_dettagli_segnalazione_tipologia);
        descrizione = view.findViewById(R.id.txt_dettagli_segnalazione_descrizone);
        posizione = view.findViewById(R.id.txt_dettagli_segnalazione_posizione);
        cbVeteterinario = view.findViewById(R.id.dettagli_segnalazione_checkbox_veterinario);
        cbEnte = view.findViewById(R.id.dettagli_segnalazione_checkbox_ente);
        cbUtente = view.findViewById(R.id.dettagli_segnalazione_checkbox_utentetradizionale);
        imgSegnalzioni = view.findViewById(R.id.img_dettagli_segnalazioni);
        button = view.findViewById(R.id.dettagli_segnalazione_button);
        btnMaps = view.findViewById(R.id.btn_maps);

        tipoSegnalazione.setText(segnalazioni.tipologiaSegnalazione);
        descrizione.setText(segnalazioni.descrizione);
        posizione.setText(segnalazioni.posizione);
        if(segnalazioni.destinatarioVeterionario.equals("si")){
            cbVeteterinario.setChecked(true);
        }
        if(segnalazioni.destinatarioEnte.equals("si")){
            cbEnte.setChecked(true);
        }
        if(segnalazioni.destinatarioUtente.equals("si")){
            cbUtente.setChecked(true);
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(segnalazioni.imgSegnalazione);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(imgSegnalzioni.getContext())
                        .load(uri).circleCrop()
                        /*.placeholder(com.firebase.ui.storage.R.drawable.common_google_signin_btn_icon_dark)
                        .circleCrop()
                        .error(com.firebase.ui.storage.R.drawable.common_google_signin_btn_icon_dark_normal)*/
                        .into(imgSegnalzioni);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                segnalazioni.presaInCarico="si";
                segnalazioni.idPresaInCarico= auth.getCurrentUser().getUid();
                reference.child("Segnalazioni").child(segnalazioni.id).setValue(segnalazioni).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        switch (utente.TipoUtente){
                            case "EntePubblico":
                                startActivity(new Intent(view.getContext(), Home_Ente_Activity.class));
                                break;
                            case "Utente Amico":
                                startActivity(new Intent(view.getContext(), MainActivity.class));
                                break;
                            case "Veterinario":
                                startActivity(new Intent(view.getContext(), Home_Veterinario_Activity.class));
                                break;
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(segnalazioni.lattitudine!=0 && segnalazioni.longitudine!=0){
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", segnalazioni.lattitudine, segnalazioni.longitudine);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "Nessuna Posizione", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
