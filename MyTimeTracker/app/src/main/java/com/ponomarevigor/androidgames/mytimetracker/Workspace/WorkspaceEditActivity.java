package com.ponomarevigor.androidgames.mytimetracker.Workspace;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ponomarevigor.androidgames.mytimetracker.Database.Workspace;
import com.ponomarevigor.androidgames.mytimetracker.R;
import com.ponomarevigor.androidgames.mytimetracker.Workspace.WorkspaceActivity;

import io.realm.Realm;

/**
 * Created by Igorek on 06.11.2017.
 */

public class WorkspaceEditActivity extends AppCompatActivity {
    Realm realm;
    EditText etName, etDescription;
    Workspace workspace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace_editing);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        int id = getIntent().getIntExtra("workspaceID", 0);
        workspace = realm.where(Workspace.class).equalTo("id", id).findFirst();

        etName = (EditText) findViewById(R.id.etName);
        etName.setText(workspace.getName());

        etDescription = (EditText) findViewById(R.id.etDescription);
        etDescription.setText(workspace.getDescription());

        TextView tvBack = (TextView)findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(WorkspaceEditActivity.this, WorkspaceActivity.class);
                //startActivity(intent);
                onBackPressed();
            }
        });

        Button tvSave = (Button) findViewById(R.id.tvSave);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etName.getText().toString())) {
                    etName.setError("Name can't be empty!");
                    return;
                }
                editProject();
                //Intent intent = new Intent(WorkspaceEditActivity.this, WorkspaceActivity.class);
                //startActivity(intent);
                onBackPressed();
            }
        });

    }

    private void editProject()
    {
        realm.beginTransaction();
        workspace.setName(etName.getText().toString());
        workspace.setDescription(etDescription.getText().toString());
        realm.commitTransaction();
    }
}
