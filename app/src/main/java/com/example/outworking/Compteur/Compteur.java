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

    int currentActivityIndex = 0;

    long remainingTimeUtilFinished = 0;

    private int numberOfActivities = 0;

    public Compteur(ArrayList<HashMap<String, Integer>> activities) {
        this.activities = activities;

        numberOfActivities = activities.size();

        for (HashMap<String, Integer> activity: activities) {
            remainingTimeUtilFinished += Long.parseLong(activity.values().toArray()[0].toString()) * 1000;
        }
        // minus the first activity as this is always playing
        remainingTimeUtilFinished -= Long.parseLong(activities.get(0).values().toArray()[0].toString()) * 1000;
    }

    // Lancer le compteur
    public void start() {
        isPaused = false;
        onStatusChange();

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
                    onActivityFinish();
                    startActivity(1);
                }

            }.start();   // Start the countdown
        }

    }

    // index == 1 go forward
    // index == -1 go backward
    // index == 0 reload the activity with callbacks
    public void startActivity(int index){
        boolean paused = isPaused;
        if((currentActivityIndex + index) < 0){
            reset(paused);
            return;
        }
        if((currentActivityIndex + index) >= activities.size()){
            updatedTime = 0;
            update();
            stop();
            return;
        }
        if(index != 0) onActivityFinish();
        stop();
        currentActivityIndex += index;
        remainingTimeUtilFinished -= Long.parseLong(activities.get(currentActivityIndex).values().toArray()[0].toString()) * 1000 * index;
        // Mise à jour events
        updateActivity();
        updateTime();
        update();
        this.start();
        if(paused) this.pause();
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
    public void reset(boolean paused) {

        if (timer != null) {

            // Arreter le timer
            stop();
        }

        // Réinitialiser
        updateTime();

        // Mise à jour
        update();
        this.start();
        if(paused) this.pause();
    }

    // Arrete l'objet CountDownTimer et l'efface
    private void stop() {
        if(timer != null){
            isPaused = true;
            onStatusChange();
            timer.cancel();
            timer = null;
        }
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

    public int getTotalMinutes() {
        return (int) ((remainingTimeUtilFinished + updatedTime) / 1000)/60;
    }

    public int getTotalSecondes() {
        int secs = (int) ((remainingTimeUtilFinished + updatedTime) / 1000);
        return secs % 60;
    }

    public int getTotalMillisecondes() {
        return (int) ((remainingTimeUtilFinished + updatedTime) % 1000);
    }

    public boolean isPaused(){return isPaused;}

    public String getActivtyName(){
        return activities.get(currentActivityIndex).keySet().toArray()[0].toString();
    }

    public int getCurrentActivityIndex(){
        return currentActivityIndex;
    }

    public int getNumberOfActivities(){return numberOfActivities;}
}
