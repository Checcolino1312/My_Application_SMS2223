package com.example.animalapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animalapp.Model.Animali;
import com.example.animalapp.Model.Immagine;
import com.example.animalapp.Model.Segnalazioni;
import com.example.animalapp.Model.Utente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class Update_Activity extends AppCompatActivity {
    private Button btnSelect, btnUpload, btnCamera;

    // view for image view
    private ImageView imageView;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    private String imgPosition;
    private String posizione;
    private Utente utente;
    private Segnalazioni segnalazioni;
    private Animali animale;


    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.Update_Activity);


        // initialise views
        btnSelect = findViewById(R.id.btnScegli);
        btnUpload = findViewById(R.id.btnInvia);
        btnCamera = findViewById(R.id.btnFaiFoto);
        imageView = findViewById(R.id.imgUpdate);

        // get the Firebase storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // on pressing btnSelect SelectImage() is called
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        // on pressing btnUpload uploadImage() is called
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        // on pressing btnUpload openCamera() is called
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( checkSelfPermission( android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED &&
                        checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    String[] permission = { android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, PERMISSION_CODE);
                }
                else{
                    openCamera();
                }

            }
        });

    }

    // Select Image method
    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here...") , PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }

        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK){
            imageView.setImageURI(filePath);
        }
    }

    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            String random = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/" + random);

            imgPosition = "gs://provalogin-65cb5.appspot.com/images/" + random;
            posizione = getIntent().getStringExtra("Posizione");

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast.makeText(Update_Activity.this, R.string.immagine_caricata, Toast.LENGTH_SHORT).show();
                                    sceltaDoveModificareImg(posizione);
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(Update_Activity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                }
                            });
        }
    }

    public void openCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera");
        filePath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
        startActivityForResult( cameraIntent, IMAGE_CAPTURE_CODE);
    }

    //override permission result


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE:
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else{
                    Toast.makeText(this, "Permesso Negato", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void sceltaDoveModificareImg(String position){
        switch (position){
            case "profilo":
                modificaImgProfilo();
                break;
            case "nuovaSegnalazione":
                imgNuovaSegnalazione();
                break;
            case "nuovoanimale":
                imganimal();
                break;
            case "dettagliMieiAnimali":
                modificaImgProfiloAnimale();
                break;
            case "nuovaFotoAlbum":
                aggiungiFotoAlbum();
                break;
        }
    }

    private void imganimal() {
        String filePath = getIntent().getStringExtra("FilePath");
        filePath = imgPosition;
    }


    public void modificaImgProfilo(){
        utente = (Utente) getIntent().getSerializableExtra("Utente");
        utente.ImgUrl=imgPosition;
        reference.child("Users").child(utente.Id).child("ImgUrl").setValue(imgPosition);
    }

    public void imgNuovaSegnalazione(){

        String filePathSegnalazione = getIntent().getStringExtra("FilePath");
        filePathSegnalazione = imgPosition;

        /*
        segnalazioni = (Segnalazioni) getIntent().getSerializableExtra("NuovaSegnalazione");
        segnalazioni.imgSegnalazione=imgPosition;
        reference.child("Segnalazioni").setValue(segnalazioni);
         */
    }

    public void modificaImgProfiloAnimale(){
        animale = (Animali) getIntent().getSerializableExtra("Animale");
        animale.imgAnimale = imgPosition;
        reference.child("Animals").child(animale.id).child("imgAnimale").setValue(imgPosition);
        this.onBackPressed();
    }

    public void aggiungiFotoAlbum(){
        animale = (Animali) getIntent().getSerializableExtra("Animale");
        Immagine img = new Immagine();
        img.id = NewAnimal.generacodiceid();
        img.imgPosition = imgPosition;
        img.idAnimale= animale.id;
        img.idPadrone = animale.padrone;

        //caricamento
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://provalogin-65cb5-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = database.getReference().child("Image").child(img.id);
        reference.setValue(img);
        this.onBackPressed();
    }

}
