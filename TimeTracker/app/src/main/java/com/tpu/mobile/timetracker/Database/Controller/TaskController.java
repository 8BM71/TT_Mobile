package com.tpu.mobile.timetracker.Database.Controller;

import com.tpu.mobile.timetracker.Database.Model.Project;
import com.tpu.mobile.timetracker.Database.Model.StatisticsTask;
import com.tpu.mobile.timetracker.Database.Model.Task;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Igorek on 25.12.2017.
 */

public class TaskController {
    private Realm realm;

    public TaskController(Realm realm)
    {
        this.realm = realm;
    }

    public void createTask(int id, String name, String description, int idProject)
    {
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setTimeCreated(System.currentTimeMillis());
        task.setState(Task.TASK_CREATED);
        task.setDuration(0);
        task.setId(id);
        if (idProject == 1)
            task.setProject(realm.where(Project.class).equalTo("id", 1).findFirst());
        else
            task.setProject(realm.where(Project.class).equalTo("id", idProject).findFirst());
        realm.beginTransaction();
        realm.copyToRealm(task);
        realm.commitTransaction();
    }

    public void addTask(Task task)
    {
        task.setProject(realm.where(Project.class).equalTo("id", 1).findFirst());
        realm.beginTransaction();
        realm.copyToRealm(task);
        realm.commitTransaction();
    }

    public Task getTask(int idTask)
    {
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        return task;
    }

    public List<Task> getTasks()
    {
        List<Task> tasks = realm.where(Task.class).findAll();
        return tasks;
    }

    public List<Task> getTasksOfProject(int idProject)
    {
        List<Task> tasks = realm.where(Task.class).equalTo("project.id", idProject).findAllSorted("id");
        return tasks;
    }

    public void startTask(int idTask)
    {
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        realm.beginTransaction();
        task.setTimeCreated(System.currentTimeMillis());
        task.setTimeStart(System.currentTimeMillis());
        task.setState(Task.TASK_RUNNING);
        realm.commitTransaction();
    }

    public void finishTask(int idTask)
    {
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        realm.beginTransaction();
        task.setState(Task.TASK_STOPPED);
        task.setTimeFinish(System.currentTimeMillis());
        StatisticsTask stat = realm.createObject(StatisticsTask.class);
        stat.setId(realm.where(StatisticsTask.class).max("id").intValue() + 1);
        stat.setDuration(task.getDuration());
        stat.setStart(task.getTimeCreated());
        stat.setEnd(task.getTimeFinish());
        task.getStatistics().add(stat);
        realm.commitTransaction();
    }

    public void finishTask(int idTask, long duration)
    {
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        realm.beginTransaction();
        task.setState(Task.TASK_STOPPED);
        task.setDuration(duration);
        task.setTimeFinish(System.currentTimeMillis());
        StatisticsTask stat = realm.createObject(StatisticsTask.class);
        stat.setId(realm.where(StatisticsTask.class).max("id").intValue() + 1);
        stat.setDuration(task.getDuration());
        stat.setStart(task.getTimeCreated());
        stat.setEnd(task.getTimeFinish());
        task.getStatistics().add(stat);
        realm.commitTransaction();
    }

    public void removeTask(int idTask)
    {
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        RealmList<StatisticsTask> stats = task.getStatistics();
        realm.beginTransaction();
        stats.deleteAllFromRealm();
        task.deleteFromRealm();
        realm.commitTransaction();
    }
}
