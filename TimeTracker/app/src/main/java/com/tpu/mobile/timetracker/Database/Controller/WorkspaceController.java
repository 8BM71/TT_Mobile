package com.tpu.mobile.timetracker.Database.Controller;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.tpu.mobile.timetracker.Database.Model.Project;
import com.tpu.mobile.timetracker.Database.Model.Task;
import com.tpu.mobile.timetracker.Database.Model.User;
import com.tpu.mobile.timetracker.Database.Model.Workspace;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import api.GetWorkspaces;
import api.type.WorkspaceInput;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Igorek on 27.12.2017.
 */

public class WorkspaceController {
    private Realm realm;

    public WorkspaceController(Realm realm)
    {
        this.realm = realm;
    }

    public void createWorkspace(String id, WorkspaceInput workspaceInput)
    {
        Workspace workspace = new Workspace();
        workspace.setId(id);
        workspace.setName(workspaceInput.name());
        workspace.setDescription(workspaceInput.description());
        workspace.setCreateDate(System.currentTimeMillis());
        realm.beginTransaction();
        realm.copyToRealm(workspace);
        realm.commitTransaction();
    }

    public Workspace getWorkspace(String id){
        return realm.where(Workspace.class).equalTo("id", id).findFirst();
    }

    public RealmResults<Workspace> createWorkspaces(List<GetWorkspaces.Workspace> workspaces)
    {
        if (workspaces == null) return null;
        for (int i = 0; i < workspaces.size(); i++)
        {
            Workspace workspace = new Workspace();
            workspace.setId(workspaces.get(i).id());
            workspace.setName(workspaces.get(i).name());
            workspace.setDescription(workspaces.get(i).description());
            workspace.setCreateDate(workspaces.get(i).crdate());
            realm.beginTransaction();
            realm.copyToRealm(workspace);
            realm.commitTransaction();
        }

        return realm.where(Workspace.class).findAllSorted("createDate", Sort.ASCENDING);
    }

    public RealmResults<Workspace> getWorkspaces()
    {
        return realm.where(Workspace.class).findAllSorted("createDate", Sort.ASCENDING);
    }

    public void removeWorkspace(String id)
    {
        realm.beginTransaction();
        RealmResults<Project> projects = realm.where(Project.class).equalTo("workspace.id", id).findAll();
        for (int i = 0; i < projects.size(); i++)
            realm.where(Task.class).equalTo("project.id", projects.get(i).getId()).findAll().deleteAllFromRealm();
        realm.where(Project.class).equalTo("workspace.id", id).findAll().deleteAllFromRealm();
        realm.where(Workspace.class).equalTo("id", id).findFirst().deleteFromRealm();
        realm.commitTransaction();
    }

    public void updateWorkspace(String id, String name, String description)
    {
        Workspace workspace = realm.where(Workspace.class).equalTo("id", id).findFirst();
        realm.beginTransaction();
        workspace.setName(name);
        workspace.setDescription(description);
        realm.commitTransaction();
    }
}
