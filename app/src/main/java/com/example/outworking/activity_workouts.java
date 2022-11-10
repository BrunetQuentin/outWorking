package com.example.outworking;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.example.outworking.db.AppDatabase;
import com.example.outworking.db.DatabaseClient;
import com.example.outworking.db.Workout;

import java.util.ArrayList;
import java.util.List;

public class activity_workouts extends AppCompatActivity {

    private DatabaseClient db;

    private ListView listWorkout;

    private WorkoutAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);

        db = DatabaseClient.getInstance(getApplicationContext());

        List<Workout> workouts = db.getAppDatabase().workoutDao().getAll();

        listWorkout = findViewById(R.id.listWorkouts);

        adapter = new WorkoutAdapter(this, new ArrayList<Workout>());
        listWorkout.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Mise à jour des workout
        getWorkouts();

    }

    private void getWorkouts() {
        class getWorkouts extends AsyncTask<Void, Void, List<Workout>> {

            @Override
            protected List<Workout> doInBackground(Void... voids) {
                List<Workout> workoutList = db.getAppDatabase()
                        .workoutDao()
                        .getAll();
                return workoutList;
            }

            @Override
            protected void onPostExecute(List<Workout> workouts) {
                super.onPostExecute(workouts);

                // Mettre à jour l'adapter avec la liste des workouts
                adapter.clear();
                adapter.addAll(workouts);
                adapter.notifyDataSetChanged();
            }
        }
    }
}