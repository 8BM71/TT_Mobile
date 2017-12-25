package com.tpu.mobile.timetracker.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tpu.mobile.timetracker.Database.Model.Workspace;
import com.tpu.mobile.timetracker.R;
import com.tpu.mobile.timetracker.Workspace.WorkspaceRecyclerViewAdapter;

import io.realm.Realm;
import io.realm.RealmResults;

public class WorkspaceFragment extends Fragment {

    Context context;
    Realm realm;
    RealmResults<Workspace> workspaces;
    RecyclerView recyclerView;
    WorkspaceRecyclerViewAdapter workspaceAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.xxx_workspace_app_bar_main, container, false);

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
                createWorkspace();
            }
        });

        Realm.init(getContext());
        realm = Realm.getDefaultInstance();
        workspaces = realm.where(Workspace.class).findAll().sort("id");

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        workspaceAdapter = new WorkspaceRecyclerViewAdapter(getContext(), workspaces, realm);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(workspaceAdapter);
        recyclerView.setItemViewCacheSize(workspaces.size());

        return view;
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
    public void onResume() {
        workspaceAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
