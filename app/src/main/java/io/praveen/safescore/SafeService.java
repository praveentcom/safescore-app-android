package io.praveen.safescore;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SafeService extends Service implements Runnable {

    private Thread thread = null;

    private void startThread(){
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void onCreate() {
        SafeScoreUtils utils = new SafeScoreUtils(this);
        int battery = utils.getBattery();
        double latitude = utils.getLatitude();
        double longitude = utils.getLongitude();
        int ontime = utils.ontime(latitude, longitude);
        boolean threat = false;
        boolean police = true;
        try {
            threat = utils.threat(latitude, longitude);
            police = utils.police(latitude, longitude);
        } catch (Exception ignored) {}
        SafeScoreAlgorithm algorithm = new SafeScoreAlgorithm(battery, ontime, threat, police);
        int score = algorithm.getScore();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("\\.", ",")).child("Background Data");
        String recordID = database.push().getKey();
        SafeDetails details = new SafeDetails();
        details.setBattery(battery);
        details.setLatitude(latitude);
        details.setLongitude(longitude);
        details.setOntime(ontime);
        details.setPolice(police);
        details.setThreat(threat);
        details.setScore(score);
        details.setDate(new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime()));
        database.child(recordID).setValue(details);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        thread = new Thread(this);
        startThread();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void run(){
        while(true){}
    }

}