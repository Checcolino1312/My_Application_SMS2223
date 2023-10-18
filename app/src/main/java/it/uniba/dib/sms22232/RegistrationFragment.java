package it.uniba.dib.sms22232;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.uniba.dib.sms22232.databinding.FragmentLoginBinding;

public class RegistrationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseFunctions functions;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private static final String TAG = "managerAppTracker";
    private TextInputEditText nameEditText, surnameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private TextInputLayout name, surname, email, password, confirmPassword;
    private ProgressDialog progressDialog;
    private View v;
    private static final int RC_SIGN_IN = 9001;
    private Button signIn;
    private CheckBox privacyConfirmCheckBox;
    private FragmentLoginBinding binding;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean isA = false;




    public static final Pattern VALID_PASSWORD_REGEX =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$");
    /*^                 # start-of-string
    (?=.*[0-9])       # a digit must occur at least once
    (?=.*[a-z])       # a lower case letter must occur at least once
    (?=.*[A-Z])       # an upper case letter must occur at least once
    (?=.*[!@#$%^&+=])  # a special character must occur at least once
    (?=\S+$)          # no whitespace allowed in the entire string
    .{8,}             # anything, at least eight places though
$                 # end-of-string*/

    public RegistrationFragment() {
        // Required empty public constructor
    }

    public static RegistrationFragment newInstance(String param1, String param2) {
        RegistrationFragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        v = inflater.inflate(R.layout.fragment_registration, container, false);
        auth = FirebaseAuth.getInstance();
        functions = FirebaseFunctions.getInstance();
        setProgressDialog();
        auth.useAppLanguage();
        email = v.findViewById(R.id.registrationEmailTextInputLayout);
        name = v.findViewById(R.id.registrationNameTextInputLayout);
        surname = v.findViewById(R.id.registrationSurnameTextInputLayout);
        confirmPassword = v.findViewById(R.id.registrationConfirmPasswordTextInputLayout);
        password = v.findViewById(R.id.registrationPasswordTextInputLayout);
        signIn = v.findViewById(R.id.registrationButton);
        privacyConfirmCheckBox = v.findViewById(R.id.privacyConfirmCheckBox);
        nameEditText = v.findViewById(R.id.registrationNameEditText);
        surnameEditText = v.findViewById(R.id.registrationSurnameEditText);
        emailEditText = v.findViewById(R.id.registrationEmailEditText);
        passwordEditText = v.findViewById(R.id.registrationPasswordEditText);
        confirmPasswordEditText = v.findViewById(R.id.registrationConfirmPasswordEditText);

        TextView policyConfirmTextView = v.findViewById(R.id.policyConfirmTextView);
        String first = v.getResources().getString(R.string.msg_accept_privacy1);
        //String next = " <font color='#FF9800'><u>" + v.getResources().getString(R.string.msg_accept_privacy2) + "</u></font> " + v.getResources().getString(R.string.msg_accept_privacy3);
        policyConfirmTextView.setText(Html.fromHtml(first ));

       /* policyConfirmTextView.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {
                                                         startActivity(new Intent(getActivity(), PolicyActivity.class));
                                                     }
                                                 }
        );

        */
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
               // email.setHelperText(getString(R.string.help_registration_email));
            }
        });


        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //password.setHelperText(getString(R.string.password_rule));
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                password.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                confirmPassword.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        signIn.setOnClickListener(v -> {


                createAccount(nameEditText.getText().toString(),
                        surnameEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        passwordEditText.getText().toString());


        });

        return v;
    }

    private void createAccount(String name, String surname, String email, String password)
    {
        Log.d(TAG, "createAccount:" + email);
        /*if (!validateForm()) {
            return;
        }

         */
        progressDialog.show();

        final String nameAccount = name;
        final String surnameAccount = surname;
        final String emailAccount = email;


        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(getActivity(),task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        firebaseUser = auth.getCurrentUser();
                        //Scrittura del nome e cognome,
                        // successivamente email e password che concretizzano la regitrazione
                        writeNameSurname(auth.getUid(), nameAccount, surnameAccount,emailAccount);
                    }  else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                       // Toast.makeText(getActivity(), getString(R.string.authentication_failed),
                             //   Toast.LENGTH_SHORT).show();
                    }
                    // [START_EXCLUDE]
                    progressDialog.dismiss();
                    // [END_EXCLUDE]
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              //  Toast.makeText(getActivity(), getString(R.string.email_already_used),
                    //    Toast.LENGTH_SHORT).show();
            }
        });
        // [END create_user_with_email]
    }

    private void writeNameSurname (final String key, final String name,
                                   final String surname,final String email){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                databaseReference = FirebaseDatabase.getInstance()
                        .getReference().child("users").child(key);
                Log.w("user_prova", "name: "+name+" "+surname);
                databaseReference.child("name").setValue(name);
                databaseReference.child("surname").setValue(surname);
                databaseReference.child("email").setValue(email);

                if(!firebaseUser.isEmailVerified()) {
                    sendEmailVerification();
                }
                auth.signOut();
                backToProfile();
            }
        }, 2000);

    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailEditText.getText().toString();





        return valid;
    }


    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.dismiss();
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
      /*  if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
               // GoogleSignInAccount account = task.getResult(ApiException.class);
                //firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]

                // [END_EXCLUDE]


            }
        }

       */
    }
    // [END onactivityresult]

    private void backToProfile() {
        Fragment newFragment = new ProfileFragment();
        // consider using Java coding conventions (upper first char class names!!!)
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(getId(), newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }
    // [END auth_with_google]


    private void sendEmailVerification() {
        // Disable button
        //findViewById(R.id.verifyEmailButton).setEnabled(false);
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = auth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        //findViewById(R.id.verifyEmailButton).setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(),
                                    getString(R.string.verification_email_sent) + " "+user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(getActivity(),
                                    getString(R.string.failed_verification_email),
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                backToProfile();
            }
        });
        // [END send_email_verification]
    }
    private void setProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
       // progressDialog.setMessage(getString(R.string.loading_access)); // Setting Message
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Horizontal
        progressDialog.setCancelable(false);
    }


}



