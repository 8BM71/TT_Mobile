package com.ponomarevigor.androidgames.mytimetracker.Tests.TaskTest3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ponomarevigor.androidgames.mytimetracker.Database.Project;
import com.ponomarevigor.androidgames.mytimetracker.Database.Task;
import com.ponomarevigor.androidgames.mytimetracker.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class TaskActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Realm realm;
    RealmResults<Task> tasks;
    RealmResults<Project> projects;
    RecyclerView recyclerView;
    TaskRecyclerViewAdapter taskAdapter;
    ItemTouchHelper touchHelper;
    Spinner spinner;
    TextView tvProject;

    ArrayList<String> projectNames;
    String[] projectsName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_main_1);
        /////////////////////////////////////////////////////
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTask();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_3);
        /////////////////////////////////////////////////////
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        tasks = realm.where(Task.class).findAllSorted("position");
        projects = realm.where(Project.class).findAll();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        taskAdapter = new TaskRecyclerViewAdapter(this, tasks, realm);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setItemViewCacheSize(tasks.size());

        spinner = (Spinner)findViewById(R.id.spinnerProjects);
        projectNames = new ArrayList<String>();
        projectsName = new String[projects.size()];
        for (int i = 0; i < projects.size(); i++)
        {
            projectNames.add(projects.get(i).getName());
            projectsName[i] = projects.get(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner, projectNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        tvProject = (TextView) findViewById(R.id.tvProject);
        tvProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProjectDialog();
            }
        });
    }

    private void showProjectDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a project").setIcon(R.drawable.project);

        builder.setItems(projectsName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tvProject.setText(projectsName[i]);
            }
        });
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }

    private void createTask()
    {
        realm.beginTransaction();
        Task taskModel = realm.createObject(Task.class);
        long createdTime = System.currentTimeMillis();
        taskModel.setName("noName");
        taskModel.setDescription("noDescription");
        taskModel.setTimeCreated(createdTime);
        taskModel.setState(Task.TASK_CREATED);
        taskModel.setDuration(0);
        taskModel.setPosition(tasks.size() - 1);
        realm.commitTransaction();

        recyclerView.setItemViewCacheSize(tasks.size());
        taskAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            createTask();
            return true;
        }

        if (id == R.id.action_clear) {
            clearAllTask();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        taskAdapter.notifyDataSetChanged();
        super.onResume();
    }

    private void clearAllTask()
    {
        realm.beginTransaction();
        realm.where(Task.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_2) {
            Intent intent = new Intent(this, com.ponomarevigor.androidgames.mytimetracker.Project.ProjectActivity.class);
            startActivity(intent);
            return true;
        }

    /*    if (id == R.id.nav_3) {
            Intent intent = new Intent(this, com.ponomarevigor.androidgames.mytimetracker.Tests.TaskTest5.TaskActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.nav_3_a) {
            Intent intent = new Intent(this, com.ponomarevigor.androidgames.mytimetracker.Tests.TaskTest1.TaskActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.nav_3_b) {
            Intent intent = new Intent(this, com.ponomarevigor.androidgames.mytimetracker.Tests.TaskTest2.TaskActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.nav_3_c) {
            return true;
        }

        if (id == R.id.nav_3_d) {
            Intent intent = new Intent(this, com.ponomarevigor.androidgames.mytimetracker.Task.TaskActivity.class);
            startActivity(intent);
            return true;
        }
*/
        return true;
    }
}
