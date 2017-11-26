package com.ponomarevigor.androidgames.mytimetracker.Tests.Project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jaredrummler.android.colorpicker.ColorPickerView;
import com.ponomarevigor.androidgames.mytimetracker.Database.Project;
import com.ponomarevigor.androidgames.mytimetracker.Database.Workspace;
import com.ponomarevigor.androidgames.mytimetracker.R;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Igorek on 06.11.2017.
 */

public class ProjectEditActivityCopy extends AppCompatActivity implements ColorPickerView.OnColorChangedListener {
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
        setContentView(R.layout.activity_project_editing);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        int id = getIntent().getIntExtra("projectID", 0);
        project = realm.where(Project.class).equalTo("id", id).findFirst();
        //int idWorkspace = getIntent().getIntExtra("workspaceID", 1);
        workspace = realm.where(Workspace.class).equalTo("projects.id", id).findFirst();
        if (workspace == null)
            workspace = realm.where(Workspace.class).equalTo("id", 1).findFirst();
        workspaces = realm.where(Workspace.class).findAll().sort("id");
        workspacesName = new String[workspaces.size()];
        for (int i = 0; i < workspaces.size(); i++)
        {
            workspacesName[i] = workspaces.get(i).getName();
        }

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
                Intent intent = new Intent(ProjectEditActivityCopy.this, ProjectActivity.class);
                startActivity(intent);
            }
        });

        Button tvSave = (Button) findViewById(R.id.tvSave);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProject();
                Intent intent = new Intent(ProjectEditActivityCopy.this, ProjectActivity.class);
                startActivity(intent);
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

        Project copyProject = realm.copyFromRealm(project);
        workspace.getProjects().add(copyProject);
        project.deleteFromRealm();

        realm.commitTransaction();
    }

    @Override
    public void onColorChanged(int newColor) {
        color = colorPickerView.getColor();
    }
}
