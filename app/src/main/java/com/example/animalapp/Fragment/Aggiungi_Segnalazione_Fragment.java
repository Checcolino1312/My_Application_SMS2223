package com.example.animalapp.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.animalapp.Home_Ente_Activity;
import com.example.animalapp.Home_Veterinario_Activity;
import com.example.animalapp.MainActivity;
import com.example.animalapp.Model.Segnalazioni;
import com.example.animalapp.Model.Utente;
import com.example.animalapp.R;
import com.example.animalapp.Registrazione_Activity;
import com.example.animalapp.Update_Activity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;


public class Aggiungi_Segnalazione_Fragment extends Fragment {

    Context context;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    FirebaseAuth auth;
    DatabaseReference reference;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    String userid;
    FirebaseDatabase db;
    Utente utente;
    Segnalazioni hashMap;

    String id;
    Uri filePathSegnalazione;
    String imgPosition = "gs://provalogin-65cb5.appspot.com/logo.png";

    EditText descrizione;

    TextView posizione;
    Spinner tipologia;
    Button btnInviaSegnalazione, btnCheckPosition;
    CheckBox cbVeterinario, cbEnte, cbUtenteTradizionale;
    ImageView imgSegnalazione;
    FloatingActionButton caricaImagineProfilo;

    private LocationRequest locationRequest;

    private double latitude;
    private double longitude;

    public Aggiungi_Segnalazione_Fragment() {
        // Required empty public constructor

    }

    public Aggiungi_Segnalazione_Fragment(Utente utentetmp) {
        this.utente=utentetmp;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();
        if (activity instanceof Home_Veterinario_Activity) {
            ((Home_Veterinario_Activity) activity).setCustomBackEnabled(false);
        } else if (activity instanceof Home_Ente_Activity) {
            ((Home_Ente_Activity) activity).setCustomBackEnabled(false);
        }else if (activity instanceof MainActivity) {
            ((MainActivity) activity).setCustomBackEnabled(false);
        }
        auth=FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();
        id = Aggiungi_Animale.generacodiceid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://provalogin-65cb5-default-rtdb.europe-west1.firebasedatabase.app/");
        reference = database.getReference().child("Segnalazioni").child(id);

    }

    @Override
    public void onResume() {
        super.onResume();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        btnCheckPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (isGPSEnabled()) {

                    getCurrentLocation();

                }else {

                    turnOnGPS();
                }
            }
        }


    }




    private void getCurrentLocation() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(getContext())
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(getContext())
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        latitude = locationResult.getLocations().get(index).getLatitude();
                                        longitude = locationResult.getLocations().get(index).getLongitude();

                                        posizione.setText("Latitude: "+ latitude + "\n" + "Longitude: "+ longitude);
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void turnOnGPS() {



        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getActivity())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(getContext(), "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(getActivity(), 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_nuova_segnalazione, container, false);



    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        descrizione=view.findViewById(R.id.txt_descrizione);
        posizione=view.findViewById(R.id.txt_posizione);
        tipologia=view.findViewById(R.id.spinner_tipologia);
        btnInviaSegnalazione=view.findViewById(R.id.btn_invia_nuova_segnalazione);
        btnCheckPosition=view.findViewById(R.id.btn_check_position);
        cbEnte=view.findViewById(R.id.checkbox_ente);
        cbUtenteTradizionale=view.findViewById(R.id.checkbox_utentetradizionale);
        cbVeterinario=view.findViewById(R.id.checkbox_veterinario);
        imgSegnalazione=view.findViewById(R.id.img_segnalazione);
        caricaImagineProfilo= view.findViewById(R.id.btn_nuava_foto_segnalazione);


        hashMap = new Segnalazioni();
        hashMap.imgSegnalazione = imgPosition;
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(hashMap.imgSegnalazione);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(imgSegnalazione);
            }
        });



        btnInviaSegnalazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog pd =new ProgressDialog(view.getContext());
                pd.setMessage("Caricamento...");
                pd.show();
                String destinatarioEnte="no";
                String destinatarioVeterinario="no";
                String destinatarioUtente="no";
                if(cbVeterinario.isChecked()){
                    destinatarioVeterinario="si";
                }
                if(cbEnte.isChecked()){
                    destinatarioEnte="si";
                }
                if(cbUtenteTradizionale.isChecked()){
                    destinatarioUtente="si";
                }


                hashMap.id=id;
                hashMap.tipologiaSegnalazione =  tipologia.getSelectedItem().toString();
                hashMap.descrizione = descrizione.getText().toString();
                hashMap.posizione = posizione.getText().toString();
                hashMap.destinatarioVeterionario = destinatarioVeterinario;
                hashMap.destinatarioEnte = destinatarioEnte;
                hashMap.destinatarioUtente = destinatarioUtente;
                hashMap.idMittente = userid;
                hashMap.presaInCarico = "no";
                hashMap.idPresaInCarico = "no";
                hashMap.imgSegnalazione = imgPosition;
                hashMap.lattitudine = latitude;
                hashMap.longitudine = longitude;

                /*
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", id);
                hashMap.put("tipologiaSegnalazione", tipologia.getSelectedItem().toString());
                hashMap.put("descrizione", descrizione.getText().toString());
                hashMap.put("posizione", posizione.getText().toString());
                hashMap.put("destinatarioVeterionario", destinatarioVeterinario);
                hashMap.put("destinatarioEnte", destinatarioEnte);
                hashMap.put("destinatarioUtente", destinatarioUtente);
                hashMap.put("idMittente",userid);
                hashMap.put("presoInCarico","no");
                hashMap.put("idPresoInCarico", "no");
                hashMap.put("imgSegnalazione",imgSegnalazione.toString());
                 */

                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
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
                pd.dismiss();
            }
        });

        caricaImagineProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( context.checkSelfPermission( Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED &&
                        context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    String[] permission = { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, PERMISSION_CODE);
                }
                else{
                    openCamera();
                }

            }

        });






    }

    public void openCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera");
        filePathSegnalazione = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePathSegnalazione);
        startActivityForResult( cameraIntent, IMAGE_CAPTURE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CAPTURE_CODE){
            imgSegnalazione.setImageURI(filePathSegnalazione);
            uploadImage();
        }

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }


    }


    private void uploadImage() {
        if (filePathSegnalazione != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(context);progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            String random = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/" + random);
            imgPosition = "gs://provalogin-65cb5.appspot.com/images/" + random;


            // adding listeners on upload
            // or failure of image
            ref.putFile(filePathSegnalazione).addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast.makeText(context, R.string.immagine_caricata, Toast.LENGTH_SHORT).show();

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                }
                            });
        }
    }

}