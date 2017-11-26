package com.ponomarevigor.androidgames.mytimetracker.Database;

import io.realm.RealmObject;

/**
 * Created by Igorek on 02.11.2017.
 */

public class StatisticsTask extends RealmObject {
    public int id;
    public static final int SET_AUTO = 0;
    public static final int SET_MANUAL = 1;

    int state;
    long durationAuto;
    long startAuto;
    long endAuto;
    long durationManual;
    long startManual;
    long endManual;

    String description;

    public StatisticsTask()
    {

    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getDurationAuto() {
        return durationAuto;
    }

    public void setDurationAuto(long durationAuto) {
        this.durationAuto = durationAuto;
    }

    public long getStartAuto() {
        return startAuto;
    }

    public void setStartAuto(long startAuto) {
        this.startAuto = startAuto;
    }

    public long getEndAuto() {
        return endAuto;
    }

    public void setEndAuto(long endAuto) {
        this.endAuto = endAuto;
    }

    public long getDurationManual() {
        return durationManual;
    }

    public void setDurationManual(long durationManual) {
        this.durationManual = durationManual;
    }

    public long getStartManual() {
        return startManual;
    }

    public void setStartManual(long startManual) {
        this.startManual = startManual;
    }

    public long getEndManual() {
        return endManual;
    }

    public void setEndManual(long endManual) {
        this.endManual = endManual;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
