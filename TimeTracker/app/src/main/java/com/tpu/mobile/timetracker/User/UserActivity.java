package com.tpu.mobile.timetracker.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.tpu.mobile.timetracker.Database.User;
import com.tpu.mobile.timetracker.R;
import com.tpu.mobile.timetracker.Main.MainActivity;

import io.realm.Realm;

/**
 * Created by Igorek on 30.11.2017.
 */

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LogUserActivity";
    private static final int RC_SIGN_IN = 9002;

    Chronometer chronometer;
    SignInButton signInButton;

    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autorization_activity);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        chronometer = (Chronometer)findViewById(R.id.chronometer);
        chronometer.start();
        signInButton = (SignInButton)findViewById(R.id.bSign);
        signInButton.setOnClickListener(this);
        validateServerClientID();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bSign:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            loadActivity(account);
        } catch (ApiException e) {
            Log.w(TAG, ":failed code=" + e.getStatusCode());
        }
    }

    private void validateServerClientID() {
        String serverClientId = getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;
            Log.w(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void loadActivity(GoogleSignInAccount account)
    {
        User user = new User();
        user.setId(0);
        user.setName(account.getDisplayName());
        user.setEmail(account.getEmail());
        user.setIdToken(account.getIdToken());
        user.setPhoto(account.getPhotoUrl().toString());
        realm.beginTransaction();
        if (realm.where(User.class).equalTo("id", 0).findFirst() != null)
            realm.where(User.class).equalTo("id", 0).findFirst().deleteFromRealm();
        realm.copyToRealm(user);
        realm.commitTransaction();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
