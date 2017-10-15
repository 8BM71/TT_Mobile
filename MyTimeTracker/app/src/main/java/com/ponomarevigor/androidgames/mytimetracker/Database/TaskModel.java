package com.ponomarevigor.androidgames.mytimetracker.Database;

import io.realm.RealmObject;

/**
 * Created by Igorek on 13.10.2017.
 */

public class TaskModel extends RealmObject
{
    public String name;
    public String description;

    public long timeCreated;
    public long timeStart;
    public long timePause;
    public long timeFinish;

    public long duration;

    public static final int TASK_CREATED = 0;
    public static final int TASK_RUNNING = 1;
    public static final int TASK_PAUSED = 2;
    public static final int TASK_STOPPED = 3;

    public int state;

    public TaskModel()
    {

    }

    public TaskModel(String name, String description)
    {
        this.name = name;
        this.description = description;
        state = 0;
        duration = 0;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimePause() {
        return timePause;
    }

    public void setTimePause(long timePause) {
        this.timePause = timePause;
    }

    public long getTimeFinish() {
        return timeFinish;
    }

    public void setTimeFinish(long timeFinish) {
        this.timeFinish = timeFinish;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}