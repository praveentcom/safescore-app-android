package io.praveen.safescore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.preference.PreferenceManager;

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
import java.util.concurrent.ExecutionException;

import static android.content.Context.BATTERY_SERVICE;

public class SafeScoreUtils {

    private Context c;
    SharedPreferences preferences;
    float lat, lon, in, out;
    boolean t = false, p = false;

    SafeScoreUtils(Context context){
        c = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(c);
        lat = preferences.getFloat("lat", 0);
        lon = preferences.getFloat("lon", 0);
        in = preferences.getInt("in", 8);
        out = preferences.getInt("out", 18);
    }

    public int getBattery(){
        BatteryManager bm = (BatteryManager) c.getSystemService(BATTERY_SERVICE);
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    @SuppressLint("MissingPermission")
    public double getLatitude(){
        LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        Location loc = null;
        if (locationManager != null) {
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (loc != null) {
            return loc.getLatitude();
        } else{
            return 0;
        }
    }

    @SuppressLint("MissingPermission")
    public double getLongitude(){
        LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        Location loc = null;
        if (locationManager != null) {
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (loc != null) {
            return loc.getLongitude();
        } else{
            return 0;
        }
    }

    public int ontime(double lat1, double lon1){
        double distance = distance(lat, lat1, lon, lon1);
        DecimalFormat _numberFormat= new DecimalFormat("#0.0");
        float mDist = Float.parseFloat(_numberFormat.format((float) distance/1000));
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df2 = new SimpleDateFormat("HH", Locale.ENGLISH);
        int mTime = Integer.valueOf(df2.format(c.getTime()));
        if (mTime > in && mTime < out){
            return 1;
        } else{
            if (mDist > 5){
                return 0;
            } else{
                return 2;
            }
        }
    }

    public boolean threat(double lat1, double lon1) throws ExecutionException, InterruptedException {
        String r = new Json2().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat1+","+lon1+"&radius=2000&type=liquor_store&key=AIzaSyCczblqj3aNVsRde-4oin7FnGmyfMpEx3c").get();
        return t;
    }

    public boolean police(double lat1, double lon1) throws ExecutionException, InterruptedException {
        String r = new Json().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat1+","+lon1+"&radius=2000&type=police&key=AIzaSyCczblqj3aNVsRde-4oin7FnGmyfMpEx3c").get();
        return p;
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
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
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
                    p = false;
                } else{
                    p = true;
                }
            } catch (Exception ignored){}
            super.onPostExecute(result);
        }
    }
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
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
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
                    t = false;
                } else{
                    t = true;
                }
            } catch (Exception ignored){}
            super.onPostExecute(result);
        }
    }

}
