package it.uniba.dib.sms22232;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewPetActivity extends AppCompatActivity {

    public final int RESULT_LOAD_IMAGE = 21;
    public final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 11;

    private static final String TAG = "animalAppTracker";

    private Button createPetButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ImageView imgNewPetCreateImageView;
    private TextInputEditText nameNewPetEditText, ageNewPetEditTex, typeNewPetEditText;

    private Uri filePath;

    private Spinner spinner;
    private String txt;

    private Pet pet;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.newPetToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*inizializzazione da firebase della variabile contenete i dati dell'utente loggato*/
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference("imagesPets");


        /*inizializzazione delle view*/
        spinner = findViewById(R.id.spinnerPet);
        createPetButton = findViewById(R.id.createPetButton);
        imgNewPetCreateImageView = findViewById(R.id.imgNewPetCreateImageView);
        nameNewPetEditText = findViewById(R.id.nameNewPetEditText);
        ageNewPetEditTex = findViewById(R.id.ageNewPetEditText);
        typeNewPetEditText = findViewById(R.id.typeNewPetEditText);

        /*quando viene cliccata la foto, si puo caricare un immagine dalla galleria*/
        imgNewPetCreateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Devo chiedere il permesso di poter leggere dallo storage per caricare una foto e la leggo
                checkPermissionReadExternalStorage();
            }
        });

        createPetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se non è stato inserito alcun nome, non fa creare l'animale
                if (nameNewPetEditText.getText().toString().isEmpty()) {
                    nameNewPetEditText.setError(getResources().getString(R.string.title_error_insert_pet_name));
                } else {
                    createNewPet();

                }

            }
        });
    }



    public void checkPermissionReadExternalStorage() {

        if (ContextCompat.checkSelfPermission(NewPetActivity.this,
                Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(NewPetActivity.this,
                    android.Manifest.permission.READ_CONTACTS)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(NewPetActivity.this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            /* Apro la galleria per selezionare la foto */
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            Log.i(TAG, "ha chiesto di prendere le foto");
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    }


    private boolean createNewPet() {
        final boolean[] success = new boolean[1];

        //questi sono i valori da memorizzare sul db
        String name = nameNewPetEditText.getText().toString();
        String age = ageNewPetEditTex.getText().toString();
        String type = typeNewPetEditText.getText().toString();
        String selection = spinner.getSelectedItem().toString();



        /*viene convertita la foto in stringa, sara null invece se non c'e nessuna foto */

        String key = databaseReference.child("pet").push().getKey();

        txt = spinner.getSelectedItem().toString();


        pet = new Pet(
                key,
                name,
                age,
                type,
                txt
        );


        Map<String, Object> petMap = pet.toMap();

        Map<String, Object> childUpdate = new HashMap<>();

        //scrittura multipla su rami differenti del db
        childUpdate.put("/pets/" + key, petMap);
        childUpdate.put("/users/" + mAuth.getUid() + "/mypets/" + key, petMap);


        databaseReference.updateChildren(childUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                success[0] = true;

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                success[0] = false;
            }
        });

        if (filePath != null) {
            fileUpdater(key);
        }

        return success[0];
    }


    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));

    }


    private void fileUpdater(String key) {

        final String idPet = key;
        final StorageReference ref = storageReference.child(idPet + "." + getExtension(filePath));


        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //Scrittura della posizione della foto nello storage
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                databaseReference.child("pets")
                                        .child(idPet).child("imgpets")
                                        .setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //scrittura avvenuta con successo
                                            }

                                        });


                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {


                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "request code = " + requestCode + " resultCode = " + resultCode + " ResultLoadImage/ResultOk = " + RESULT_LOAD_IMAGE + "/" + RESULT_OK);

        Log.i(TAG, "data = " + data);

        /*viene caricata l'immagine scelta dalla galleria nell image view*/
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            filePath = data.getData();
            imgNewPetCreateImageView.setPadding(9, 9, 9, 9);
            Picasso.get().load(filePath).fit().centerInside()
                    .transform(new CircleTrasformation()).into(imgNewPetCreateImageView);//trasforma l'immagine in formato tondo
        }

        Intent backToHome = new Intent(this, MainActivity.class);
        startActivity(backToHome);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    /* Apro la galleria per selezionare la foto */
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                } else {
                    Toast.makeText(NewPetActivity.this, "E'necessario dare il permesso per poter caricare la foto", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
}
