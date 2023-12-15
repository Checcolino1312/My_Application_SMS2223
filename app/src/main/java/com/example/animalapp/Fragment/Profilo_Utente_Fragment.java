package com.example.animalapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


import com.example.animalapp.R;

public class Profilo_Utente_Fragment extends Fragment {


    TextView ciaoutente;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_user, container, false);
        ciaoutente = view.findViewById(R.id.ciaoutente);
        return view;
    }






}
