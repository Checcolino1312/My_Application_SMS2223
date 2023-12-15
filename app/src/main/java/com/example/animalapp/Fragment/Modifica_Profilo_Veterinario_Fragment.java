package com.example.animalapp.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.animalapp.Home_Ente_Activity;
import com.example.animalapp.Home_Veterinario_Activity;
import com.example.animalapp.MainActivity;
import com.example.animalapp.Model.Utente;
import com.example.animalapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class Modifica_Profilo_Veterinario_Fragment extends Fragment {

    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    Utente utente;



    public Modifica_Profilo_Veterinario_Fragment(Utente u){
        this.utente = u;
    }

    EditText editNome,editCognome,editEmail,editPassword;
    Button salvaButton;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String nomeUser,cognomeUser,emailUser,passwordUser;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modifica_profilo_veterinario, container, false);

        Activity activity = getActivity();
        if (activity instanceof Home_Veterinario_Activity) {
            ((Home_Veterinario_Activity) activity).setCustomBackEnabled(false);
        } else if (activity instanceof Home_Ente_Activity) {
            ((Home_Ente_Activity) activity).setCustomBackEnabled(false);
        }else if (activity instanceof MainActivity) {
            ((MainActivity) activity).setCustomBackEnabled(false);
        }

        editNome = view.findViewById(R.id.editName);
        editCognome = view.findViewById(R.id.editCognome);
        editEmail = view.findViewById(R.id.editEmail);
        editPassword = view.findViewById(R.id.editPassword);
        salvaButton = view.findViewById(R.id.saveButton);
        showUserData();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                // Azioni da eseguire quando viene premuto il pulsante "Indietro" nel Fragment
                // Esempio: torna indietro al Fragment "Profilo"

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(); // Rimuovi il fragment corrente dallo stack di back stack
                return true; // Consuma l'evento di pressione del pulsante "Indietro"
            }
            return false; // L'evento di pressione del pulsante "Indietro" viene propagato alle altre viste
        });


        salvaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isChanged()){
                    Toast.makeText(getContext(),"Modifica avvenuta con successo", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getContext(),"Nessuna modifica trovata", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }

    public boolean isChanged(){
        boolean tmp = false;
        if(!nomeUser.equals(editNome.getText().toString())){

            reference.child("Users").child(userId).child("Nome").setValue(editNome.getText().toString());
            nomeUser = editNome.getText().toString();
            tmp = true;
        }
        if(!cognomeUser.equals(editCognome.getText().toString())){
            reference.child("Users").child(userId).child("Cognome").setValue(editCognome.getText().toString());
            cognomeUser = editCognome.getText().toString();
            tmp = true;
        }
        if(!emailUser.equals(editEmail.getText().toString())){
            reference.child("Users").child(userId).child("Email").setValue(editEmail.getText().toString());
            emailUser = editEmail.getText().toString();
            tmp = true;
        }

        if(!passwordUser.equals(editPassword.getText().toString())){
            reference.child("Users").child(userId).child("Password").setValue(editPassword.getText().toString());
            passwordUser = editPassword.getText().toString();
            user.updatePassword(passwordUser);
            tmp = true;
        }
        return tmp;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*ProfileVeterinarioFragment tmp = (ProfileVeterinarioFragment) getParentFragmentManager().findFragmentById(R.id.fragment_container_profilo_veterinario);
        if (tmp != null){
            getParentFragmentManager().beginTransaction().hide(tmp).commit();
        }

         */
    }

    public void showUserData() {

        nomeUser = utente.Nome;
        emailUser = utente.Email;
        cognomeUser = utente.Cognome;
        passwordUser = utente.Password;
        editNome.setText(nomeUser);
        editEmail.setText(emailUser);
        editCognome.setText(cognomeUser);
        editPassword.setText(passwordUser);
    }
}