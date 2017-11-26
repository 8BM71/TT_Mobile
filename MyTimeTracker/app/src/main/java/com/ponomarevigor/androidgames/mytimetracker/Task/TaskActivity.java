package com.ponomarevigor.androidgames.mytimetracker.Task;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ponomarevigor.androidgames.mytimetracker.Database.Project;
import com.ponomarevigor.androidgames.mytimetracker.Database.Task;
import com.ponomarevigor.androidgames.mytimetracker.Database.Workspace;
import com.ponomarevigor.androidgames.mytimetracker.Project.ProjectActivity;
import com.ponomarevigor.androidgames.mytimetracker.Project.ProjectCreateActivity;
import com.ponomarevigor.androidgames.mytimetracker.Project.ProjectEditActivity;
import com.ponomarevigor.androidgames.mytimetracker.R;
import com.ponomarevigor.androidgames.mytimetracker.Task.ItemTaskTouchHelper.ItemTaskTouchHelper;
import com.ponomarevigor.androidgames.mytimetracker.Workspace.WorkspaceActivity;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class TaskActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Realm realm;
    RealmResults<Task> tasks;
    RealmResults<Project> projects;
    RecyclerView recyclerView;
    TaskRecyclerViewAdapter taskAdapter;
    ItemTouchHelper touchHelper;
    ImageButton ibAdd, ibEdit, ibDelete;
    TextView tvProject;
    Project project;

    String[] projectsName;
    int pos = 0;
    int[] projectsColor;

    int idProject;

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

        ibAdd = (ImageButton)findViewById(R.id.ibAdd);
        ibEdit = (ImageButton)findViewById(R.id.ibEdit);
        ibDelete = (ImageButton)findViewById(R.id.ibDelete);
        /////////////////////////////////////////////////////

        idProject = getIntent().getIntExtra("projectID", -1);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        //tasks = realm.where(Task.class).findAllSorted("position");
        //tasks = realm.where(Task.class).findAllSorted("timeCreated", Sort.DESCENDING);
        //initProject();
        projects = realm.where(Project.class).findAll().sort("id");
        if (projects.size() == 0) initProject();
        //project = projects.get(0);
        //tasks = project.getTasks().sort("timeCreated", Sort.DESCENDING);
        if (idProject == -1)
            tasks = realm.where(Task.class).findAllSorted("timeCreated", Sort.DESCENDING);
        else {
            project = realm.where(Project.class).equalTo("id", idProject).findFirst();
            //tasks = project.getTasks().sort("timeCreated", Sort.DESCENDING);
            tasks = realm.where(Task.class).equalTo("project.id", project.getId())
                    .findAllSorted("timeCreated", Sort.DESCENDING);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        taskAdapter = new TaskRecyclerViewAdapter(this, tasks,  project, realm);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setItemViewCacheSize(tasks.size());
        ItemTouchHelper.Callback callback = new ItemTaskTouchHelper(taskAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        projectsName = new String[projects.size() + 1 ];
        projectsName[0] = "All tasks";
        projectsColor = new int[projects.size() + 1 ];
        projectsColor[0] = Color.WHITE;
        createProjects(projects);


        tvProject = (TextView) findViewById(R.id.tvProject);
        if (idProject == -1) {
            Log.d("myLogTest", "1");
            tvProject.setText(projectsName[0]);
        }
        else {
            Log.d("myLogTest", "2");
            tvProject.setText(project.getName());
            Log.d("myLogTest", "2 + " + project.getName());
        }
        tvProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProjectDialog();
            }
        });

    }

    private void createProjects(RealmResults<Project> projects)
    {
        for (int i = 0; i < projects.size(); i++)
        {
            projectsName[i + 1] = projects.get(i).getName();
            projectsColor[i + 1] = projects.get(i).getColor();
        }
    }

    private void initProject()
    {
        Workspace workspace = new Workspace();
        workspace.setName("No workspace");
        workspace.setDescription("No description");
        workspace.setId(1);
        realm.beginTransaction();
        realm.copyToRealm(workspace);
        realm.commitTransaction();

        Project project = new Project();
        project.setName("No project");
        project.setDescription("No description");
        project.setUserHost("you");
        project.setId(1);
        project.setColor(Color.LTGRAY);
        project.setWorkspace(realm.where(Workspace.class).findFirst());
        project.setStart(System.currentTimeMillis());
        realm.beginTransaction();
        realm.copyToRealm(project);
        realm.commitTransaction();
    }

    private void showProjectDialog()
    {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a project").setIcon(R.drawable.project);
        builder.setItems(projectsName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadTasks(i);
            }
        });
        builder.setCancelable(true);
        builder.create();
        builder.show();*/

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a project").setIcon(R.drawable.project);
        AdapterDialog adapter = new AdapterDialog(this, R.layout.dialog_project_row, projectsName, projectsColor);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                loadTasks(item);
            }
        });
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }

    private void loadTasks(int pos)
    {
        this.pos = pos;
        tvProject.setText(projectsName[pos]);
        if (pos == 0) {
            tasks = realm.where(Task.class).findAllSorted("timeCreated", Sort.DESCENDING);
            project = null;
            idProject = -1;
        }
        else {
            project = projects.get(pos - 1);
            idProject = project.getId();
            //tasks = project.getTasks().sort("timeCreated", Sort.DESCENDING);
            tasks = realm.where(Task.class).equalTo("project.id", project.getId())
                    .findAllSorted("timeCreated", Sort.DESCENDING);
        }

        taskAdapter.setTasks(tasks);
        taskAdapter.setProject(project);
        recyclerView.setItemViewCacheSize(tasks.size());
        taskAdapter.notifyDataSetChanged();
    }

    private void createTask()
    {
        final View v = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("  Create new task!")
                .setIcon(R.drawable.task)
                .setCancelable(true)
                .setNegativeButton("Add task",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                TextView tvName = (TextView) v.findViewById(R.id.tvNameTask);
                                TextView tvDesc = (TextView) v.findViewById(R.id.tvDescriptionTask);

                                String name = tvName.getText().toString();
                                String description = tvDesc.getText().toString();
                                if (name.isEmpty() && project == null)
                                    name = "Task: " + realm.where(Project.class).equalTo("id", 1).findFirst().getName();
                                if (name.isEmpty())
                                    name = "Task: " + project.getName();
                                realm.beginTransaction();
                                Task taskModel = realm.createObject(Task.class);
                                long createdTime = System.currentTimeMillis();
                                taskModel.setName(name);
                                taskModel.setDescription(description);
                                taskModel.setTimeCreated(createdTime);
                                taskModel.setState(Task.TASK_CREATED);
                                taskModel.setDuration(0);

                                taskModel.setId(realm.where(Task.class).max("id").intValue() + 1);
                                if (project == null)
                                    taskModel.setProject(realm.where(Project.class).equalTo("id", 1).findFirst());
                                else
                                    taskModel.setProject(realm.where(Project.class).equalTo("id", project.getId()).findFirst());
                                realm.commitTransaction();

                                recyclerView.setItemViewCacheSize(tasks.size());
                                taskAdapter.notifyDataSetChanged();
                            }
                        });
        builder.setView(v);
        AlertDialog alert = builder.create();
        alert.show();
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
        getMenuInflater().inflate(R.menu.task_1, menu);
        return true;
        //return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.actionCreate) {
            Intent intent = new Intent(this, ProjectCreateActivity.class);
            startActivity(intent);
            return true;
        }

        if (idProject == -1 || idProject == 1)
        {
            Toast.makeText(this, "Select a project", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (id == R.id.actionEdit) {
            Intent intent = new Intent(this, ProjectEditActivity.class);
            intent.putExtra("projectID", idProject);
            startActivity(intent);
            return true;
        }

 /*       if (id == R.id.actionDelete) {
 //НЕ РАБОТАЕТ, вылетает, потом разобраться.
            showDeleteDialog();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDialog()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = "Delete " + project.getName() + "?";
        builder.setTitle(title)
                .setCancelable(true)
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                realm.beginTransaction();
                                project.deleteFromRealm();
                                tasks.deleteAllFromRealm();
                                realm.commitTransaction();
                                projects = realm.where(Project.class).findAll().sort("id");
                                createProjects(projects);
                                loadTasks(0);
                                dialog.dismiss();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        createProjects(realm.where(Project.class).findAll().sort("id"));
        if (pos != 0)
            tvProject.setText(projectsName[pos]);
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

        if (id == R.id.nav_1) {
            Intent intent = new Intent(this, WorkspaceActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.nav_2) {
            Intent intent = new Intent(this, ProjectActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.nav_3) {
            return true;
        }

        return true;
    }
}
