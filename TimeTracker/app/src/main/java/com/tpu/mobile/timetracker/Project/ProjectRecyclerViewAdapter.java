package com.tpu.mobile.timetracker.Project;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tpu.mobile.timetracker.Database.Model.Project;
import com.tpu.mobile.timetracker.Database.Model.Task;
import com.tpu.mobile.timetracker.Database.Model.Workspace;
import com.tpu.mobile.timetracker.ProjectInfo.ProjectInfoActivity;
import com.tpu.mobile.timetracker.R;
import com.tpu.mobile.timetracker.Main.MainActivity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Igorek on 12.10.2017.
 */

public class ProjectRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Project> projects;
    Workspace workspace;
    Realm realm;

    public ProjectRecyclerViewAdapter(Context context, List<Project> projects, Workspace workspace, Realm realm) {
        this.context = context;
        this.projects = projects;
        this.workspace = workspace;
        this.realm = realm;
    }

    public void setProjects(RealmResults<Project> projects) {
        this.projects = projects;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProjectViewHolder(LayoutInflater.from(context).inflate(R.layout.project_item_main, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ProjectViewHolder vh = (ProjectViewHolder) holder;
        final Project project = projects.get(position);
        vh.isOpen = false;
        vh.baseLayout.close(false);
        vh.textName.setText(project.getName());

        if (project.getDescription().equals(""))
            vh.textDescription.setText("No description");
        else
            vh.textDescription.setText(project.getDescription());

        vh.textNumber.setText("Tasks: " + realm.where(Task.class).equalTo("project.id", project.getId())
                                        .findAll().size());
        vh.imageIndicator.setBackgroundColor(project.getColor());

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

        vh.bInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProjectInfoActivity.class);
                intent.putExtra("projectID", project.getId());
                context.startActivity(intent);
            }
        });

        vh.bOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(context, TaskActivity.class);
                //intent.putExtra("projectID", project.getId());
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("projectID", project.getId());
                intent.putExtra("fragmentID", R.id.nav_task);
                context.startActivity(intent);
            }
        });

        vh.bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProjectEditActivity.class);
                intent.putExtra("projectID", project.getId());
                context.startActivity(intent);
            }
        });

        vh.bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (projects.get(position).getId() != 1) {
                    realm.beginTransaction();
                    realm.where(Task.class).equalTo("project.id", project.getId()).findAll().deleteAllFromRealm();
                    projects.get(position).deleteFromRealm();
                    realm.commitTransaction();
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, projects.size());
                }
                else
                    Toast.makeText(context, "Default project can't be removed.", Toast.LENGTH_LONG).show();
            }
        });
    }
}