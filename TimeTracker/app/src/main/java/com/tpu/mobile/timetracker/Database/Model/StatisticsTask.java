package com.tpu.mobile.timetracker.Database.Model;

import io.realm.RealmObject;

/**
 * Created by Igorek on 02.11.2017.
 */

public class StatisticsTask extends RealmObject {
    public int id;
    long duration;
    long start;
    long end;
    String note;

    public StatisticsTask()
    {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
