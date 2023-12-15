package com.example.animalapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.animalapp.Fragment.Cerca_Fragment;
import com.example.animalapp.Fragment.InCarico_Fragment;
import com.example.animalapp.Fragment.Preferiti_Fragment;
import com.example.animalapp.Fragment.Profilo_Fragment;
import com.example.animalapp.Fragment.Veterinario_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.fragment.app.Fragment;


import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static boolean isCustomBackEnabled = false;

    public static void setCustomBackEnabled(boolean isEnabled) {
        isCustomBackEnabled = isEnabled;
    }


    //Gestisco onBackPressed nell'activity per ogni fragment al quale  serve implementare questo metodo
    @Override
    public void onBackPressed() {

        if(isCustomBackEnabled){
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof Veterinario_Fragment) {
                ((Veterinario_Fragment) fragment).onBackPressed();
            }

            if (fragment instanceof Cerca_Fragment) {
                ((Cerca_Fragment) fragment).onBackPressed();
            }
            if (fragment instanceof Preferiti_Fragment) {
                ((Preferiti_Fragment) fragment).onBackPressed();
            }

            if (fragment instanceof Profilo_Fragment) {
                ((Profilo_Fragment) fragment).onBackPressed();
            }
        }

        else{
            super.onBackPressed();
        }

    }

    BottomNavigationView bottomNavigationView;
    View actionbar_nav;
    Fragment selectedFragment=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListner);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new Veterinario_Fragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_nav, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Profilo_Fragment()).addToBackStack(null).commit();
            return true;
        }
        if (id == R.id.logout){
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("LOGOUT").setMessage("Vuoi Effettuare il logout?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseAuth.getInstance().signOut();
                            onBackPressed();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }




    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListner = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.segnalazioni:
                    selectedFragment= new Veterinario_Fragment();
                    break;
                case R.id.incarico_utente:
                    selectedFragment= new InCarico_Fragment();
                    break;
                case R.id.ic_search:
                    selectedFragment= new Cerca_Fragment();
                    break;
                case R.id.ic_favorites:
                    selectedFragment= new Preferiti_Fragment();
                    break;
                case R.id.ic_profile:
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    selectedFragment= new Profilo_Fragment();
                    break;

            }
            if( selectedFragment != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
            }

            return true;
        }
    };

}