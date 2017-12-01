package com.ponomarevigor.androidgames.mytimetracker.Workspace;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ponomarevigor.androidgames.mytimetracker.Database.Project;
import com.ponomarevigor.androidgames.mytimetracker.Database.Workspace;
import com.ponomarevigor.androidgames.mytimetracker.Project.ProjectActivity;
import com.ponomarevigor.androidgames.mytimetracker.R;
import com.ponomarevigor.androidgames.mytimetracker.Test.MainActivity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Igorek on 12.10.2017.
 */

public class WorkspaceRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Workspace> workspaces;
    Realm realm;

    public WorkspaceRecyclerViewAdapter(Context context, List<Workspace> workspaces, Realm realm) {
        this.context = context;
        this.workspaces = workspaces;
        this.realm = realm;
    }

    @Override
    public int getItemCount() {
        return workspaces.size();
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
        if (workspace.getDescription().equals(""))
            vh.textDescription.setText("no description");
        else
            vh.textDescription.setText(workspace.getDescription());

        vh.textNumber.setText("Projects: " +
                realm.where(Project.class).equalTo("workspace.id", workspace.getId()).findAll().size());

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
                intent.putExtra("workspaceID", workspace.getId());
                context.startActivity(intent);
            }
        });

        vh.bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (workspaces.get(position).getId() != 1) {
                    realm.beginTransaction();
                    RealmResults<Project> results =
                            realm.where(Project.class).equalTo("workspace.id", workspace.getId()).findAll();
                    Log.d("myLogTest", "size = " + results.size());
                    while (results.size() != 0)
                    {
                        Log.d("myLogTest", "i = ");
                        results.get(0).setWorkspace(realm.where(Workspace.class).equalTo("id", 1).findFirst());
                    }
                    workspace.deleteFromRealm();
                    realm.commitTransaction();
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, workspaces.size());

                }
                else
                    Toast.makeText(context, "Default workspace can't be removed.", Toast.LENGTH_LONG).show();
            }
        });
    }
}