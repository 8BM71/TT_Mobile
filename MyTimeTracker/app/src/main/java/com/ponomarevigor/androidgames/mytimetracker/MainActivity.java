package com.ponomarevigor.androidgames.mytimetracker;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ponomarevigor.androidgames.mytimetracker.Database.TaskModel;
import com.ponomarevigor.androidgames.mytimetracker.Task.Task;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/*
    Обдумать список тасков.
    taskModel и tasks почти одно и тоже.
    TaskModel не разрешает использовать функции таймера, или я коряво затестил.
    Но проект при сборке выдал ошибку.
    Можно выкинуть tasks, а к каждому объекту taskModel привязать таймеры.
    Тогда не придется вызывать функцию initData.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    List<Task> tasks;
    RealmResults<TaskModel> taskModels;
    RecyclerViewTaskAdapter taskAdapter;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        /////////////////////////////////////////////////////
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        tasks = new ArrayList<Task>();
        initData(); //Вытаскиваем данные из TaskModel

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        taskAdapter = new RecyclerViewTaskAdapter(this, tasks, realm);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(taskAdapter);
    }

    private void createTask()
    {
        final View v = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("  Create new task!")
                //.setMessage("Task")
                .setIcon(R.drawable.task)
                .setCancelable(true)
                .setNegativeButton("Add task",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                TextView tvName = (TextView) v.findViewById(R.id.tvNameTask);
                                TextView tvDesc = (TextView) v.findViewById(R.id.tvDescriptionTask);

                                String name = tvName.getText().toString();
                                String description = tvDesc.getText().toString();

                                Task task = new Task(name, description);
                                tasks.add(task);

                                realm.beginTransaction();
                                TaskModel taskModel = realm.createObject(TaskModel.class);
                                taskModel.setName(name);
                                taskModel.setDescription(description);
                                long createdTime = System.currentTimeMillis();
                                taskModel.setTimeCreated(createdTime);
                                taskModel.setState(TaskModel.TASK_CREATED);
                                taskModel.setDuration(0);
                                realm.commitTransaction();

                                taskAdapter.notifyDataSetChanged();
                            }
                        });
        builder.setView(v);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initData()
    {
        taskModels = realm.where(TaskModel.class).findAll();
        int size = taskModels.size();
        for (int i = 0; i < size; i++) {
            TaskModel taskModel = taskModels.get(i);
            String name = taskModel.getName();
            String description = taskModel.getDescription();
            Task task = new Task(name, description);
            task.setTimeStart(taskModel.getTimeStart());
            task.setTimePause(taskModel.getTimePause());
            task.setTimeFinish(taskModel.getTimeFinish());
            task.setDuration(taskModel.getDuration());
            int state = taskModel.getState();
            task.setState(state);
            switch (state)
            {
                case TaskModel.TASK_RUNNING:
                    task.setTimeStart();
                    break;
                case TaskModel.TASK_PAUSED:
                    task.setTimePause();
                    break;
                case TaskModel.TASK_STOPPED:
                    task.setTimeStop();
                    break;
            }
            tasks.add(task);
        }
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

    private void clearAllTask()
    {
        realm.beginTransaction();
        realm.where(TaskModel.class).findAll().deleteAllFromRealm();
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
        return true;
    }
}
