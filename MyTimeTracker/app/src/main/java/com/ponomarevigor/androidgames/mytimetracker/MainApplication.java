package com.ponomarevigor.androidgames.mytimetracker;

import android.app.Application;

import com.ponomarevigor.androidgames.mytimetracker.Database.Project;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Igorek on 22.11.2017.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().initialData(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Project project = new Project();
                project.setName("NoProject");
                project.setDescription("NoDescription");
                project.setId(1);
                realm.beginTransaction();
                realm.copyToRealm(project);
                realm.commitTransaction();
                realm.close();
            }
        }).build();
        Realm.setDefaultConfiguration(config);
    }
}
