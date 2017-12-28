package com.tpu.mobile.timetracker.Database.Model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Igorek on 01.11.2017.
 */

public class Workspace extends RealmObject {
    public String id;
    public String name;
    public String description;
    public Long createDate;

    public Workspace() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = Long.parseLong(createDate);
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }
}
