package com.ponomarevigor.androidgames.mytimetracker.Project;

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
import android.widget.Toast;

import com.ponomarevigor.androidgames.mytimetracker.Database.Project;
import com.ponomarevigor.androidgames.mytimetracker.Database.Task;
import com.ponomarevigor.androidgames.mytimetracker.Database.Workspace;
import com.ponomarevigor.androidgames.mytimetracker.ProjectInfo.ProjectInfoActivity;
import com.ponomarevigor.androidgames.mytimetracker.R;
import com.ponomarevigor.androidgames.mytimetracker.Task.TaskActivity;
import com.ponomarevigor.androidgames.mytimetracker.Workspace.WorkspaceEditActivity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.R.attr.id;

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
        return new ProjectViewHolder(LayoutInflater.from(context).inflate(R.layout.item_project_recyclerview_1, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ProjectViewHolder vh = (ProjectViewHolder) holder;
        final Project project = projects.get(position);
        vh.isOpen = false;
        vh.baseLayout.close(false);
        if (project.getName().equals(""))
            vh.textName.setText("no name");
        else
            vh.textName.setText(project.getName());

        if (project.getDescription().equals(""))
            vh.textDescription.setText("no description");
        else
            vh.textDescription.setText(project.getDescription());

        vh.textNumber.setText("Tasks: " + realm.where(Task.class).equalTo("project.id", project.getId())
                                        .findAll().size());
        vh.imageIndicator.setBackgroundColor(project.getColor());

        vh.layoutClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showActionDialog(workspace.getId());
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
                Intent intent = new Intent(context, TaskActivity.class);
                intent.putExtra("projectID", project.getId());
                context.startActivity(intent);
            }
        });

        vh.bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProjectEditActivity.class);
                intent.putExtra("projectID", project.getId());
//                intent.putExtra("workspaceID", workspace.getId());
                context.startActivity(intent);
            }
        });

        vh.bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (projects.get(position).getId() != 1) {
                    final Project project = realm.copyFromRealm(projects.get(position));
                    final List<Task> tasks = realm.copyFromRealm(
                            realm.where(Task.class).equalTo("project.id", project.getId()).findAll());
                    realm.beginTransaction();
                    realm.where(Task.class).equalTo("project.id", project.getId()).findAll().deleteAllFromRealm();
                    projects.get(position).deleteFromRealm();
                    realm.commitTransaction();

                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, projects.size());

                    Snackbar snackbar = Snackbar.make(vh.itemView,
                            project.getName() + " is removed!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            realm.beginTransaction();
                            //realm.copyToRealm(project);
                            realm.copyToRealm(tasks);
                            realm.commitTransaction();
                            notifyItemInserted(position);
                            notifyItemRangeChanged(position, projects.size());
                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();*/

                if (projects.get(position).getId() != 1) {
                    realm.beginTransaction();
                    realm.where(Task.class).equalTo("project.id", project.getId()).findAll().deleteAllFromRealm();
                    projects.get(position).deleteFromRealm();
                    realm.commitTransaction();
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, projects.size());
                }
                else
                {
                    Toast.makeText(context, "Default project can't be removed.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void showActionDialog(final int id) {
      /*  AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                //.setTitle("Select an action!")
                //.setIcon(R.drawable.task)

                .setCancelable(true)
                .setNegativeButton("Edit a project",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                Intent intent = new Intent(context, ProjectEditActivity.class);
                                intent.putExtra("projectID", id);
                                context.startActivity(intent);
                            }
                        })
                .setPositiveButton("Open a tasks",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                Intent intent = new Intent(context, TaskActivity.class);
                                intent.putExtra("projectID", id);
                                context.startActivity(intent);
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();*/

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


/*    @Override
    public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {
        final int position = viewHolder.getAdapterPosition();
        final Project project = realm.copyFromRealm(projects.get(position));
        final List<Task> tasks = realm.copyFromRealm(projects.get(position).getTasks());
        realm.beginTransaction();
        projects.get(position).getTasks().deleteAllFromRealm();
        projects.get(position).deleteFromRealm();
        realm.commitTransaction();

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, projects.size());

        Snackbar snackbar = Snackbar.make(viewHolder.itemView,
                project.getName() + " is removed!", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.beginTransaction();
                realm.copyToRealm(project);
                realm.commitTransaction();
                notifyItemInserted(position);
                notifyItemRangeChanged(position, projects.size());
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }*/
}