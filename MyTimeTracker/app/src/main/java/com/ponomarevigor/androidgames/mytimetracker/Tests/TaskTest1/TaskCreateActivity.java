package com.ponomarevigor.androidgames.mytimetracker.Tests.TaskTest1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ponomarevigor.androidgames.mytimetracker.Database.Project;
import com.ponomarevigor.androidgames.mytimetracker.Database.Task;
import com.ponomarevigor.androidgames.mytimetracker.R;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Igorek on 06.11.2017.
 */

public class TaskCreateActivity extends AppCompatActivity {
    Realm realm;
    EditText etName, etDescription, etProject;
    List<Project> projects;
    Project project;
    int positionTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_creating);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        positionTask = getIntent().getIntExtra("task", 0);

        etName = (EditText) findViewById(R.id.etName);
        etProject = (EditText) findViewById(R.id.etProject);
        etDescription = (EditText) findViewById(R.id.etDescription);

        etProject.setKeyListener(null);
        etProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProjectsDialog();
            }
        });

        projects = realm.where(Project.class).findAll();
        project = projects.get(0); //////////////////////////

        TextView tvBack = (TextView)findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskCreateActivity.this, com.ponomarevigor.androidgames.mytimetracker.Tests.TaskTest1.TaskActivity.class);
                startActivity(intent);
            }
        });

        Button tvSave = (Button) findViewById(R.id.tvSave);
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
                Intent intent = new Intent(TaskCreateActivity.this, com.ponomarevigor.androidgames.mytimetracker.Tests.TaskTest1.TaskActivity.class);
                startActivity(intent);
            }
        });

        Button bSave = (Button) findViewById(R.id.bSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
                Intent intent = new Intent(TaskCreateActivity.this, com.ponomarevigor.androidgames.mytimetracker.Tests.TaskTest1.TaskActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showProjectsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a project").setIcon(R.drawable.project);
        AdapterDialog adapter = new AdapterDialog(this, R.layout.dialog_project_row, projects);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                etProject.setText(projects.get(item).getName());
                project = projects.get(item);
            }
        });
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }

    private void addTask()
    {
        Task task = new Task();
        task.setName(etName.getText().toString());
        task.setDescription(etDescription.getText().toString());
        //task.setProject(project);
        task.setPosition(positionTask);
        realm.beginTransaction();
        //realm.copyToRealm(task);
        project.getTasks().add(task);
        realm.commitTransaction();
    }
}
