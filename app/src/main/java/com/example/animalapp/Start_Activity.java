package com.example.animalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

public class Start_Activity extends AppCompatActivity {
    Button login, register;

    FirebaseUser firebaseUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.Start_Activity);

        login=findViewById(R.id.login);
        register=findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Start_Activity.this,Login_Activity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Start_Activity.this,Registrazione_Activity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //redirect se l'utente è gia loggato
        if(firebaseUser!= null){
            startActivity(new Intent(Start_Activity.this,MainActivity.class));
        }
    }
}
