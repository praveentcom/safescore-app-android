package io.praveen.safescore;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    SharedPreferences preferences;
    Button scream;
    double lat1, lon1;
    TextView name, score, battery, location, police, away, time, tour, threat;
    int Score = 0;

    @SuppressLint({"SetTextI18n", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        float lat = preferences.getFloat("lat", 0);
        float lon = preferences.getFloat("lon", 0);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = null;
        if (locationManager != null) {
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (loc != null) {
            lat1 = loc.getLatitude();
            lon1 = loc.getLongitude();
        }
        double distance = distance(lat, lat1, lon, lon1);
        mUser = mAuth.getCurrentUser();
        name = findViewById(R.id.main_welcome);
        score = findViewById(R.id.main_score);
        battery = findViewById(R.id.main_battery);
        location = findViewById(R.id.main_location);
        location.setText(lat1 + ", " + lon1);
        away = findViewById(R.id.main_away);
        time = findViewById(R.id.main_time);
        police = findViewById(R.id.main_police);
        scream = findViewById(R.id.scraem);
        scream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MediaPlayer mp2 = MediaPlayer.create(MainActivity.this, R.raw.raw);
                mp2.setLooping(true);
                scream.setText("CLICK AGAIN TO SCREAM OUT LOUD");
                scream.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        if(mp2.isPlaying()) {
                            mp2.pause();
                            scream.setText("CLICK TO SCREAM OUT LOUD");
                        } else {
                            mp2.start();
                            scream.setText("CLICK AGAIN TO TURN OFF");
                        }
                    }
                });
            }
        });
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm | dd/MM/yyyy", Locale.ENGLISH);
        String formattedDate = df.format(c.getTime());
        time.setText(formattedDate);
        DecimalFormat _numberFormat= new DecimalFormat("#0.0");
        float mDist = Float.parseFloat(_numberFormat.format((float) distance/1000));
        tour = findViewById(R.id.main_tour);
        threat = findViewById(R.id.main_threat);
        new Json3().execute("https://maps.googleapis.com/maps/api/place/textsearch/json?location="+lat1+","+lon1+"&radius=10000&query=fort&key=AIzaSyCczblqj3aNVsRde-4oin7FnGmyfMpEx3c");
        new Json().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat1+","+lon1+"&radius=2000&type=police&key=AIzaSyCczblqj3aNVsRde-4oin7FnGmyfMpEx3c");
        new Json2().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat1+","+lon1+"&radius=20000&type=liquor_store&key=AIzaSyCczblqj3aNVsRde-4oin7FnGmyfMpEx3c");
        Score += 20;
        if (mDist > 5) {
            away.setText("AWAY FROM HOME BY " + mDist + " KMs");
            away.setTypeface(null, Typeface.BOLD);
        } else if (mDist > 0.5){
            away.setText("AWAY FROM HOME BY " + mDist + " KMs");
            away.setTextColor(getResources().getColor(R.color.colorGreen));
        } else{
            away.setText("SWEET, YOU ARE NEAR YOUR HOME!");
            away.setTextColor(getResources().getColor(R.color.colorGreen));
        }
        name.setText("Welcome " + mUser.getDisplayName() +",\nYour TourScore is");
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        if (bm != null) {
            int batteryLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            battery.setText(batteryLevel+"%");
            if (batteryLevel > 90){
                Score += 20;
            } else if (batteryLevel > 75){
                Score += 16;
            } else if (batteryLevel > 50){
                Score += 12;
            } else if (batteryLevel > 25){
                Score += 8;
            } else if (batteryLevel > 10){
                Score += 4;
            }
        }
        score.setText(Score+" /100");
        Intent myIntent = new Intent(MainActivity.this, SafeService.class);
        PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+1000000, pendingIntent);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else{
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static double distance(double lat1, double lat2, double lon1, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000;
        distance = Math.pow(distance, 2) + Math.pow(0, 2);
        distance = Math.sqrt(distance);
        return distance;
    }

    @SuppressLint("StaticFieldLeak")
    private class Json extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (IOException ignored) {} finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException ignored) {}
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject mainObject = new JSONObject(result);
                String policeStatus = mainObject.getString("status");
                if (policeStatus.equals("ZERO_RESULTS")){
                    Score += 5;
                    police.setText("NO STATIONS NEAR 2 KMs");
                    police.setTypeface(null, Typeface.BOLD);
                    police.setTextColor(getResources().getColor(R.color.colorAccent));
                } else{
                    JSONArray res = mainObject.getJSONArray("results");
                    JSONObject obj = res.getJSONObject(0);
                    Score += 20;
                    final JSONObject lo = obj.getJSONObject("geometry");
                    JSONObject geo = lo.getJSONObject("location");
                    final String lat = geo.getString("lat");
                    final String lng = geo.getString("lng");
                    final String loc = obj.getString("name");
                    police.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + loc + ")";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                            startActivity(intent);
                        }
                    });
                    police.setText("YES, STATION FOUND\n("+loc+")");
                    police.setTextColor(getResources().getColor(R.color.colorGreen));
                }
                score.setText(Score+" /100");
            } catch (Exception ignored){}
            super.onPostExecute(result);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Json3 extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (IOException ignored) {} finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException ignored) {}
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject mainObject = new JSONObject(result);
                String policeStatus = mainObject.getString("status");
                if (policeStatus.equals("ZERO_RESULTS")){
                    Score += 5;
                    tour.setText("NO SPOTS NEAR 10 KMS");
                    tour.setTypeface(null, Typeface.BOLD);
                    tour.setTextColor(getResources().getColor(R.color.colorAccent));
                } else{
                    JSONArray res = mainObject.getJSONArray("results");
                    JSONObject obj = res.getJSONObject(0);
                    Score += 20;
                    final JSONObject lo = obj.getJSONObject("geometry");
                    JSONObject geo = lo.getJSONObject("location");
                    final String lat = geo.getString("lat");
                    final String lng = geo.getString("lng");
                    final String loc = obj.getString("name");
                    tour.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + loc + ")";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                            startActivity(intent);
                        }
                    });
                    tour.setTypeface(null, Typeface.BOLD);
                    tour.setText(loc.toUpperCase());
                    tour.setTextColor(getResources().getColor(R.color.colorGreen));
                }
                score.setText(Score+" /100");
            } catch (Exception ignored){}
            super.onPostExecute(result);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class Json2 extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (IOException ignored) {} finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException ignored) {}
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject mainObject = new JSONObject(result);
                String wineStatus = mainObject.getString("status");
                if (wineStatus.equals("ZERO_RESULTS")){
                    threat.setText("NONE/MINIMAL");
                    Score += 20;
                    threat.setTextColor(getResources().getColor(R.color.colorGreen));
                } else{
                    Score += 5;
                    JSONArray res = mainObject.getJSONArray("results");
                    JSONObject obj = res.getJSONObject(0);
                    final JSONObject lo = obj.getJSONObject("geometry");
                    JSONObject geo = lo.getJSONObject("location");
                    final String lat = geo.getString("lat");
                    final String lng = geo.getString("lng");
                    final String loc = obj.getString("name");
                    threat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + loc + ")";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                            startActivity(intent);
                        }
                    });
                    threat.setText("LIQUOR SHOP\n("+loc+")");
                    threat.setTypeface(null, Typeface.BOLD);
                    threat.setTextColor(getResources().getColor(R.color.colorAccent));
                }
                score.setText(Score+" /100");
            } catch (Exception ignored){}
            super.onPostExecute(result);
        }
    }
}
