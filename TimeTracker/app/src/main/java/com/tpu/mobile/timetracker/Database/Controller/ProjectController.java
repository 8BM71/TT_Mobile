package com.tpu.mobile.timetracker.Database.Controller;

import com.tpu.mobile.timetracker.Database.Model.Project;
import com.tpu.mobile.timetracker.Database.Model.StatisticsTask;
import com.tpu.mobile.timetracker.Database.Model.Task;
import com.tpu.mobile.timetracker.Database.Model.Workspace;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Igorek on 25.12.2017.
 */

public class ProjectController {
    private Realm realm;

    public ProjectController(Realm realm)
    {
        this.realm = realm;
    }

    public void createProject(int id, String name, String description, int color, int idWs)
    {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setId(id);
        project.setColor(color);
        project.setWorkspace(realm.where(Workspace.class).equalTo("id", idWs).findFirst());
        project.setStart(System.currentTimeMillis());
        realm.beginTransaction();
        realm.copyToRealm(project);
        realm.commitTransaction();
    }

    public void addProject(Project project)
    {
        realm.beginTransaction();
        realm.copyToRealm(project);
        realm.commitTransaction();
    }

    public Project getProject(int idProject)
    {
        Project project = realm.where(Project.class).equalTo("id", idProject).findFirst();
        return project;
    }

    public List<Project> getProjects()
    {
        List<Project> projects = realm.where(Project.class).findAll();
        return projects;
    }

    public List<Project> getProjectsOfWs(int idWs)
    {
        List<Project> projects = realm.where(Project.class).equalTo("workspace.id", idWs).findAll();
        return projects;
    }
}
