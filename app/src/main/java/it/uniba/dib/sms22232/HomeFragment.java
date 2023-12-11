package it.uniba.dib.sms22232;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private RecyclerView groupsRecyclerView;



    private FirebaseAuth mAuth;
    private boolean isLogged;
    private boolean isEmailVerified;

    private ProgressDialog mProgress;
    private int i = 0; //variabile usatata per contare i gruppi letti inizialmente

    //database references
    private DatabaseReference reffUsers;

    private ImageView animalImageView;
    private SwipeRefreshLayout homeSwipeRefresh;

    private Button addPetButton;
    private View root;

   
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();

        /* vengono mostrati 2 layout diversi a seconda se l'utente ha verificato l'account tramite mail o no*/
        final View root;
        if (!isLogged) {
            root = profileFragment(inflater, container);
        } else {
            if (isEmailVerified) {
                root = homeFragment(inflater, container);
            } else {
                root = notEmailVerificatedHomeFragment(inflater, container);
            }

        }

        return root;
    }

    private View notEmailVerificatedHomeFragment(LayoutInflater inflater, final ViewGroup container) {
        View root = inflater.inflate(R.layout.fragment_not_email_verificated, container, false);
        MaterialButton emailIntentButton = root.findViewById(R.id.emailIntentButton);
        MaterialButton sendEmailVerificationButton = root.findViewById(R.id.sendEmailVerificationButton);
        final BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);

        sendEmailVerificationButton.setOnClickListener(
                new MaterialButton.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendEmailVerification();
                    }
                });

        emailIntentButton.setOnClickListener(new MaterialButton.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                getActivity().recreate();
                /* Intent che apre la casella di posta elettronica */
                Intent intent = Intent.makeMainSelectorActivity(
                        Intent.ACTION_MAIN,
                        Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //la posta elettronica viene aperta separatamente
                startActivity(intent);
            }
        });
        return root;
    }

    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(),
                                    getString(R.string.verification_email_sent) + " "+user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("pippo", "sendEmailVerification", task.getException());
                            Toast.makeText(getActivity(),
                                    getString(R.string.failed_verification_email),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private View homeFragment(LayoutInflater inflater, final ViewGroup container) {
        root = inflater.inflate(R.layout.fragment_home, container, false);

        /* vengono inizializzate tutte le view nel fragment*/
        inizializeViews(root);


        if (isLogged )
        {
            i = 0;

            addPetButton.setVisibility(View.VISIBLE);

            addPetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addPet = new Intent(getActivity(), NewPetActivity.class);
                    startActivity(addPet);

                }
            });

        }


        return root;
    }


    private View profileFragment(LayoutInflater inflater, final ViewGroup container){
        root = inflater.inflate(R.layout.fragment_login, container, false);
        /* vengono inizializzate tutte le view nel fragment*/

        return root;
    }

    private void inizializeViews(View root) {

        addPetButton = root.findViewById(R.id.addPetButton);
        addPetButton.setVisibility(View.GONE);//nasconde il bottone mi serve per non renderlo visibile a tutte le pagine

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void dismissProgress() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

    private void verifyLogged() {
        /* firebaseUser contiene l'informazione relativa all'utente se è loggato o meno */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        /* memorizzo in isLogged l'informazione boolean relativa all'utente se è loggato o meno*/
        if (firebaseUser == null) {
            Log.w("pippo", "mAuth: " + mAuth.getUid());
            isLogged = false;
            isEmailVerified = false;
        } else {
            Log.w("pippo", "mAuth: " + mAuth.getUid() + "firebaseUser: " + firebaseUser + " emailVerif: " + firebaseUser.isEmailVerified() + "");
            isLogged = true;
            isEmailVerified = firebaseUser.isEmailVerified();
        }
    }

    private void setProgressDialog() {
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage(getString(R.string.title_loading)); // Setting Message
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Horizontal
        mProgress.setCancelable(false);
    }

}