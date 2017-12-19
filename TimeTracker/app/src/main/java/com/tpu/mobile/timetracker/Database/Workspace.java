package com.tpu.mobile.timetracker.Database;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Igorek on 01.11.2017.
 */

public class Workspace extends RealmObject {
    public int id;

    public String name;
    public String description;
    public RealmList<Project> projects;


    public Workspace() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RealmList<Project> getProjects() {
        return projects;
    }

    public void setProjects(RealmList<Project> projects) {
        this.projects = projects;
    }

}
