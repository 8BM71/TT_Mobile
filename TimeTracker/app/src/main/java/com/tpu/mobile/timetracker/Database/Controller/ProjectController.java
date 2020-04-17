package com.tpu.mobile.timetracker.Database.Controller;

import com.tpu.mobile.timetracker.Database.Model.Project;
import com.tpu.mobile.timetracker.Database.Model.StatisticsTask;
import com.tpu.mobile.timetracker.Database.Model.Task;
import com.tpu.mobile.timetracker.Database.Model.Workspace;

import java.util.List;
import java.util.TimeZone;

import api.GetProjects;
import api.GetWorkspaces;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Igorek on 25.12.2017.
 */

public class ProjectController {
    private Realm realm;
    public ProjectController(Realm realm)
    {
        this.realm = realm;
    }

    public void createProject(String id, String name, String description, int color, int idWs)
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

    public void createProject(String id, String name, String description, int color, String idWs)
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

    public void updateProject(String id, String name, String description, int color, String idWs)
    {
        Project project = realm.where(Project.class).equalTo("id", id).findFirst();
        realm.beginTransaction();
        project.setName(name);
        project.setDescription(description);
        project.setId(id);
        project.setColor(color);
        //project.setWorkspace(realm.where(Workspace.class).equalTo("id", idWs).findFirst());
        realm.commitTransaction();
    }

    public RealmResults<Project> createProjects(List<GetProjects.Project> projects, List<String> ids)
    {
        if (projects == null) return null;
        for (int i = 0; i < projects.size(); i++)
        {
            Project project = new Project();
            project.setId(projects.get(i).id());
            project.setStart(projects.get(i).crdate());
            project.setName(projects.get(i).name());
            project.setColor(projects.get(i).color());
            project.setIdWorkspace(ids.get(i));
            project.setWorkspace(realm.where(Workspace.class).equalTo("id", ids.get(i)).findFirst());
            project.setIdWorkspace(ids.get(i));
            realm.beginTransaction();
            realm.copyToRealm(project);
            realm.commitTransaction();
        }

        return realm.where(Project.class).findAllSorted("start", Sort.ASCENDING);
    }

    public void addProject(Project project)
    {
        realm.beginTransaction();
        realm.copyToRealm(project);
        realm.commitTransaction();
    }

    public Project getProject(String idProject)
    {
        return realm.where(Project.class).equalTo("id", idProject).findFirst();
    }

    public RealmResults<Project> getProjects()
    {
        return realm.where(Project.class).findAllSorted("start");
    }

    public RealmResults<Project> getProjectsOfWs(String idWs)
    {
        return realm.where(Project.class).equalTo("workspace.id", idWs).findAllSorted("start");
    }

    public void removeProject(String id)
    {
        realm.beginTransaction();
        // + tasks
        realm.where(Task.class).equalTo("project.id", id).findAll().deleteAllFromRealm();
        realm.where(Project.class).equalTo("id", id).findFirst().deleteFromRealm();
        realm.commitTransaction();
    }
}
