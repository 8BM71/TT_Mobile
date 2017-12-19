package com.tpu.mobile.timetracker.Database;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Igorek on 13.10.2017.
 */

public class Task extends RealmObject {
    public int id;

    public String name;
    public String description;

    public long timeCreated;
    public long timeStart;
    public long timeFinish;

    public static final int TASK_CREATED = 0;
    public static final int TASK_RUNNING = 1;
    public static final int TASK_STOPPED = 2;

    public int state;
    public long duration;

    public RealmList<StatisticsTask> statistics;
    public Project project;

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

    public RealmList<StatisticsTask> getStatistics() {
        return statistics;
    }

    public void setStatistics(RealmList<StatisticsTask> statistics) {
        this.statistics = statistics;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
