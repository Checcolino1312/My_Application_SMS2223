package com.example.animalapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.animalapp.Adapter.Immagine_Adapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Index_Activity extends AppCompatActivity {
    private ImageView image;
    private Button updateBtn;

    //variabili lista immagini
    private RecyclerView mRecyclerView;
    private Immagine_Adapter mAdapter;
    private DatabaseReference mDatabaseRef;
    private ArrayList<Upload_Activity> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.Index_Activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        image = findViewById(R.id.imageAvatar);
        updateBtn=findViewById(R.id.bntAddImg);
        downloadImage("download.jpg");
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Update_Activity.class);
                startActivity(intent);
            };
        });

        //lista immagini
        mRecyclerView=findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads= new ArrayList<>();

        mDatabaseRef= FirebaseDatabase.getInstance().getReference("images");

        //logica
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot : snapshot.getChildren()){
                    Upload_Activity upload = postSnapshot.getValue(Upload_Activity.class);
                    mUploads.add(upload);

                }



                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void downloadImage(String nameImage){
        //variabili
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageReference = storageRef.child(nameImage);
        //logica
        long MAXBYTES=1024*1024;
        imageReference.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                image.setImageBitmap(bitmap);
                Toast.makeText(Index_Activity.this,"Immagine Caricata Con Successo",Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("fallito");
            }
        });

    }
}
