package com.ponomarevigor.androidgames.mytimetracker.Tests.WorkSpaceTest1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ponomarevigor.androidgames.mytimetracker.Database.Workspace;
import com.ponomarevigor.androidgames.mytimetracker.Project.ProjectEditActivity;
import com.ponomarevigor.androidgames.mytimetracker.R;
import com.ponomarevigor.androidgames.mytimetracker.Task.TaskActivity;
import com.ponomarevigor.androidgames.mytimetracker.Tests.WorkSpaceTest1.ItemWorkspaceTouchHelper.ItemWorkspaceTouchHelperAdapter;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Igorek on 12.10.2017.
 */

public class WorkspaceRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemWorkspaceTouchHelperAdapter {
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
        return new WorkspaceViewHolder(LayoutInflater.from(context).inflate(R.layout.item_workspace_recyclerview_1, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final WorkspaceViewHolder vh = (WorkspaceViewHolder) holder;
        final Workspace workspace = workspaces.get(position);
        if (workspace.getName().equals(""))
            vh.textName.setText("no name");
        else
            vh.textName.setText(workspace.getName());

        if (workspace.getDescription().equals(""))
            vh.textDescription.setText("no description");
        else
            vh.textDescription.setText(workspace.getDescription());

        vh.textNumber.setText("Projects: " + workspace.getProjects().size());

        vh.layoutClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActionDialog(workspace.getId());
            }
        });
    }

    public void showActionDialog(final int id) {
        final View v = LayoutInflater.from(context).inflate(R.layout.dialog_project_action, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Button bGo = (Button) v.findViewById(R.id.bGoTask);
        bGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TaskActivity.class);
                intent.putExtra("projectID", id);
                context.startActivity(intent);
            }
        });
        Button bEdit = (Button) v.findViewById(R.id.bEditProject);
        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProjectEditActivity.class);
                intent.putExtra("projectID", id);
                context.startActivity(intent);
            }
        });
        builder.setView(v);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
    }

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {
        final int position = viewHolder.getAdapterPosition();
        final Workspace workspace = realm.copyFromRealm(workspaces.get(position));
        realm.beginTransaction();
        workspaces.get(position).deleteFromRealm();
        realm.commitTransaction();

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, workspaces.size());

        Snackbar snackbar = Snackbar.make(viewHolder.itemView,
                workspace.getName() + " is removed!", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.beginTransaction();
                realm.copyToRealm(workspace);
                realm.commitTransaction();
                notifyItemInserted(position);
                notifyItemRangeChanged(position, workspaces.size());
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }
}