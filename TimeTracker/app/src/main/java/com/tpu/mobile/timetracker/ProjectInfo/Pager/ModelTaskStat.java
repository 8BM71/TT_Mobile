package com.tpu.mobile.timetracker.ProjectInfo.Pager;

import com.tpu.mobile.timetracker.Database.StatisticsTask;
import com.tpu.mobile.timetracker.Database.Task;

/**
 * Created by Igorek on 25.11.2017.
 */

public class ModelTaskStat {
    Task task;
    StatisticsTask stat;

    public ModelTaskStat()
    {
        task = null;
        stat = null;
    }

    public ModelTaskStat(Task task)
    {
        this.task =task;
        stat = null;
    }

    public ModelTaskStat(StatisticsTask stat)
    {
        task = null;
        this.stat = stat;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public StatisticsTask getStat() {
        return stat;
    }

    public void setStat(StatisticsTask stat) {
        this.stat = stat;
    }
}
