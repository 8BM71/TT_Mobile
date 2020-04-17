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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.tpu.mobile.timetracker.Database.Controller.WorkspaceController;
import com.tpu.mobile.timetracker.Database.Model.User;
import com.tpu.mobile.timetracker.Database.Model.Workspace;
import com.tpu.mobile.timetracker.MainApplication;
import com.tpu.mobile.timetracker.R;
import com.tpu.mobile.timetracker.Workspace.WorkspaceRecyclerViewAdapter;

import java.util.List;

import api.CreateWorkspace;
import api.GetWorkspaces;
import api.type.WorkspaceInput;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.realm.Realm;
import io.realm.RealmResults;

public class WorkspaceFragment extends Fragment {
    ApolloClient client;
    Realm realm;
    WorkspaceController workspaceController;
    String ownerID;
    RealmResults<Workspace> workspaces;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    WorkspaceRecyclerViewAdapter workspaceAdapter;
    public WorkspaceFragment(String ownerID) {
        this.ownerID = ownerID;
    }

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
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        client = ((MainApplication)getActivity().getApplication()).getApolloClient();
        realm = ((MainApplication)getActivity().getApplication()).getRealm();
        workspaceController = new WorkspaceController(realm);
        checkWorkspaces();
        workspaceAdapter = new WorkspaceRecyclerViewAdapter(getContext(), client, realm, workspaces);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(workspaceAdapter);
        return view;
    }

    private void checkWorkspaces()
    {
        if (workspaceController.getWorkspaces().size() != 0)
            workspaces = workspaceController.getWorkspaces();
        else
            loadWorkspaces();
    }

    private void loadWorkspaces()
    {
        progressBar.setVisibility(View.VISIBLE);
        ApolloCall<GetWorkspaces.Data> response = client.query(new GetWorkspaces(ownerID));
        Rx2Apollo.from(response)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Response<GetWorkspaces.Data>>() {
                    @Override
                    public void onNext(Response<GetWorkspaces.Data> dataResponse) {
                        if (dataResponse.errors().isEmpty())
                            workspaces = workspaceController.createWorkspaces(dataResponse.data().workspaces());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {
                        workspaceAdapter.setWorkspaces(workspaces);
                        progressBar.setVisibility(View.GONE);
                    }
                });
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

                        final WorkspaceInput workspace = WorkspaceInput.builder()
                                .ownerId(ownerID)
                                .name(name)
                                .description(description)
                                .build();

                        progressBar.setVisibility(View.VISIBLE);
                        Rx2Apollo.from(client.mutate(new CreateWorkspace(workspace)))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableObserver<Response<CreateWorkspace.Data>>() {
                                    @Override
                                    public void onNext(Response<CreateWorkspace.Data> dataResponse) {
                                        if (dataResponse.errors().isEmpty())
                                            workspaceController.createWorkspace(dataResponse.data().createWorkspace(),
                                                    workspace);
                                        Log.d("myLog", "createWorkspace-data:" + dataResponse.data());
                                        Log.d("myLog", "createWorkspace-error:" + dataResponse.errors());
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onComplete() {
                                        progressBar.setVisibility(View.GONE);
                                        recyclerView.setItemViewCacheSize(workspaces.size());
                                        workspaceAdapter.notifyDataSetChanged();
                                    }
                                });
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
