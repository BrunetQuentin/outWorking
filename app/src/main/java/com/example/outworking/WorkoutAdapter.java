package com.example.outworking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.outworking.db.DatabaseClient;
import com.example.outworking.db.Workout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;


public class WorkoutAdapter extends ArrayAdapter<Workout> {

    private DatabaseClient db;

    public WorkoutAdapter(Context mCtx, List<Workout> workoutList) {
        super(mCtx, R.layout.template_workout, workoutList);
        db = DatabaseClient.getInstance(mCtx);
    }

    /**
     * Remplit une ligne de la listView avec les informations du workout
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Récupération du workout en question
        final Workout workout = getItem(position);

        // Charge le template XML
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.template_workout, parent, false);

        // Récupération des objets graphiques dans le template
        TextView textViewName = (TextView) rowView.findViewById(R.id.workoutName);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout linearProg = (LinearLayout) rowView.findViewById(R.id.workoutPro);

        //
        textViewName.setText(workout.getTitle());

        HashMap<String, Integer> map = new HashMap<String, Integer>(){{
            put("Prepare", workout.getPrepare());
            put("Work", workout.getWork());
            put("Rest", workout.getRest());
            put("Cycles", workout.getCycles());
            put("Sets", workout.getSets());
            put("Rest between sets", workout.getRestBetweenSets());
            put("Cool Down", workout.getCoolDown());
        }};

        LinearLayout activityDetail = (LinearLayout) rowView.findViewById(R.id.workoutPro);

        int index = 1;

        for (String key : map.keySet()) {
            TextView line = new TextView(getContext());
            line.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            line.setText(index + ". " + key + ": " + map.get(key) + " sec");
            activityDetail.addView(line);
            index++;
        }

        TextView detail = new TextView(getContext());
        int total = (workout.getWork() + workout.getRest()) * workout.getCycles();
        int secTotal = total % 60;
        int minTotal = (total / 60) % 60;
        detail.setText("Total: " + minTotal + ":" + secTotal + " * " + workout.getCycles() + " * " + workout.getSets());
        detail.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        activityDetail.addView(detail);

        FloatingActionButton playWorkshopButton = rowView.findViewById(R.id.playWorkshopButton);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) FloatingActionButton editWorkshopButton = rowView.findViewById(R.id.editWorkshopButton);
        FloatingActionButton deleteWorkshopButton = rowView.findViewById(R.id.DeleteWorkshopButton);

        // play the workout
        playWorkshopButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(getContext() , play_workout.class);
            myIntent.putExtra("WORKOUT", workout);
            getContext().startActivity(myIntent);
        });

        // edit the workout
        editWorkshopButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(getContext() , activity_timer.class);
            myIntent.putExtra("WORKOUT", workout);
            getContext().startActivity(myIntent);
        });

        // delete the workout
        deleteWorkshopButton.setOnClickListener(view -> {
            deleteWorkout(workout);
            this.remove(workout);
        });

        return rowView;
    }

    // Méthode pour supprimer le workout
    public void deleteWorkout(Workout workout) {
        class DeleteInDbWorkout extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                db.getAppDatabase().workoutDao().delete(workout);
                return null;
            }
        }
        DeleteInDbWorkout dw = new DeleteInDbWorkout();
        dw.execute();
    }

}
