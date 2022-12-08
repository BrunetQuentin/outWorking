package com.example.outworking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.outworking.db.DatabaseClient;
import com.example.outworking.db.Workout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class activity_timer extends AppCompatActivity {

    Workout workout;

    private DatabaseClient db;

    private TextInputEditText activityName;

    String[] keys = new String[] { "Prepare", "Work", "Rest", "Cycles", "Sets", "Rest between sets", "Cool Down" };

    HashMap<String, Integer> ids;

    private boolean isNew = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        db = DatabaseClient.getInstance(getApplicationContext());

        workout = (Workout) getIntent().getSerializableExtra("WORKOUT");

        if(workout == null){
            isNew = true;
            workout = new Workout();
            // default params
            workout.setTitle("Nouvel exercice");
            workout.setPrepare(30);
            workout.setWork(20);
            workout.setRest(10);
            workout.setCycles(12);
            workout.setSets(3);
            workout.setRestBetweenSets(90);
            workout.setCoolDown(60);
        }

        LinearLayout timer = (LinearLayout) findViewById(R.id.activity_timer);

        activityName = (TextInputEditText) findViewById(R.id.activityName);
        activityName.setText(workout.getTitle());

        ids = new HashMap<String, Integer>();

        for (String key : keys) {
            LayoutInflater inflater = LayoutInflater.from(activity_timer.this); // 1
            View line = inflater.inflate(R.layout.template_timer, null);

            TextView title = (TextView) line.findViewById(R.id.title);
            title.setText(key);

            FloatingActionButton buttonAdd = (FloatingActionButton) line.findViewById(R.id.plus);
            FloatingActionButton buttonMinus = (FloatingActionButton) line.findViewById(R.id.minus);

            EditText text = (EditText) line.findViewById(R.id.editNumber);

            int id = View.generateViewId();
            ids.put(key, id);
            text.setId(id);

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    text.setText(Integer.toString(Integer.parseInt(String.valueOf(text.getText())) + 1));
                }
            });

            buttonMinus.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    text.setText(Integer.toString(Integer.parseInt(String.valueOf(text.getText())) - 1));
                }
            });
            text.setText(Integer.toString(getValue(key)));

            timer.addView(line);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void modifyValue(String key, int number) {

        HashMap<String, Runnable> map = new HashMap<String, Runnable>(){{
            put("Prepare", () -> workout.setPrepare(number));
            put("Work", () -> workout.setWork(number));
            put("Rest", () -> workout.setRest(number));
            put("Cycles", () -> workout.setCycles(number));
            put("Sets", () -> workout.setSets(number));
            put("Rest between sets", () -> workout.setRestBetweenSets(number));
            put("Cool Down", () -> workout.setCoolDown(number));
        }};

        map.get(key).run();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public int getValue(String key) {
        HashMap<String, Supplier<Integer>> map = new HashMap<String, Supplier<Integer>>(){{
            put("Prepare", () -> workout.getPrepare());
            put("Work", () -> workout.getWork());
            put("Rest", () -> workout.getRest());
            put("Cycles", () -> workout.getCycles());
            put("Sets", () -> workout.getSets());
            put("Rest between sets", () -> workout.getRestBetweenSets());
            put("Cool Down", () -> workout.getCoolDown());
        }};
        return map.get(key).get();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveObjectWorkout(View view){
        for(Map.Entry<String, Integer> entry : ids.entrySet()){
            EditText text =  findViewById(entry.getValue());
            modifyValue(entry.getKey(),Integer.parseInt(String.valueOf(text.getText())));
        }
        workout.setTitle(activityName.getText().toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void playWorkout(View view) {
        saveObjectWorkout(view);
        Intent playWorkout = new Intent(activity_timer.this, play_workout.class);
        playWorkout.putExtra("WORKOUT", (Serializable) workout); //Optional parameters
        activity_timer.this.startActivity(playWorkout);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveInDbWorkout(View view) {
        class SaveInDbWorkout extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                if(isNew){
                    db.getAppDatabase().workoutDao().insert(workout);
                }else {
                    db.getAppDatabase().workoutDao().update(workout);
                }
                return null;
            }
        }

        this.saveObjectWorkout(view);
        SaveInDbWorkout sw = new SaveInDbWorkout();
        sw.execute();

        Intent activityWorkouts = new Intent(activity_timer.this, activity_workouts.class);
        activity_timer.this.startActivity(activityWorkouts);
    }
}