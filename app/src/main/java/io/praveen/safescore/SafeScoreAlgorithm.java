package io.praveen.safescore;

public class SafeScoreAlgorithm {

    /*  Each of this 5 Parameters are given 20 Marks based on the values.

        1. Battery

            20 - 90% and above
            16 - 75% and above
            12 - 50% and above
            8 - 25% and above
            4 - 10% and above

        2. Behaviour

            20 - For unchanged Behaviours in mobile
            10 - For behavioural changes in mobile like SIM Change

        3. On-Time

            20 - If in home during non-working hours
            15 - If not in home during working hours
            5 - If not in home during non-working hours

        4. Threat

            20 - If no threats like Wine Shops found within 2 KM radius
            10 - If few/many threats found near 2 KM radius

        5. Police

            20 - If police station was found within 2 KM radius
            10 - If no police station found near 2 KM radius  */

    private int battery;
    private int ontime;
    private boolean threat;
    private boolean police;

    SafeScoreAlgorithm(int battery, int ontime, boolean threat, boolean police){
        this.battery = battery;
        this.ontime = ontime;
        this.threat = threat;
        this.police = police;
    }

    public int getScore(){
        int score = 20;
        if (battery > 90){
            score += 20;
        } else if (battery > 75){
            score += 16;
        } else if (battery > 50){
            score += 12;
        } else if (battery > 25){
            score += 8;
        } else if (battery > 10){
            score += 4;
        }
        if (ontime == 0){
            score += 5;
        } else if (ontime == 1){
            score += 10;
        } else if (ontime == 2){
            score += 20;
        }
        if (police){
            score += 20;
        } else{
            score += 5;
        }
        if (threat){
            score += 5;
        } else {
            score += 20;
        }
        return score;
    }

}
