package com.ponomarevigor.androidgames.mytimetracker.Workspace;

import android.content.Context;
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

import com.ponomarevigor.androidgames.mytimetracker.Database.Workspace;
import com.ponomarevigor.androidgames.mytimetracker.Project.ProjectActivity;
import com.ponomarevigor.androidgames.mytimetracker.R;
import com.ponomarevigor.androidgames.mytimetracker.Task.TaskActivity;

import io.realm.Realm;
import io.realm.RealmResults;

public class WorkspaceActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    Realm realm;
    RealmResults<Workspace> workspaces;
    RecyclerView recyclerView;
    WorkspaceRecyclerViewAdapter workspaceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workspace_activity_main);
        context = this.getBaseContext();
        /////////////////////////////////////////////////////
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createWorkspace();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_workspace);
        /////////////////////////////////////////////////////

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        workspaces = realm.where(Workspace.class).findAll().sort("id");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        workspaceAdapter = new WorkspaceRecyclerViewAdapter(this, workspaces, realm);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(workspaceAdapter);
        recyclerView.setItemViewCacheSize(workspaces.size());
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

                        recyclerView.setItemViewCacheSize(workspaces.size());
                        workspaceAdapter.notifyDataSetChanged();

                        alert.dismiss();
                    }
                });
            }
        });

        alert.show();
    }

    @Override
    public void onStop() {
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
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        workspaceAdapter.notifyDataSetChanged();
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
        if (id == R.id.nav_workspace)
            return;

        if (id == R.id.nav_project)
            intent = new Intent(this, ProjectActivity.class);

        if (id == R.id.nav_task)
            intent = new Intent(this, TaskActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
