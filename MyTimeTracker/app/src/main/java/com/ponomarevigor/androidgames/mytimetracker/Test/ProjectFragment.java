package com.ponomarevigor.androidgames.mytimetracker.Test;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ponomarevigor.androidgames.mytimetracker.Database.Project;
import com.ponomarevigor.androidgames.mytimetracker.Database.Workspace;
import com.ponomarevigor.androidgames.mytimetracker.Project.ProjectCreateActivity;
import com.ponomarevigor.androidgames.mytimetracker.Project.ProjectRecyclerViewAdapter;
import com.ponomarevigor.androidgames.mytimetracker.R;
import com.ponomarevigor.androidgames.mytimetracker.Task.TaskActivity;
import com.ponomarevigor.androidgames.mytimetracker.Workspace.WorkspaceActivity;
import com.ponomarevigor.androidgames.mytimetracker.Workspace.WorkspaceEditActivity;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProjectFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.xxx_project_app_bar_main, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProject();
            }
        });
        Realm.init(getContext());
        realm = Realm.getDefaultInstance();
        workspaces = realm.where(Workspace.class).findAll().sort("id");
        idWorkspace = getActivity().getIntent().getIntExtra("workspaceID", -1);
        if (idWorkspace == -1)
            projects = realm.where(Project.class).findAll().sort("id");
        else {
            workspace = realm.where(Workspace.class).equalTo("id", idWorkspace).findFirst();
            projects = realm.where(Project.class).equalTo("workspace.id", workspace.getId()).findAll();
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        projectAdapter = new ProjectRecyclerViewAdapter(getContext(), projects, workspace, realm);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(projectAdapter);
        recyclerView.setItemViewCacheSize(projects.size());

        createWorkspaces(workspaces);

        tvWorkspace = (TextView) view.findViewById(R.id.tvWorkspace);
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

        return view;
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
        Intent intent = new Intent(getActivity(), ProjectCreateActivity.class);
        if (workspace == null)
            intent.putExtra("idWorkspace", 1);
        else
            intent.putExtra("idWorkspace", workspace.getId());
        startActivity(intent);
    }

    private void showWorkspaceDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    private void createWorkspace() {

        final View v = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_workspace_create, null);
        final TextView tvName = (TextView) v.findViewById(R.id.tvName);
        final TextView tvDesc = (TextView) v.findViewById(R.id.tvDescription);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.project, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
            Toast.makeText(getContext(), "Select a workspace", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (id == R.id.actionEdit) {
            Intent intent = new Intent(getActivity(), WorkspaceEditActivity.class);
            intent.putExtra("workspaceID", idWorkspace);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        createWorkspaces(realm.where(Workspace.class).findAll().sort("id"));
        if (pos != 0)
            tvWorkspace.setText(workspacesName[pos]);
        projectAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
