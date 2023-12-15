package com.example.animalapp.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.animalapp.Adapter.Cure_Adapter;
import com.example.animalapp.Model.Animali;

import com.example.animalapp.Adapter.Spese_Adapter;
import com.example.animalapp.MainActivity;
import com.example.animalapp.Home_Ente_Activity;
import com.example.animalapp.Home_Veterinario_Activity;
import com.example.animalapp.Model.Cure;
import com.example.animalapp.Model.Spesa;
import com.example.animalapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Salute_Animali_Fragment extends Fragment {


    Animali animal;

    Button aggiungiCura;
    AlertDialog dialog;
    DatabaseReference reference;

    RecyclerView recyclerView;
    private Cure_Adapter cureAdapter;
    private List<Cure> mCure;

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mCure.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Cure cura = snapshot.getValue(Cure.class);
                    mCure.add(cura);
                }
                cureAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    public Salute_Animali_Fragment(Animali animale) {
        this.animal = animale;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_salute_animali, container, false);


        Activity activity = getActivity();
        if (activity instanceof Home_Veterinario_Activity) {
            ((Home_Veterinario_Activity) activity).setCustomBackEnabled(false);
        } else if (activity instanceof Home_Ente_Activity) {
            ((Home_Ente_Activity) activity).setCustomBackEnabled(false);
        }else if (activity instanceof MainActivity) {
            ((MainActivity) activity).setCustomBackEnabled(false);
        }
        aggiungiCura = view.findViewById(R.id.aggiungiCura);

        recyclerView = view.findViewById(R.id.recycler_view_cure_animali);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCure = new ArrayList<>();
        cureAdapter = new Cure_Adapter(this.getContext(),mCure);
        recyclerView.setAdapter(cureAdapter);
        Query db= FirebaseDatabase.getInstance().getReference("Cure").orderByChild("idAnimale").equalTo(animal.id);
        db.addValueEventListener(valueEventListener);

        aggiungiCura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialog();
            }
        });
        return view;
    }

    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_cura,null);
        EditText cura = view.findViewById(R.id.cura_edit);
        EditText prezzo = view.findViewById(R.id.prezzo_edit);
        EditText date = view.findViewById(R.id.calendarViewCura);
        builder.setView(view);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    c = Calendar.getInstance();


                    // on below line we are getting
                    // our day, month and year.
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    // on below line we are creating a variable for date picker dialog.
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            // on below line we are passing context.
                            getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    // on below line we are setting date to our edit text.
                                    date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                }
                            },
                            // on below line we are passing year,
                            // month and day for selected date in our date picker.
                            year, month, day);
                    // at last we are calling show to
                    // display our date picker dialog.
                    datePickerDialog.show();
                }
            }
        });
        builder.setTitle("Inserisci Cura")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = Aggiungi_Animale.generacodiceid();
                        Cure holder = new Cure();
                        holder.cura = cura.getText().toString();
                        holder.prezzo = prezzo.getText().toString()+"€";
                        holder.id = id;
                        holder.idAnimale = animal.id;
                        holder.idPadrone = animal.padrone;
                        holder.date = date.getText().toString();
                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://provalogin-65cb5-default-rtdb.europe-west1.firebasedatabase.app/");
                        reference = database.getReference().child("Cure").child(id);
                        reference.setValue(holder);
                    }
                })
                .setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialog = builder.create();
        dialog.show();

    }
}