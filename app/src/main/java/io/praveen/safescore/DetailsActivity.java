package io.praveen.safescore;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class DetailsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ImageView locate;
    EditText name, home, in, out, contact, number;
    Button next;
    double lat, lon;
    int inh, outh;
    Intent i;
    SharedPreferences preferences;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        preferences = PreferenceManager.getDefaultSharedPreferences(DetailsActivity.this);
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getEmail().replaceAll("\\.", ",")).child("Common Details");
        i = new Intent(DetailsActivity.this, MainActivity.class);
        name = findViewById(R.id.details_name);
        next = findViewById(R.id.details_submit);
        home = findViewById(R.id.details_location);
        locate = findViewById(R.id.details_locate);
        in = findViewById(R.id.details_in);
        out = findViewById(R.id.details_out);
        contact = findViewById(R.id.details_emergency);
        number = findViewById(R.id.details_emergency_mobile);
        locate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"MissingPermission", "SetTextI18n"})
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = null;
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (location != null) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    home.setText(lat + ", " + lon);
                } else{
                    home.setHint("Turn on GPS and try again!");
                }
            }
        });
        in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(DetailsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                inh = hour;
                                if(minute == 0){
                                    in.setText(hour + ":00");
                                } else {
                                    in.setText(hour + ":" + minute);
                                }
                            }
                        }, 8, 0, false);
                timePickerDialog.show();
            }
        });
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(DetailsActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                outh = hour;
                                if(minute == 0){
                                    out.setText(hour + ":00");
                                } else {
                                    out.setText(hour + ":" + minute);
                                }
                            }
                        }, 18, 0, false);
                timePickerDialog.show();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (check()){
                    next.setText("PLEASE WAIT");
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putFloat("lat", (float) lat);
                    editor.putFloat("lon", (float) lon);
                    editor.putInt("in", inh);
                    editor.putInt("out", outh);
                    editor.apply();
                    next.setTextColor(getResources().getColor(R.color.colorPrimary));
                    next.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLight)));
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString()).build();
                    mUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            final DatabaseReference dLat = database.child("Latitude");
                            final DatabaseReference dLong = database.child("Longitude");
                            final DatabaseReference dIn = database.child("Time In");
                            final DatabaseReference dOut = database.child("Time Out");
                            final DatabaseReference dContact = database.child("Emergency Contact");
                            final DatabaseReference dMobile = database.child("Emergency Mobile");
                            dLat.setValue(lat).addOnCompleteListener(DetailsActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dLong.setValue(lon).addOnCompleteListener(DetailsActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            dIn.setValue(inh).addOnCompleteListener(DetailsActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    dOut.setValue(outh).addOnCompleteListener(DetailsActivity.this, new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            dContact.setValue(contact.getText().toString()).addOnCompleteListener(DetailsActivity.this, new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    dMobile.setValue(number.getText().toString()).addOnCompleteListener(DetailsActivity.this, new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            startActivity(i);
                                                                            finish();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public boolean check(){
        if (name.getText().toString().length() == 0){
            Toast.makeText(DetailsActivity.this, "Enter the name!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (home.getText().toString().length() == 0){
            Toast.makeText(DetailsActivity.this, "Click the location icon and try again!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (in.getText().toString().length() == 0 || out.getText().toString().length() == 0){
            Toast.makeText(DetailsActivity.this, "Select your commute time!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (contact.getText().toString().length() == 0){
            Toast.makeText(DetailsActivity.this, "Enter your emergency contact name!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name.getText().toString().length() < 10){
            Toast.makeText(DetailsActivity.this, "Enter your emergency contact's number!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
