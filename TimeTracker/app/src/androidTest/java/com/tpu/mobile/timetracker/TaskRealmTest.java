package com.tpu.mobile.timetracker;

import android.support.annotation.NonNull;
import android.util.Log;

import com.tpu.mobile.timetracker.Database.Controller.ProjectController;
import com.tpu.mobile.timetracker.Database.Controller.TaskController;
import com.tpu.mobile.timetracker.Database.Model.Project;
import com.tpu.mobile.timetracker.Database.Model.StatisticsTask;
import com.tpu.mobile.timetracker.Database.Model.Task;
import com.tpu.mobile.timetracker.Task.TaskActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

/**
 * Created by Igorek on 24.12.2017.
 */

public class TaskRealmTest {
    private Realm realm;
    private TaskController ctrTask;
    private ProjectController ctrProject;

    @Before
    public void setUp() throws Exception {
        RealmConfiguration config = new RealmConfiguration.Builder().inMemory().name("test-realm").build();
        realm = Realm.getInstance(config);
        ctrTask = new TaskController(realm);
        ctrProject = new ProjectController(realm);
    }

    @After
    public void tearDown() throws Exception {
        realm.close();
    }

    @Test
    public void addTask() throws Exception {
        Task expected = createTask("1");
        ctrTask.addTask(expected);
        Task actual = ctrTask.getTask("1");
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void getTasksOfProject() throws Exception {
        ctrProject.addProject(createProject("1"));
        Task task1 = createTask("1");
        Task task2 = createTask("2");
        Task task3 = createTask("3");
        ctrTask.addTask(task1);
        ctrTask.addTask(task2);
        ctrTask.addTask(task3);
        List<Task> tasks = ctrTask.getTasksOfProject("1");
        assertThat(tasks.get(0), equalTo(task1));
        assertThat(tasks.get(1), equalTo(task2));
        assertThat(tasks.get(2), equalTo(task3));
        assertEquals(3, tasks.size());
    }

    @Test
    public void getTaskProjectNull() throws Exception {
        ctrProject.addProject(createProject("2"));
        List<Task> tasks = ctrTask.getTasksOfProject("2");
        assertEquals(0, tasks.size());
    }

    @Test
    public void startTask() throws Exception {
        Task task = createTask("1");
        ctrTask.addTask(task);
        ctrTask.startTask("1");
        assertEquals(Task.TASK_RUNNING, ctrTask.getTask("1").getState());
    }

    @Test
    public void finishTaskStatSize() throws Exception {
        Task task = createTask("3");
        ctrTask.addTask(task);
        ctrTask.startTask("3");
        assertEquals(Task.TASK_RUNNING, ctrTask.getTask("3").getState());
        ctrTask.finishTask("3", 5000);
        assertEquals(Task.TASK_STOPPED, ctrTask.getTask("3").getState());
        ctrTask.startTask("3");
        assertEquals(Task.TASK_RUNNING, ctrTask.getTask("3").getState());
        ctrTask.finishTask("3", 5000);
        assertEquals(Task.TASK_STOPPED, ctrTask.getTask("3").getState());
        ctrTask.startTask("3");
        assertEquals(Task.TASK_RUNNING, ctrTask.getTask("3").getState());
        ctrTask.finishTask("3", 5000);
        assertEquals(Task.TASK_STOPPED, ctrTask.getTask("3").getState());
        assertEquals(3, ctrTask.getTask("3").getStatistics().size());
    }

    @Test
    public void finishTask() throws Exception {
        Task t = createTask("1");
        ctrTask.addTask(t);
        ctrTask.startTask("1");
        ctrTask.finishTask("1", 5000);
        Task task = ctrTask.getTask("1");
        StatisticsTask stat = task.getStatistics().get(0);
        assertEquals(5000, stat.getDuration());
    }


    @Test
    public void removeTask() throws Exception {
        Task t = createTask("5");
        ctrTask.addTask(t);
        ctrTask.startTask("5");
        ctrTask.finishTask("5", 1000);
        ctrTask.startTask("5");
        ctrTask.finishTask("5", 1000);
        ctrTask.startTask("5");
        ctrTask.finishTask("5", 1000);
        Task task = ctrTask.getTask("5");
        assertEquals(3, task.getStatistics().size());
        ctrTask.removeTask("5");
        task = ctrTask.getTask("5");
        assertEquals(null, task);
    }


    @NonNull
    private Task createTask(String id) {
        Task task = new Task();
        task.setId(id);
        task.setName("test-name-task");
        task.setDescription("task-description-task");
        return task;
    }

    @NonNull
    private Project createProject(String id) {
        Project project = new Project();
        project.setId(id);
        project.setName("test-name-project");
        project.setDescription("task-description-project");
        return project;
    }
}
