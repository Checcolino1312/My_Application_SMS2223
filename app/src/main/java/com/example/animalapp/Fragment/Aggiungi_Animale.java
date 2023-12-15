package com.example.animalapp.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.animalapp.Home_Ente_Activity;
import com.example.animalapp.Home_Veterinario_Activity;
import com.example.animalapp.MainActivity;

import com.example.animalapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;

import java.util.Random;

public class Aggiungi_Animale extends Fragment {


    public ImageView immaginedacaricare;

    Spinner specieanimale, sterilizzazioneanima, sessoanim, statosaluteanim;
    EditText nomeAnimale, padrone, preferenzaCibo, eta, chip;

    String imgPosition = "gs://provalogin-65cb5.appspot.com/logo.png";
    FirebaseAuth auth;
    DatabaseReference reference;
    String userid, id;

    String spece;

    public Aggiungi_Animale(){

    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        auth= FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid(); //padrone
        id = Aggiungi_Animale.generacodiceid(); //animale
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://provalogin-65cb5-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = database.getReference().child("Animals").child(id);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_newanimal, container, false);
        Activity activity = getActivity();
        if (activity instanceof Home_Veterinario_Activity) {
            ((Home_Veterinario_Activity) activity).setCustomBackEnabled(false);
        } else if (activity instanceof Home_Ente_Activity) {
            ((Home_Ente_Activity) activity).setCustomBackEnabled(false);
        }else if (activity instanceof MainActivity) {
            ((MainActivity) activity).setCustomBackEnabled(false);
        }
       immaginedacaricare = view.findViewById(R.id.img);


       return view;
   }
   @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
       super.onViewCreated(view, savedInstanceState);

       //reference
        nomeAnimale = view.findViewById(R.id.nome_animale);
        chip = view.findViewById(R.id.chip_animale);
        eta = view.findViewById(R.id.eta_animale);
        //padrone_animale = view.findViewById(R.id.padrone_animale);
        preferenzaCibo = view.findViewById(R.id.preferenza_animale);
        statosaluteanim = view.findViewById(R.id.salute_animale);
        sessoanim = view.findViewById(R.id.sesso_animale);
        specieanimale = view.findViewById(R.id.specie_animale);
        sterilizzazioneanima = view.findViewById(R.id.sterilizzazione_animale);
        // Set up ArrayAdapter for each spinner
       ArrayAdapter<CharSequence> sessoAdapter = ArrayAdapter.createFromResource(
               requireContext(),
               R.array.sessoa,
               android.R.layout.simple_spinner_dropdown_item
       );
       sessoanim.setAdapter(sessoAdapter);

       // Set up ArrayAdapter for each spinner
       ArrayAdapter<CharSequence> sterilizzazioneAdapter = ArrayAdapter.createFromResource(
               requireContext(),
               R.array.sterilizzazioneanimale,
               android.R.layout.simple_spinner_dropdown_item
       );
       sterilizzazioneanima.setAdapter(sterilizzazioneAdapter);
       // Set up ArrayAdapter for each spinner
       ArrayAdapter<CharSequence> specieAdapter = ArrayAdapter.createFromResource(
               requireContext(),
               R.array.speciea,
               android.R.layout.simple_spinner_dropdown_item
       );
       specieanimale.setAdapter(specieAdapter);
       // Set up ArrayAdapter for each spinner
       ArrayAdapter<CharSequence> saluteAdapter = ArrayAdapter.createFromResource(
               requireContext(),
               R.array.statosalute,
               android.R.layout.simple_spinner_dropdown_item
       );
       statosaluteanim.setAdapter(saluteAdapter);

       spece = specieanimale.getSelectedItem().toString();
       defImg(spece);



