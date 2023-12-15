package com.example.animalapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.animalapp.Fragment.Animali_Veterinario_Fragment;
import com.example.animalapp.Fragment.InCarico_Fragment;


import com.example.animalapp.Fragment.Profilo_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Home_Veterinario_Activity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment=null;
    private static boolean isCustomBackEnabled = false;

    public static void setCustomBackEnabled(boolean isEnabled) {
        isCustomBackEnabled = isEnabled;
    }

    //Gestisco onBackPressed nell'activity per ogni fragment al quale  serve implementare questo metodo
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if(isCustomBackEnabled){
            if (fragment instanceof Animali_Veterinario_Fragment) {
                ((Animali_Veterinario_Fragment) fragment).onBackPressed();
            }

            if (fragment instanceof Animali_Veterinario_Fragment) {
                ((Animali_Veterinario_Fragment) fragment).onBackPressed();
            }
            if (fragment instanceof Profilo_Fragment) {
                ((Profilo_Fragment) fragment).onBackPressed();
            }

            if (fragment instanceof InCarico_Fragment) {
                ((InCarico_Fragment) fragment).onBackPressed();
            }
        }
        else{
            super.onBackPressed();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_veterinario);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListner);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new Animali_Veterinario_Fragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListner = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.homeVet:
                    selectedFragment= new Animali_Veterinario_Fragment();
                    break;
                case R.id.inCaricoVet:
                    selectedFragment= new InCarico_Fragment();
                    break;
                case R.id.petsVet:
                    selectedFragment= new Animali_Veterinario_Fragment();
                    break;
                case R.id.profileVet:
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
}
