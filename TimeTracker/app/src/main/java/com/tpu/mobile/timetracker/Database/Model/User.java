package com.tpu.mobile.timetracker.Database.Model;

import io.realm.RealmObject;

/**
 * Created by Igorek on 30.11.2017.
 */

public class User extends RealmObject {
    public String id;
    public String name;
    public String username;
    public String email;
    public String photo;

    public User() {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
