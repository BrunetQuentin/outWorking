package com.example.outworking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.outworking.db.Workout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WorkoutAdapter extends ArrayAdapter<Workout> {

    public WorkoutAdapter(Context mCtx, List<Workout> workoutList) {
        super(mCtx, R.layout.template_workout, workoutList);
    }

    /**
     * Remplit une ligne de la listView avec les informations de la multiplication associée
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

        int index = 1;

        for (String key : map.keySet()) {
            TextView line = new TextView(getContext());
            line.setText(index + ". " + key + ": " + map.get(key) + " sec");
            index++;
        }
        return rowView;
    }

}
