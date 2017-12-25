package com.tpu.mobile.timetracker.Project;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jaredrummler.android.colorpicker.ColorPickerView;
import com.tpu.mobile.timetracker.Database.Model.Project;
import com.tpu.mobile.timetracker.Database.Model.Workspace;
import com.tpu.mobile.timetracker.R;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Igorek on 06.11.2017.
 */

public class ProjectEditActivity extends AppCompatActivity implements ColorPickerView.OnColorChangedListener {
    Project project;
    Realm realm;
    EditText etName, etDescription, etWorkspace;
    ColorPickerView colorPickerView;
    int color;
    List<Workspace> workspaces;
    Workspace workspace;
    String[] workspacesName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_activity_edit);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        int id = getIntent().getIntExtra("projectID", 0);
        project = realm.where(Project.class).equalTo("id", id).findFirst();
        workspace = project.getWorkspace();
        if (workspace == null)
            workspace = realm.where(Workspace.class).equalTo("id", 1).findFirst();
        workspaces = realm.where(Workspace.class).findAll().sort("id");
        workspacesName = new String[workspaces.size()];
        for (int i = 0; i < workspaces.size(); i++)
            workspacesName[i] = workspaces.get(i).getName();

        etName = (EditText) findViewById(R.id.etName);
        etName.setText(project.getName());

        etDescription = (EditText) findViewById(R.id.etDescription);
        etDescription.setText(project.getDescription());

        colorPickerView = (ColorPickerView) findViewById(R.id.colorView);
        colorPickerView.setOnColorChangedListener(this);

        etWorkspace = (EditText) findViewById(R.id.etWorkspace);
        etWorkspace.setText(workspace.getName());

        color = project.getColor();
        colorPickerView.setColor(color, true);

        TextView tvBack = (TextView)findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(ProjectEditActivity.this, ProjectActivity.class);
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
                //Intent intent = new Intent(ProjectEditActivity.this, ProjectActivity.class);
                //startActivity(intent);
                onBackPressed();
            }
        });

        etWorkspace.setKeyListener(null);
        etWorkspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWorkspaceDialog();
            }
        });
    }

    private void showWorkspaceDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a workspace").setIcon(R.drawable.workspace);
        builder.setItems(workspacesName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                workspace = workspaces.get(i);
                etWorkspace.setText(workspace.getName());
            }
        });
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }

    private void editProject()
    {
        realm.beginTransaction();
        project.setName(etName.getText().toString());
        project.setDescription(etDescription.getText().toString());
        project.setColor(color);
        project.setWorkspace(realm.where(Workspace.class).equalTo("id", workspace.getId()).findFirst());
        realm.commitTransaction();
    }

    @Override
    public void onColorChanged(int newColor) {
        color = colorPickerView.getColor();
    }
}
