package com.example.outworking;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.outworking.Compteur.Compteur;
import com.example.outworking.Compteur.OnUpdateListener;
import com.example.outworking.db.Workout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.function.Supplier;

public class play_workout extends AppCompatActivity implements OnUpdateListener {

    private TextView timerValue;

    private Compteur compteur;

    Workout workout;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_workout);

        workout = (Workout) getIntent().getSerializableExtra("WORKOUT");

        timerValue = (TextView) findViewById(R.id.temps_global);

        compteur = new Compteur();

        compteur.addOnUpdateListener(this);

        miseAJour();
    }

    private void miseAJour() {
        System.out.println("oui");
        // Affichage des informations du compteur
        timerValue.setText("" + compteur.getMinutes() + ":"
                + String.format("%02d", compteur.getSecondes()) + ":"
                + String.format("%03d", compteur.getMillisecondes()));

    }

    public void playTimer(View view) {
        FloatingActionButton playButton = (FloatingActionButton) findViewById(R.id.playButton);

        if(compteur.isPaused()){
            playButton.setImageResource(R.drawable.pause_solid);
            compteur.start();
        }else {
            playButton.setImageResource(R.drawable.play_solid);
            compteur.pause();
        }
    }

    @Override
    public void onUpdate() {
        miseAJour();
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
}