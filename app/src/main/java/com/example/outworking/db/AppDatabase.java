package com.example.outworking.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Workout.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WorkoutDao workoutDao();

}