       specieanimale.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
           @Override
           public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
               spece = specieanimale.getSelectedItem().toString();
               defImg(spece);
           }
       });


    /*Definire il processo per inserire una immagine animale da telefono*/



    /*Cliccando il bottone di registrazione andiamo a inserire tutti i dati raccolti nel database.
    bisogna però prima controllare che tutto sia corretto*/
        final Button caricamentoanimale = view.findViewById(R.id.caricamentoanimale);
            caricamentoanimale.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View view){


                String nomeanimale = nomeAnimale.getText().toString();
                String etaanimale = eta.getText().toString();
                String chipanimale = chip.getText().toString();
                //String padrone = padrone_animale.getText().toString();
                String preferenzacibo = preferenzaCibo.getText().toString();
                String sessoa = sessoanim.getSelectedItem().toString();
                String specie = specieanimale.getSelectedItem().toString();
                String sterilizzazioneanimale = sterilizzazioneanima.getSelectedItem().toString();
                String saluteanimale = statosaluteanim.getSelectedItem().toString();

                if(isCorrect(chipanimale, nomeanimale, etaanimale, preferenzacibo)){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("nomeAnimale", nomeanimale);
                    hashMap.put("eta", etaanimale);
                    hashMap.put("chip", chipanimale);
                    hashMap.put("id",id);
                    hashMap.put("padrone",userid);
                    hashMap.put("preferenzaCibo", preferenzacibo);
                    hashMap.put("sesso", sessoa);
                    hashMap.put("specie",specie);
                    hashMap.put("sterilizzazione",sterilizzazioneanimale);
                    hashMap.put("statoSalute",saluteanimale);
                    hashMap.put("imgAnimale",imgPosition);

                    Toast.makeText(getContext(), "caricamentoeffettuato", Toast.LENGTH_SHORT).show();


                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(view.getContext(), MainActivity.class));
                        }
                    });
                }else{
                    Toast.makeText(getContext(), "errore", Toast.LENGTH_SHORT).show();
                }



                //mettere dialog se dati sono corretti


               // caricamentoFirebase(nome, eta, chip, padrone, preferenza, sesso, specie, sterilizzazione, salute);



            }
        });


    }



    public void defImg(String spece){
        switch (spece){
            case "Cane":
                immaginedacaricare.setImageResource(R.drawable.cane);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/cane.jpg";
                break;
            case "Gatto":
                immaginedacaricare.setImageResource(R.drawable.gatto);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/gatto.jpg";
                break;
            case "Rana":
                immaginedacaricare.setImageResource(R.drawable.rana);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/rana.jpg";
                break;
            case "Anatra":
                immaginedacaricare.setImageResource(R.drawable.anatra);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/anatra.jpg";
                break;
            case "Usignolo":
                immaginedacaricare.setImageResource(R.drawable.usignolo);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/usignolo.jpg";
                break;
            case "Pulcino":
                immaginedacaricare.setImageResource(R.drawable.pulcino);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/pulcino.jpg";
                break;
            case "Lupo":
                immaginedacaricare.setImageResource(R.drawable.lupo);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/lupo.jpg";
                break;
            case "Volpe":
                immaginedacaricare.setImageResource(R.drawable.volpe);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/volpe.jpg";
                break;
            case "Zebra":
                immaginedacaricare.setImageResource(R.drawable.zebra);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/zebra.jpg";
                break;
            case "Coccodrillo":
                immaginedacaricare.setImageResource(R.drawable.coccodrillo);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/coccodrillo.jpg";
                break;
            case "Tartaruga":
                immaginedacaricare.setImageResource(R.drawable.tartaruga);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/tartaruga.jpg";
                break;
            case "Pappagallo":
                immaginedacaricare.setImageResource(R.drawable.pappagallo);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/pappagallo.jpg";
                break;
            case "Iguana":
                immaginedacaricare.setImageResource(R.drawable.iguana);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/iguana.jpg";
                break;
            case "Fenicottero-rosa":
                immaginedacaricare.setImageResource(R.drawable.fenicottero);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/fenicottero.jpg";
                break;
            case "Foca Monaca":
                immaginedacaricare.setImageResource(R.drawable.foca);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/foca.jpg";
                break;
            case "Dog":
                immaginedacaricare.setImageResource(R.drawable.cane);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/cane.jpg";
                break;
            case "Cat":
                immaginedacaricare.setImageResource(R.drawable.gatto);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/gatto.jpg";
                break;
            case "Frog":
                immaginedacaricare.setImageResource(R.drawable.rana);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/rana.jpg";
                break;
            case "Duck":
                immaginedacaricare.setImageResource(R.drawable.anatra);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/anatra.jpg";
                break;
            case "Nightongale":
                immaginedacaricare.setImageResource(R.drawable.usignolo);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/usignolo.jpg";
                break;
            case "Chick":
                immaginedacaricare.setImageResource(R.drawable.pulcino);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/pulcino.jpg";
                break;
            case "Woolf":
                immaginedacaricare.setImageResource(R.drawable.lupo);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/lupo.jpg";
                break;
            case "Fox":
                immaginedacaricare.setImageResource(R.drawable.volpe);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/volpe.jpg";
                break;
            case "Crocodile":
                immaginedacaricare.setImageResource(R.drawable.coccodrillo);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/coccodrillo.jpg";
                break;
            case "Turtle":
                immaginedacaricare.setImageResource(R.drawable.tartaruga);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/tartaruga.jpg";
                break;
            case "Parot":
                immaginedacaricare.setImageResource(R.drawable.pappagallo);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/pappagallo.jpg";
                break;
            case "Pink-Flamingo":
                immaginedacaricare.setImageResource(R.drawable.fenicottero);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/fenicottero.jpg";
                break;
            case "Monk Seal":
                immaginedacaricare.setImageResource(R.drawable.foca);
                imgPosition ="gs://provalogin-65cb5.appspot.com/Animal/foca.jpg";
                break;
        }
    }

    public static String generacodiceid(){
        final String caratteri = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder ris = new StringBuilder();
        int i = 10;
        while (i > 0){
            Random random = new Random();
            ris.append(caratteri.charAt(random.nextInt(caratteri.length())));
            i--;
        }
        return ris.toString();
    }


    private boolean isCorrect(String chipanimale, String nomea, String etaa, String preferenza) {
        boolean isValid = true;

        if (!chipanimale.isEmpty() && (chipanimale.length() > 12 || chipanimale.length() < 12)) {
            chip.requestFocus();
            chip.setError(getResources().getString(R.string.chiperror));
            Toast.makeText(getContext(), getResources().getString(R.string.chip), Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            chip.setError(null);
        }

        if (nomea.isEmpty()) {
            nomeAnimale.requestFocus();
            nomeAnimale.setError(getResources().getString(R.string.nomeanimalemancante));
            //Toast.makeText(getContext(), getResources().getString(R.string.nomeanimalemancante), Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            nomeAnimale.setError(null);
        }
        if (etaa.isEmpty()) {
            eta.requestFocus();
            eta.setError(getResources().getString(R.string.etamancante));
           // Toast.makeText(getContext(), getResources().getString(R.string.nomeanimalemancante), Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            eta.setError(null);
        }
        if (preferenza.isEmpty()) {
           preferenzaCibo.requestFocus();
            preferenzaCibo.setError(getResources().getString(R.string.cibomancante));
            // Toast.makeText(getContext(), getResources().getString(R.string.nomeanimalemancante), Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            preferenzaCibo.setError(null);
        }

        return isValid;
    }

}