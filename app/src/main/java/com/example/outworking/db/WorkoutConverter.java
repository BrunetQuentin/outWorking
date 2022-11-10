package com.example.outworking.db;

import androidx.room.ProvidedTypeConverter;
import androidx.room.TypeConverter;

@ProvidedTypeConverter
public class WorkoutConverter {
    @TypeConverter
    public Workout StringToWorkout(String string) {
        return new Workout();
    }

    @TypeConverter
    public String ExampleToString(Workout workout) {
        return "";
    }
}