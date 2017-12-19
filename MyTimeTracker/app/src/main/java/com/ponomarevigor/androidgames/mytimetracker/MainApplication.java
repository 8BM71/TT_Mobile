package com.ponomarevigor.androidgames.mytimetracker;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import io.realm.Realm;

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
    }
}
