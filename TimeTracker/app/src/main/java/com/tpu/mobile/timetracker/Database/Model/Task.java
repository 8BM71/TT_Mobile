package com.tpu.mobile.timetracker.Database.Model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Igorek on 13.10.2017.
 */

public class Task extends RealmObject {
    public String id;
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
    public RealmList<StatisticsTask> stats;
    public String idActiveStat;
    public Project project;
    public String idProject;
    public Task()
    {
        state = TASK_CREATED;
        duration = 0;
        stats = new RealmList<StatisticsTask>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdProject() {
        return idProject;
    }

    public void setIdProject(String idProject) {
        this.idProject = idProject;
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

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = Long.parseLong(timeCreated);
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

    public void setTimeStart(String timeStart) {
        this.timeStart = Long.parseLong(timeStart);
    }

    public long getTimeFinish() {
        return timeFinish;
    }

    public void setTimeFinish(long timeFinish) {
        this.timeFinish = timeFinish;
    }

    public void setTimeFinish(String timeFinish) {
        this.timeFinish = Long.parseLong(timeFinish);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public RealmList<StatisticsTask> getStatistics() {
        return stats;
    }

    public void setStatistics(RealmList<StatisticsTask> statistics) {
        this.stats = statistics;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getIdActiveStat() {
        return idActiveStat;
    }

    public void setIdActiveStat(String idActiveStat) {
        this.idActiveStat = idActiveStat;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Task)) return false;

        Task task = (Task) obj;
        return this.getId().equals(task.getId());
                //&& this.getName().equals(task.getName()) &&
                //this.getDescription().equals(task.getDescription());
    }
}
