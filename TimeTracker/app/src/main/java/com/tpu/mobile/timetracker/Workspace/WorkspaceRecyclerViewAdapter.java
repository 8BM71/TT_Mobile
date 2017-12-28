package com.tpu.mobile.timetracker.Workspace;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.tpu.mobile.timetracker.Database.Controller.WorkspaceController;
import com.tpu.mobile.timetracker.Database.Model.Project;
import com.tpu.mobile.timetracker.Database.Model.Workspace;
import com.tpu.mobile.timetracker.R;
import com.tpu.mobile.timetracker.Main.MainActivity;

import java.util.List;

import api.CreateWorkspace;
import api.RemoveWorkspace;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Igorek on 12.10.2017.
 */

public class WorkspaceRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Workspace> workspaces;
    Realm realm;
    ApolloClient client;
    WorkspaceController workspaceController;

    public WorkspaceRecyclerViewAdapter(Context context, ApolloClient client, Realm realm, List<Workspace> workspaces) {
        this.context = context;
        this.workspaces = workspaces;
        this.realm = realm;
        this.client = client;
        workspaceController = new WorkspaceController(realm);
    }

    public void setWorkspaces(List<Workspace> workspaces) {
        this.workspaces = workspaces;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (workspaces != null)
            return workspaces.size();
        else
            return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WorkspaceViewHolder(LayoutInflater.from(context).inflate(R.layout.workspace_item_main, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final WorkspaceViewHolder vh = (WorkspaceViewHolder) holder;
        final Workspace workspace = workspaces.get(position);
        vh.isOpen = false;
        vh.baseLayout.close(false);

        vh.textName.setText(workspace.getName());
        if (workspace.getDescription() == null || workspace.getDescription().equals(""))
            vh.textDescription.setText("No description");
        else
            vh.textDescription.setText(workspace.getDescription());

        //////////////////////////////////////////////
        vh.textNumber.setText("Projects: " +
                //realm.where(Project.class).equalTo("workspace.id", workspace.getId()).findAll().size());
                realm.where(Project.class).equalTo("idWorkspace", workspace.getId()).findAll().size());

        vh.layoutClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!vh.isOpen) {
                    vh.isOpen = true;
                    vh.baseLayout.open(true);
                }
                else {
                    vh.isOpen = false;
                    vh.baseLayout.close(true);
                }
            }
        });

        vh.bOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(context, ProjectActivity.class);
                //intent.putExtra("workspaceID", workspace.getId());
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("workspaceID", workspace.getId());
                intent.putExtra("fragmentID", R.id.nav_project);
                context.startActivity(intent);
            }
        });

        vh.bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WorkspaceEditActivity.class);
                Log.d("myLog", "size = " + realm.where(Workspace.class).findAll().size());
                intent.putExtra("workspaceID", workspace.getId());
                context.startActivity(intent);
            }
        });

        vh.bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = workspaces.get(position).getId();
                String idDef = workspaces.get(0).getId();
                vh.bDelete.setClickable(false);
                if (!id.equals(idDef)) {
                    if (true) {
                        Rx2Apollo.from(client.mutate(new RemoveWorkspace(id)))
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableObserver<Response<RemoveWorkspace.Data>>() {
                                    @Override
                                    public void onNext(Response<RemoveWorkspace.Data> dataResponse) {
                                        if (dataResponse.errors().isEmpty())
                                            if (dataResponse.data().removeWorkspace())
                                                workspaceController.removeWorkspace(id);
                                        Log.d("myLog", "removeWorkspace-data:" + dataResponse.data());
                                        Log.d("myLog", "removeWorkspace-error:" + dataResponse.errors());
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
                                        vh.bDelete.setClickable(true);
                                    }

                                    @Override
                                    public void onComplete() {
                                        vh.bDelete.setClickable(true);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, workspaces.size());
                                    }
                                });
                    }
                }
                else
                    Toast.makeText(context, "Default workspace can't be removed.", Toast.LENGTH_LONG).show();
            }
        });
    }
}