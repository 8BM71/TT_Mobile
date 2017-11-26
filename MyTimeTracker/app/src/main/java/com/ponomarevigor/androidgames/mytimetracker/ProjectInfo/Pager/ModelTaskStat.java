package com.ponomarevigor.androidgames.mytimetracker.ProjectInfo.Pager;

import com.ponomarevigor.androidgames.mytimetracker.Database.StatisticsTask;
import com.ponomarevigor.androidgames.mytimetracker.Database.Task;

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
