package com.ponomarevigor.androidgames.mytimetracker;

import android.app.Application;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.ponomarevigor.androidgames.mytimetracker.Database.Project;
import com.ponomarevigor.androidgames.mytimetracker.Project.ProjectActivity;
import com.ponomarevigor.androidgames.mytimetracker.Test.MainActivity;
import com.ponomarevigor.androidgames.mytimetracker.User.UserActivity;

import junit.framework.Test;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Igorek on 22.11.2017.
 */

public class MainApplication extends Application {
    Realm realm;
    GoogleSignInAccount account;
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        account = GoogleSignIn.getLastSignedInAccount(this);
        /*if (account == null) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }*/
    }
}
