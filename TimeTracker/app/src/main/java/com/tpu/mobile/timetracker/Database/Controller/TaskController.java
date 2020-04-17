package com.tpu.mobile.timetracker.Database.Controller;

import android.util.Log;

import com.tpu.mobile.timetracker.Database.Model.Project;
import com.tpu.mobile.timetracker.Database.Model.StatisticsTask;
import com.tpu.mobile.timetracker.Database.Model.Task;

import java.util.List;
import java.util.TimeZone;

import api.GetProjects;
import api.GetTasks;
import api.StartTask;
import api.StopTimeEntry;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Igorek on 25.12.2017.
 */

public class TaskController {
    private Realm realm;

    public TaskController(Realm realm) {
        this.realm = realm;
    }

    public void createTask(String id, String name, String description, String idProject) {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setDescription(description);
        task.setTimeCreated(System.currentTimeMillis());
        task.setState(Task.TASK_CREATED);
        task.setDuration(0);
        task.setProject(realm.where(Project.class).equalTo("id", idProject).findFirst());
        realm.beginTransaction();
        realm.copyToRealm(task);
        realm.commitTransaction();
    }

    public RealmResults<Task> createTasks(List<GetTasks.Task> tasks, List<String> ids) {
        if (tasks == null) return null;
        for (int i = 0; i < tasks.size(); i++) {
            realm.beginTransaction();
            Task task = realm.createObject(Task.class);
            task.setId(tasks.get(i).id());
            task.setName(tasks.get(i).name());
            task.setDescription(tasks.get(i).description());
            task.setProject(realm.where(Project.class).equalTo("id", ids.get(i)).findFirst());
            for (int j = 0; j < tasks.get(i).timeEntries().size(); j++) {
                StatisticsTask stat = realm.createObject(StatisticsTask.class);
                stat.setId(tasks.get(i).timeEntries().get(j).id());
                stat.setCreate(tasks.get(i).timeEntries().get(j).crdate());
                stat.setStart(tasks.get(i).timeEntries().get(j).startDate());
                if (!tasks.get(i).timeEntries().get(j).endDate().equals(""))
                    stat.setEnd(tasks.get(i).timeEntries().get(j).endDate());
                else
                    stat.setEnd(tasks.get(i).timeEntries().get(j).startDate());
                stat.setDuration(tasks.get(i).timeEntries().get(j).duration());
                stat.setTask(realm.where(Task.class).equalTo("id", task.getId()).findFirst());
                task.getStatistics().add(stat);
            }

            if (task.getStatistics().size() != 0) {
                StatisticsTask stat = realm.where(StatisticsTask.class).equalTo("task.id", task.getId()).findAllSorted("create", Sort.DESCENDING).first();
                task.setTimeCreated(stat.getCreate());
                task.setTimeStart(stat.getStart());
                task.setTimeFinish(stat.getEnd());
                task.setDuration(stat.getDuration());
                task.setIdActiveStat(stat.getId());
                if (stat.getStart() == 0)
                    task.setState(Task.TASK_CREATED);
                else if (stat.getEnd() == stat.getStart())
                    task.setState(Task.TASK_RUNNING);
                else
                    task.setState(Task.TASK_STOPPED);
            }
            else
            {
                task.setState(Task.TASK_CREATED);
                task.setTimeCreated(tasks.get(i).crdate());
            }
            realm.commitTransaction();
        }
        return realm.where(Task.class).findAllSorted("timeCreated", Sort.DESCENDING);
    }


    public void addTask(Task task) {
        task.setProject(realm.where(Project.class).equalTo("id", "1").findFirst());
        realm.beginTransaction();
        realm.copyToRealm(task);
        realm.commitTransaction();
    }

    public Task getTask(String idTask) {
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        return task;
    }

    public RealmResults<Task> getTasks() {
        return realm.where(Task.class).findAllSorted("timeCreated", Sort.DESCENDING);
    }

    public RealmResults<Task> getTasksOfProject(String idProject) {
        return realm.where(Task.class).equalTo("project.id", idProject).findAllSorted("timeCreated", Sort.DESCENDING);
    }

    public void startTask(String idTask, StartTask.StartTask1 timeEntry) {
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        realm.beginTransaction();
        task.setTimeCreated(timeEntry.crdate());
        task.setTimeStart(System.currentTimeMillis());
        task.setState(Task.TASK_RUNNING);
        task.setDuration(0);
        StatisticsTask stat = realm.createObject(StatisticsTask.class);
        stat.setId(timeEntry.id());
        stat.setCreate(timeEntry.crdate());
        stat.setDuration(0);
        stat.setStart(timeEntry.startDate());
        stat.setEnd(timeEntry.crdate());
        stat.setTask(realm.where(Task.class).equalTo("id", idTask).findFirst());
        task.getStatistics().add(stat);
        task.setIdActiveStat(stat.getId());
        realm.commitTransaction();
    }

    public void startTask(String idTask) {
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        realm.beginTransaction();
        task.setTimeStart(System.currentTimeMillis());
        task.setState(Task.TASK_RUNNING);
        task.setDuration(0);
        StatisticsTask stat = realm.createObject(StatisticsTask.class);
        stat.setTask(realm.where(Task.class).equalTo("id", idTask).findFirst());
        task.getStatistics().add(stat);
        task.setIdActiveStat(stat.getId());
        realm.commitTransaction();
    }

    public void finishTask(String idTask, StopTimeEntry.StopTimeEntry1 timeEntry) {
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        StatisticsTask stat = realm.where(StatisticsTask.class).equalTo("id", task.getIdActiveStat()).findFirst();
        realm.beginTransaction();
        task.setState(Task.TASK_STOPPED);
        task.setTimeFinish(timeEntry.endDate());
        task.setDuration(timeEntry.duration());
        stat.setCreate(timeEntry.crdate());
        stat.setStart(timeEntry.startDate());
        stat.setEnd(timeEntry.endDate());
        stat.setDuration(timeEntry.duration());
        realm.commitTransaction();
    }

    public void finishTask(String idTask, long duration) {
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        StatisticsTask stat = realm.where(StatisticsTask.class).equalTo("id", task.getIdActiveStat()).findFirst();
        realm.beginTransaction();
        task.setState(Task.TASK_STOPPED);
        stat.setDuration(duration);
        realm.commitTransaction();
    }

    public void removeTask(String idTask) {
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        RealmList<StatisticsTask> stats = task.getStatistics();
        realm.beginTransaction();
        stats.deleteAllFromRealm();
        task.deleteFromRealm();
        realm.commitTransaction();
    }

    public void updateTask(String idTask, String name, String description, String idProject)
    {
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        realm.beginTransaction();
        task.setName(name);
        task.setDescription(description);
        task.setProject(realm.where(Project.class).equalTo("id", idProject).findFirst());
        realm.commitTransaction();
    }

    public void updateStat(String idStat, long start, long end, long duration)
    {
        StatisticsTask stat = realm.where(StatisticsTask.class).equalTo("id", idStat).findFirst();
        realm.beginTransaction();
        stat.setDuration(duration);
        stat.setStart(start);
        stat.setEnd(end);
        realm.commitTransaction();
    }
}
