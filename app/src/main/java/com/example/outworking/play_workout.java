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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class play_workout<activities> extends AppCompatActivity implements OnUpdateListener {

    private TextView timerValue;

    private TextView activityValue;

    private Compteur compteur;

    Workout workout;

    LinearLayout displayActivities;

    ScrollView scrollActivities;

    FloatingActionButton playButton;

    ArrayList<HashMap<String, Integer>> activities;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_workout);

        workout = (Workout) getIntent().getSerializableExtra("WORKOUT");

        timerValue = (TextView) findViewById(R.id.temps_global);
        activityValue = (TextView) findViewById(R.id.activityName);

        displayActivities = (LinearLayout) findViewById(R.id.display_activities);

        scrollActivities = (ScrollView) findViewById(R.id.scrollActivities);
        scrollActivities.setSmoothScrollingEnabled(true);

        playButton = (FloatingActionButton) findViewById(R.id.playButton);

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
            addActivity(index, ordre[i], map.get(ordre[i]), "Run");
            index++;
            int finalI = i;
            activities.add(index-1, new HashMap<String, Integer>() {{
                put(ordre[finalI], map.get(ordre[finalI]));
            }});
        }

        compteur = new Compteur(activities);
        compteur.addOnUpdateListener(this);

        miseAJour();
    }

    private void miseAJour() {
        // Affichage des informations du compteur
        timerValue.setText("" + compteur.getMinutes() + ":"
                + String.format("%02d", compteur.getSecondes()) + ":"
                + String.format("%03d", compteur.getMillisecondes()));

    }

    private void miseAJourActivity() {
        // Affiche le nom de l'activité
        activityValue.setText(compteur.getActivtyName());
    }

    public void playTimer(View view) {
        FloatingActionButton playButton = (FloatingActionButton) findViewById(R.id.playButton);

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
        System.out.println(compteur.getCurrentActivityIndex());
        View activityLine = displayActivities.findViewById(compteur.getCurrentActivityIndex());
        if(activityLine != null){
            activityLine.setBackgroundColor(R.color.purple_200);
            scrollActivities.smoothScrollTo(0, (int)activityLine.getY());
        }
    }

    public void affichageExercices(){
        LinearLayout displayActivities = (LinearLayout) findViewById(R.id.display_activities);

        HashMap<String, Supplier<Integer>> map = new HashMap<String, Supplier<Integer>>(){{
            put("Prepare", () -> workout.getPrepare());
            put("Work", () -> workout.getWork());
            put("Rest", () -> workout.getRest());
            put("Cycles", () -> workout.getCycles());
            put("Sets", () -> workout.getSets());
            put("Rest between sets", () -> workout.getRestBetweenSets());
            put("Cool Down", () -> workout.getCoolDown());
        }};
    }


    // add activity in the list of activities
    public void addActivity(int index, String activity, int time, String detail){
        LayoutInflater inflater = LayoutInflater.from(play_workout.this); // 1
        View line = inflater.inflate(R.layout.template_play_workout, null);

        TextView indexView = line.findViewById(R.id.index);
        indexView.setText((index + 1) + ".");

        TextView activityView = line.findViewById(R.id.activity);
        activityView.setText(activity + ".");

        TextView timeView = line.findViewById(R.id.time);
        timeView.setText(Integer.toString(time));

        TextView detailView = line.findViewById(R.id.detail);
        detailView.setText(detail);

        line.setId(index);

        displayActivities.addView(line);
    }

    public void goForward(View view){
        compteur.startActivity(1);
    }

    public void goBackward(View view){
        compteur.startActivity(-1);
    }
}