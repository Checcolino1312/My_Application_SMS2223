package it.uniba.dib.sms22232;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Toolbar toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.settingsFragment, new MySettingsFragment())
                .commit();

    }



    public static class MySettingsFragment extends PreferenceFragmentCompat {
        private FirebaseAuth mAuth;
        private Context context;
        private Preference infoSettings;
        private DatabaseReference databaseReference;
        private String userToken = "";
        private static final String TAG = "animalAppTracker";


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.settings, rootKey);

            databaseReference = FirebaseDatabase.getInstance().getReference().child("token").child("userToken");
            getToken();


            Preference logoutProfile = findPreference("logout");
            logoutProfile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();

                    databaseReference.child(userToken).removeValue();



                    Toast.makeText(getActivity(), "Logout eseguito",
                            Toast.LENGTH_SHORT).show();

                    getActivity().setResult(ProfileFragment.LOGOUT_ID);
                    getActivity().finish();
                    return true;
                }
            });



        }


        private void getToken(){
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.i(TAG, "error set token 'null'", task.getException());
                                return;
                            }

                            userToken = task.getResult().getToken();
                            Log.i(TAG, "token attuale "+ userToken);
                        }
                    });
        }

        @Override
        public void onAttach(Context activity) {
            super.onAttach(activity);
            context = activity;
        }



    }

}
