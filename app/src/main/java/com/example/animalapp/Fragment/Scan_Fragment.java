package com.example.animalapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.animalapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;


public class Scan_Fragment extends Fragment {

    private CodeScanner codeScanner;
    private String position;

    public Scan_Fragment(String position) {
        this.position=position;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qrcode_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CodeScannerView scannerView = view.findViewById(R.id.scanner_animal);
        codeScanner = new CodeScanner(getContext(),scannerView);
        codeScanner.startPreview();

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {

                switch (position){
                    case "NuovoAnimale":
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String authid = FirebaseAuth.getInstance().getUid().toString();

                                String pet = result.getText();
                                FirebaseDatabase.getInstance().getReference().child("Animals").child(pet)
                                        .child("padrone").setValue(authid);

                                Toast.makeText(getContext(), getResources().getString(R.string.added), Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        });
                        break;
                    case "Stallo":
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String authid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

                                String pet = result.getText();
                                FirebaseDatabase.getInstance().getReference().child("Animals").child(pet)
                                        .child("idStallo").setValue(authid);
                                Toast.makeText(getContext(), getResources().getString(R.string.added), Toast.LENGTH_SHORT).show();
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        });
                        break;
                }


            }
        });

    }
}