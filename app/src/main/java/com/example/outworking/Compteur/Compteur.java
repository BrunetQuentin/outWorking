package com.example.outworking.Compteur;

import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fbm on 24/10/2017.
 */
public class Compteur extends UpdateSource {

    // CONSTANTE
    private final static long INITIAL_TIME = 5000;

    // DATA
    private long updatedTime = 0;
    private CountDownTimer timer;   // https://developer.android.com/reference/android/os/CountDownTimer.html

    private boolean isPaused = true;
    private ArrayList<HashMap<String, Integer>> activities;

    int currentActivityIndex = 1;

    public Compteur(ArrayList<HashMap<String, Integer>> activities) {
        this.activities = activities;
        updateTime();
    }

    // Lancer le compteur
    public void start() {
        isPaused = false;

        if (timer == null) {

            // Créer le CountDownTimer
            timer = new CountDownTimer(updatedTime, 10) {

                // Callback fired on regular interval
                public void onTick(long millisUntilFinished) {
                    updatedTime = millisUntilFinished;

                    // Mise à jour
                    update();
                }

                // Callback fired when the time is up
                public void onFinish() {
                    updatedTime = 0;
                    startActivity(1);
                }

            }.start();   // Start the countdown
        }

    }

    public void startActivity(int index){
        stop();
        currentActivityIndex += index;
        // Mise à jour events
        updateActivity();
        update();

        updateTime();
        this.start();
    }

    // Mettre en pause le compteur
    public void pause() {

        if (timer != null) {

            // Arreter le timer
            stop();

            // Mise à jour
            update();
        }
    }

    public void updateTime(){
        updatedTime = Long.parseLong(activities.get(currentActivityIndex).values().toArray()[0].toString()) * 1000;
    }


    // Remettre à le compteur à la valeur initiale
    public void reset() {

        if (timer != null) {

            // Arreter le timer
            stop();
        }

        // Réinitialiser
        updatedTime = INITIAL_TIME;

        // Mise à jour
        update();

    }

    // Arrete l'objet CountDownTimer et l'efface
    private void stop() {
        isPaused = true;
        timer.cancel();
        timer = null;
    }

    public int getMinutes() {
        return (int) (updatedTime / 1000)/60;
    }

    public int getSecondes() {
        int secs = (int) (updatedTime / 1000);
        return secs % 60;
    }

    public int getMillisecondes() {
        return (int) (updatedTime % 1000);
    }

    public boolean isPaused(){return isPaused;}

    public String getActivtyName(){
        return activities.get(currentActivityIndex).keySet().toArray()[0].toString();
    }

    public int getCurrentActivityIndex(){
        return currentActivityIndex;
    }
}
