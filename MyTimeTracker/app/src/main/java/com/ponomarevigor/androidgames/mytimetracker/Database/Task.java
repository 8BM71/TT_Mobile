package com.ponomarevigor.androidgames.mytimetracker.Database;

import io.realm.RealmObject;

/**
 * Created by Igorek on 13.10.2017.
 */

public class Task extends RealmObject {
    public String name;
    public String description;

    public long timeCreated;
    public long timeStart;
    public long timePause;
    public long timeFinish;

    public static final int TASK_CREATED = 0;
    public static final int TASK_RUNNING = 1;
    public static final int TASK_PAUSED = 2;
    public static final int TASK_STOPPED = 3;

    public int state;
    public long duration;

    public int position;

    public Task()
    {
        state = TASK_CREATED;
        duration = 0;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimeStart() {
        long currentTime = System.currentTimeMillis();
        long step = currentTime - timeStart;
        long d = getDuration() + step;
        return d;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
