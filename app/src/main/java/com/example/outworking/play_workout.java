package com.example.outworking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

    Workout workout;

    LinearLayout displayActivities;

    ScrollView scrollActivities;

    FloatingActionButton playButton;

    ArrayList<HashMap<String, Integer>> activities;

    boolean isLocked = false;

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
                    i -= 3;
                    cycles--;
                }
                continue;
            }
            if(ordre[i] == "Sets") {
                if(sets != 0) {
                    cycles = workout.getCycles();
                    if(cycles == 0) cycles = 1;
                    cycles--;
                    i -= 5;
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

        compteur = new Compteur(activities);
        compteur.addOnUpdateListener(this);
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

    private void miseAJourActivity() {
        // Affiche le nom de l'activité
        activityName.setText(compteur.getActivtyName());
        activityCompteur.setText((compteur.getCurrentActivityIndex() + 1) + "/" + compteur.getNumberOfActivities());
    }

    public void playTimer(View view) {
        if(isLocked) return;
        if(compteur.isPaused()){
            compteur.start();
        }else {
            compteur.pause();
        }
    }

    @Override
    public void onUpdate() {
        miseAJour();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onUpdateActivity() {
        miseAJourActivity();
    }

    @Override
    public void onStatusChange() {
        if(compteur.isPaused()){
            playButton.setImageResource(R.drawable.play_solid);
        }else {
            playButton.setImageResource(R.drawable.pause_solid);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onActivityFinish(){
        View activityLine = displayActivities.findViewById(compteur.getCurrentActivityIndex());
        if(activityLine != null){
            activityLine.setBackgroundColor(R.color.purple_200);
            scrollActivities.smoothScrollTo(0, (int)activityLine.getY());
        }
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

    public void goForward(View view){
        if(isLocked) return;
        compteur.startActivity(1);
    }

    public void goBackward(View view){
        if(isLocked) return;
        compteur.startActivity(-1);
    }

    public void lockUnlock(View view){
        isLocked = !isLocked;
        if(isLocked){
            lockedButton.setImageResource(R.drawable.lock_solid);
        }else {
            lockedButton.setImageResource(R.drawable.lock_open_solid);
        }
    }
}