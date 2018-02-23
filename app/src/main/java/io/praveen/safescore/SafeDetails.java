package io.praveen.safescore;

public class SafeDetails {

    private int battery;
    private double latitude;
    private double longitude;
    private int ontime;
    private boolean threat;
    private boolean police;
    private String date;
    private int score;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getBattery() {
        return battery;
    }

    public boolean isThreat() {
        return threat;
    }

    public boolean isPolice() {
        return police;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getOntime() {
        return ontime;
    }

    public void setOntime(int ontime) {
        this.ontime = ontime;
    }

    public void setThreat(boolean threat) {
        this.threat = threat;
    }

    public void setPolice(boolean police) {
        this.police = police;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
