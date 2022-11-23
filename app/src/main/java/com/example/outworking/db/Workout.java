package com.example.outworking.db;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "workout")
public class Workout implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;

    private int prepare;

    private int work;

    private int rest;

    private int cycles;

    private int sets;

    private int restBetweenSets;

    private int coolDown;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrepare() {
        return prepare;
    }

    public void setPrepare(int prepare) {
        this.prepare = prepare;
    }

    public int getWork() {
        return work;
    }

    public void setWork(int work) {
        this.work = work;
    }

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public int getCycles() {
        return cycles;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getRestBetweenSets() {
        return restBetweenSets;
    }

    public void setRestBetweenSets(int restBetweenSets) {
        this.restBetweenSets = restBetweenSets;
    }

    public int getCoolDown() {
        return coolDown;
    }

    public void setCoolDown(int coolDown) {
        this.coolDown = coolDown;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}