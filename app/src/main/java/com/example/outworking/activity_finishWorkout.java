package com.example.outworking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.outworking.db.Workout;

import java.util.HashMap;

public class activity_finishWorkout extends AppCompatActivity {

    // activities that user completed
    HashMap<String, Integer> activitiesCompleted;

    // corresponding workout
    Workout workout;

    LinearLayout activitiesCompletedLayout;

    // message to send if the button shared is pushed
    String messageToSend = "Récapitulatif de la séance outWorking\n";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_workout);

        workout = (Workout) getIntent().getSerializableExtra("WORKOUT");

        activitiesCompleted = (HashMap<String, Integer>) getIntent().getSerializableExtra("ACTIVITIESCOMPLETED");

        activitiesCompletedLayout = findViewById(R.id.activities_completed);

        HashMap<String, Integer> map = new HashMap<String, Integer>(){{
            put("Prepare", workout.getPrepare());
            put("Work", workout.getWork());
            put("Rest", workout.getRest());
            put("Rest between sets", workout.getRestBetweenSets());
            put("Cool Down", workout.getCoolDown());
        }};

        // build the resume of the user workout
        activitiesCompleted.forEach((String name, Integer number) -> {
            LayoutInflater inflater = LayoutInflater.from(activity_finishWorkout.this);
            View line = inflater.inflate(R.layout.template_finish_workout, null);

            TextView activityName = line.findViewById(R.id.activity_name);
            activityName.setText(name);

            TextView activityTime = line.findViewById(R.id.activity_time);

            activityTime.setText(map.get(name) + " sec");

            TextView activityNumber = line.findViewById(R.id.activity_number);
            activityNumber.setText("X" + number);

            activitiesCompletedLayout.addView(line);

            messageToSend += name + " " + map.get(name) + " sec" + "X" + number + "\n";
        });
    }

    // If the user want to go back without saving or hare
    public void goBackToWorkouts(View view){
        Intent myIntent = new Intent(activity_finishWorkout.this , activity_workouts.class);
        activity_finishWorkout.this.startActivity(myIntent);
    }

    // To save in the history of workout realized
    public void saveInHistory(View view){
        goBackToWorkouts(view);
    }

    // To share the workout that user did
    public void share(View view){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, messageToSend);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}