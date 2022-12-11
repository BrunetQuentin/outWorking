package com.example.outworking;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.outworking.Compteur.Compteur;
import com.example.outworking.Compteur.OnUpdateListener;
import com.example.outworking.db.Workout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;

public class play_workout<activities> extends AppCompatActivity implements OnUpdateListener {

    private TextView timerValue;

    private TextView activityName;

    private TextView activityValue;

    private TextView activityCompteur;

    private FloatingActionButton lockedButton;

    private Compteur compteur;

    private Workout workout;

    private LinearLayout displayActivities;

    private ScrollView scrollActivities;

    private FloatingActionButton playButton;

    private ArrayList<HashMap<String, Integer>> activities;

    boolean isLocked = false;

    private HashMap<String, Integer> numberOfActivitiesCompleted;

    private MediaPlayer mp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_workout);

        workout = (Workout) getIntent().getSerializableExtra("WORKOUT");

        timerValue = (TextView) findViewById(R.id.temps_global);
        activityName = (TextView) findViewById(R.id.activityName);
        activityValue = (TextView) findViewById(R.id.temp_activite);
        activityCompteur = (TextView) findViewById(R.id.compteur_activite);

        displayActivities = (LinearLayout) findViewById(R.id.display_activities);

        scrollActivities = (ScrollView) findViewById(R.id.scrollActivities);
        scrollActivities.setSmoothScrollingEnabled(true);

        playButton = (FloatingActionButton) findViewById(R.id.playButton);

        lockedButton = (FloatingActionButton) findViewById(R.id.lockedButton);

        activities = new ArrayList<HashMap<String, Integer>>();

        numberOfActivitiesCompleted = new HashMap<>();

        mp = MediaPlayer.create(this, R.raw.beep);

        HashMap<String, Integer> map = new HashMap<String, Integer>(){{
            put("Prepare", workout.getPrepare());
            put("Work", workout.getWork());
            put("Rest", workout.getRest());
            put("Cycles", workout.getCycles());
            put("Sets", workout.getSets());
            put("Rest between sets", workout.getRestBetweenSets());
            put("Cool Down", workout.getCoolDown());
        }};

        String[] ordre = new String[]{
            "Prepare",
            "Work",
            "Rest",
            "Cycles",
            "Rest between sets",
            "Sets",
            "Cool Down",
        };
        // Creation des activités
        int index = 0;
        int cycles = workout.getCycles();
        int sets = workout.getSets();
        if(cycles == 0) cycles = 1;
        if(sets == 0) sets = 1;
        sets--; // au moins un set qu'on execute toujours
        cycles--; // au moins un cycle qu'on execute toujours
        for (int i = 0; i < ordre.length; i++) {
            if(ordre[i] == "Cycles") {
                if(cycles != 0) {
                    i -= 3; // on recul de trois pour un cycle
                    cycles--;
                }
                continue;
            }
            if(ordre[i] == "Sets") {
                if(sets != 0) {
                    cycles = workout.getCycles();
                    if(cycles == 0) cycles = 1;
                    cycles--;
                    i -= 5; // on recul de cinq pour un set
                    sets--;
                }
                continue;
            }
            addActivity(index, ordre[i], map.get(ordre[i]));
            index++;
            int finalI = i;
            activities.add(index-1, new HashMap<String, Integer>() {{
                put(ordre[finalI], map.get(ordre[finalI]));
            }});
        }

        // Creation du compteur
        compteur = new Compteur(activities);
        // abbonnement aux eventlistener
        compteur.addOnUpdateListener(this);
        // update the graphical object
        compteur.startActivity(0);

        miseAJour();
    }

    private void miseAJour() {
        // Affichage des informations du compteur
        timerValue.setText("" + compteur.getTotalMinutes() + ":"
                + String.format("%02d", compteur.getTotalSecondes()) + ":"
                + String.format("%03d", compteur.getTotalMillisecondes()));
        activityValue.setText(compteur.getMinutes() + ":" + String.format("%02d", compteur.getSecondes()));
    }

    // Méthode invoqué lors du changement d'une activitée
    private void miseAJourActivity() {
        // Affiche le nom de l'activité
        activityName.setText(compteur.getActivtyName());
        activityCompteur.setText((compteur.getCurrentActivityIndex() + 1) + "/" + compteur.getNumberOfActivities());

        View activityLine = displayActivities.findViewById(compteur.getCurrentActivityIndex() - 1);
        if(activityLine == null) return;
        scrollActivities.smoothScrollTo(0, (int)activityLine.getY());

        //final MediaPlayer mp = MediaPlayer.create(this, R.raw.);
        //mp.start();
    }

    // pause or start the timer
    public void playTimer(View view) {
        if(isLocked) return; // if the applciation is locked do nothing
        if(compteur.isPaused()){
            compteur.start();
        }else {
            compteur.pause();
        }
    }

    // mise a jour des timers
    @Override
    public void onUpdate() {
        miseAJour();
    }

    // mise a jour de l'activité
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onUpdateActivity() {
        miseAJourActivity();
    }

    // mise a jour du bouton play/pause
    @Override
    public void onStatusChange() {
        if(compteur.isPaused()){
            playButton.setImageResource(R.drawable.play_solid);
        }else {
            playButton.setImageResource(R.drawable.pause_solid);
        }
    }

    // mise a jour de l'activité quand elle est terminée
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onActivityFinish(){
        View activityLine = displayActivities.findViewById(compteur.getCurrentActivityIndex());
        if(activityLine != null){
            activityLine.setBackgroundColor(R.color.purple_200);
        }
        if(numberOfActivitiesCompleted.get(compteur.getActivtyName()) == null) numberOfActivitiesCompleted.put(compteur.getActivtyName(), 0);
        numberOfActivitiesCompleted.put(compteur.getActivtyName(), numberOfActivitiesCompleted.get(compteur.getActivtyName()) + 1);

        mp.start();
    }

    // Start the activity finish when the last activity is finished
    @Override
    public void onFinish() {
        Intent myIntent = new Intent(play_workout.this , activity_finishWorkout.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        myIntent.putExtra("ACTIVITIESCOMPLETED", numberOfActivitiesCompleted);
        myIntent.putExtra("WORKOUT", workout);
        play_workout.this.startActivity(myIntent);
    }

    // add activity in the list of activities
    public void addActivity(int index, String activity, int time){
        LayoutInflater inflater = LayoutInflater.from(play_workout.this); // 1
        View line = inflater.inflate(R.layout.template_play_workout, null);

        TextView indexView = line.findViewById(R.id.index);
        indexView.setText((index + 1) + ".");

        TextView activityView = line.findViewById(R.id.activity);
        activityView.setText(activity + ".");

        TextView timeView = line.findViewById(R.id.time);
        timeView.setText(Integer.toString(time));

        line.setId(index);

        displayActivities.addView(line);
    }

    // go forward in the activities
    public void goForward(View view){
        if(isLocked) return;
        compteur.startActivity(1);
    }

    // go backward in the activities
    public void goBackward(View view){
        if(isLocked) return;
        compteur.startActivity(-1);
    }

    // locak or unlock the application
    public void lockUnlock(View view){
        isLocked = !isLocked;
        if(isLocked){
            lockedButton.setImageResource(R.drawable.lock_solid);
        }else {
            lockedButton.setImageResource(R.drawable.lock_open_solid);
        }
    }

    // Invocked when the orientation is changed
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            System.out.println("ORIENTATION LANDSCAPE");
        } else {
            System.out.println("ORIENTATION PORTRAIT");
        }
    }

    @Override
    protected void onStop() {

        super.onStop();

        compteur.pause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        compteur.stop();

        this.finish();
    }
}