package com.example.outworking;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.outworking.db.Workout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

public class activity_timer extends AppCompatActivity {

    Workout workout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        workout = (Workout) getIntent().getSerializableExtra("WORKOUT");

        if(workout == null){
            workout = new Workout();
        }

        LinearLayout timer = (LinearLayout) findViewById(R.id.activity_timer);

        HashMap<String, Integer> map = new HashMap<String, Integer>(){{
            put("Prepare", workout.getPrepare());
            put("Work", workout.getWork());
            put("Rest", workout.getRest());
            put("Cycles", workout.getCycles());
            put("Sets", workout.getSets());
            put("Rest between sets", workout.getRestBetweenSets());
            put("Cool Down", workout.getCoolDown());
        }};

        for (String key : map.keySet()) {
            LayoutInflater inflater = LayoutInflater.from(activity_timer.this); // 1
            View line = inflater.inflate(R.layout.template_timer, null);

            TextView title = (TextView) line.findViewById(R.id.title);
            title.setText(key);

            EditText text = (EditText) line.findViewById(R.id.editNumber);
            System.out.println(map.get(key));
            text.setText(map.get(key).toString());

            timer.addView(line);
        }
    }
}