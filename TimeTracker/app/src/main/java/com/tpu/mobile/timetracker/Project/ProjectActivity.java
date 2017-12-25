package com.tpu.mobile.timetracker.Project;

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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpu.mobile.timetracker.Database.Model.Project;
import com.tpu.mobile.timetracker.Database.Model.Workspace;
import com.tpu.mobile.timetracker.R;
import com.tpu.mobile.timetracker.Task.TaskActivity;
import com.tpu.mobile.timetracker.Workspace.WorkspaceActivity;
import com.tpu.mobile.timetracker.Workspace.WorkspaceEditActivity;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProjectActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Realm realm;
    RealmResults<Project> projects;
    RealmResults<Workspace> workspaces;
    Workspace workspace;
    RecyclerView recyclerView;
    ProjectRecyclerViewAdapter projectAdapter;
    TextView tvWorkspace;

    String[] workspacesName;
    int pos = 0;
    int idWorkspace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_activity_main);
        /////////////////////////////////////////////////////
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProject();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_project);
        /////////////////////////////////////////////////////
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        workspaces = realm.where(Workspace.class).findAll().sort("id");
        idWorkspace = getIntent().getIntExtra("workspaceID", -1);
        if (idWorkspace == -1)
            projects = realm.where(Project.class).findAll().sort("id");
        else {
            workspace = realm.where(Workspace.class).equalTo("id", idWorkspace).findFirst();
            projects = realm.where(Project.class).equalTo("workspace.id", workspace.getId()).findAll();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        projectAdapter = new ProjectRecyclerViewAdapter(this, projects, workspace, realm);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(projectAdapter);
        recyclerView.setItemViewCacheSize(projects.size());

        createWorkspaces(workspaces);

        tvWorkspace = (TextView) findViewById(R.id.tvWorkspace);
        if (idWorkspace == -1)
            tvWorkspace.setText(workspacesName[0]);
        else
            tvWorkspace.setText(workspace.getName());

        tvWorkspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWorkspaceDialog();
            }
        });
    }

    private void createWorkspaces(RealmResults<Workspace> workspaces)
    {
        workspacesName = new String[workspaces.size() + 1];
        workspacesName[0] = "All projects";
        for (int i = 0; i < workspaces.size(); i++)
        {
            workspacesName[i + 1] = workspaces.get(i).getName();
        }
    }

    private void createProject()
    {
        Intent intent = new Intent(ProjectActivity.this, ProjectCreateActivity.class);
        if (workspace == null)
            intent.putExtra("idWorkspace", 1);
        else
            intent.putExtra("idWorkspace", workspace.getId());
        startActivity(intent);
    }

    private void showWorkspaceDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a workspace").setIcon(R.drawable.workspace);
        builder.setItems(workspacesName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loadProjects(i);
            }
        });
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }

    private void loadProjects(int pos)
    {
        this.pos = pos;
        tvWorkspace.setText(workspacesName[pos]);
        if (pos == 0) {
            projects = realm.where(Project.class).findAll().sort("id");
            workspace = null;
            idWorkspace = -1;
        }
        else {
            workspace = workspaces.get(pos - 1);
            idWorkspace = workspace.getId();
            projects = realm.where(Project.class).equalTo("workspace.id", workspace.getId()).findAll();
        }

        projectAdapter.setProjects(projects);
        projectAdapter.setWorkspace(workspace);
        recyclerView.setItemViewCacheSize(projects.size());
        projectAdapter.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.actionCreate) {
            createWorkspace();
            return true;
        }

        if (idWorkspace == -1 || idWorkspace == 1)
        {
            Toast.makeText(this, "Select a workspace", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (id == R.id.actionEdit) {
            Intent intent = new Intent(this, WorkspaceEditActivity.class);
            intent.putExtra("workspaceID", idWorkspace);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createWorkspace() {

        final View v = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_workspace_create, null);
        final TextView tvName = (TextView) v.findViewById(R.id.tvName);
        final TextView tvDesc = (TextView) v.findViewById(R.id.tvDescription);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("  Create new workspace!")
                .setIcon(R.drawable.workspace)
                .setCancelable(true)
                .setPositiveButton("Add workspace", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        builder.setView(v);
        final AlertDialog alert = builder.create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) alert).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = tvName.getText().toString();
                        String description = tvDesc.getText().toString();

                        if (TextUtils.isEmpty(name)) {
                            tvName.setError("Name can't be empty!");
                            return;
                        }

                        realm.beginTransaction();
                        Workspace workspace = realm.createObject(Workspace.class);
                        workspace.setName(name);
                        workspace.setDescription(description);
                        workspace.setId(realm.where(Workspace.class).max("id").intValue() + 1);
                        realm.commitTransaction();

                        workspaces = realm.where(Workspace.class).findAll().sort("id");
                        workspacesName = new String[workspaces.size() + 1];
                        createWorkspaces(workspaces);
                        loadProjects(workspaces.size());

                        alert.dismiss();
                    }
                });
            }
        });

        alert.show();
    }

    @Override
    protected void onResume() {
        createWorkspaces(realm.where(Workspace.class).findAll().sort("id"));
        if (pos != 0)
            tvWorkspace.setText(workspacesName[pos]);
        projectAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                loadActivity(id);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadActivity(int id)
    {
        Intent intent = null;
        if (id == R.id.nav_project)
            return;

        if (id == R.id.nav_workspace)
            intent = new Intent(this, WorkspaceActivity.class);

        if (id == R.id.nav_task)
            intent = new Intent(this, TaskActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
