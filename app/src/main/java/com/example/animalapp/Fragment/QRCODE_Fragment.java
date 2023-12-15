package com.example.animalapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.animalapp.Model.Animali;
import com.example.provalogin.Adapter.ImageAdapter;
import com.example.provalogin.Model.Animal;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import com.example.animalapp.R;

import java.util.ArrayList;


public class QRCODE_Fragment extends Fragment {

    private Animali animal;

    ImageView imgCondividi, imgQrCode;


    public QRCODE_Fragment(Animali animal) {
        this.animal=animal;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q_r_c_o_d_e, container, false);

        imgQrCode = view.findViewById(R.id.img_qrcode);
        generateQR();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void generateQR()
    {
        String text = animal.id;
        MultiFormatWriter writer = new MultiFormatWriter();
        try
        {
            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE,600,600);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            imgQrCode.setImageBitmap(bitmap);

        } catch (WriterException e)
        {
            e.printStackTrace();
        }
    }



}