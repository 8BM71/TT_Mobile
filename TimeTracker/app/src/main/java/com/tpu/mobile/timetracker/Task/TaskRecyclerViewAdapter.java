package com.tpu.mobile.timetracker.Task;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tpu.mobile.timetracker.Database.Model.Project;
import com.tpu.mobile.timetracker.Database.Model.StatisticsTask;
import com.tpu.mobile.timetracker.Database.Model.Task;
import com.tpu.mobile.timetracker.R;
import com.tpu.mobile.timetracker.Task.ItemTaskTouchHelper.ItemTaskTouchHelperAdapter;
import com.tpu.mobile.timetracker.TaskEdit.TaskEditActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Igorek on 12.10.2017.
 */

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTaskTouchHelperAdapter{
    Context context;
    List<ModelTask> models;
    List<Task> tasks;
    Project project;
    Realm realm;
    int types[];

    public TaskRecyclerViewAdapter(Context context, List<ModelTask> models, List<Task> tasks, Project project, Realm realm) {
        this.context = context;
        this.tasks = tasks;
        this.project = project;
        this.realm = realm;
        setModels(models);
    }

    @Override
    public int getItemViewType(int position) {
        return (types[position] == 1) ? 1:0;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setProject(Project project)
    {
        this.project = project;
    }

    public void setModels(List<ModelTask> models)
    {
        this.models = models;
        if (models != null) {
            types = new int[models.size()];
            for (int i = 0; i < models.size(); i++) {
                if (models.get(i).getDate() == -1)
                    types[i] = 1;
                else
                    types[i] = 0;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (models != null)
            return models.size();
        else
            return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new DateViewHolder(LayoutInflater.from(context).inflate(
                    R.layout.task_item_date, parent, false), context);
        else
            return new TaskViewHolder(LayoutInflater.from(context).inflate(
                    R.layout.task_item_main, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == 0) {
            final DateViewHolder vh = (DateViewHolder) holder;
            long date = models.get(position).getDate() * 3600000; //ЧАС
            String time = new SimpleDateFormat("dd MMMM yyyy HH:mm").format(new Date(date));
            vh.tvName.setText(time);
        }
        else {
            final TaskViewHolder vh = (TaskViewHolder) holder;
            final Task task = models.get(position).getTask();
            vh.textName.setText(task.getName());
            if (project == null) {
                Project project = task.getProject();
                vh.imageIndicator.setBackgroundColor(project.getColor());
                vh.textDescription.setText(project.getName());
            } else {
                vh.imageIndicator.setBackgroundColor(project.getColor());
                if (task.getDescription().equals(""))
                    vh.textDescription.setText("No description");
                else
                    vh.textDescription.setText(task.getDescription());
            }

            if (task.getState() == Task.TASK_CREATED)
                vh.create();

            if (task.getState() == Task.TASK_RUNNING) {
                vh.setStep(task.getTimeStart());
                vh.start();
            }

            if (task.getState() == Task.TASK_STOPPED) {
                vh.setStep(task.getDuration());
                vh.chronometer.setBase(SystemClock.elapsedRealtime() - vh.getStep());
                vh.stop();
            }

            View.OnClickListener onClickActive = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (task.getState() == Task.TASK_STOPPED ||
                            task.getState() == Task.TASK_CREATED) {
                        realm.beginTransaction();
                        task.setTimeCreated(System.currentTimeMillis());/////////////
                        task.setTimeStart(System.currentTimeMillis());
                        task.setState(Task.TASK_RUNNING);
                        task.setDuration(vh.getStep());
                        realm.commitTransaction();
                        vh.start();
                        update();
                        return;
                    }

                    if (task.getState() == Task.TASK_RUNNING) {
                        realm.beginTransaction();
                        task.setDuration(vh.getCurrentStep());
                        task.setState(Task.TASK_STOPPED);
                        task.setTimeFinish(System.currentTimeMillis());
                        StatisticsTask stat = realm.createObject(StatisticsTask.class);
                        stat.setId(realm.where(StatisticsTask.class).max("id").intValue() + 1);
                        stat.setDuration(task.getDuration());
                        stat.setStart(task.getTimeCreated());
                        stat.setEnd(task.getTimeFinish());
                        task.getStatistics().add(stat);
                        realm.commitTransaction();
                        vh.stop();
                        return;
                    }
                }
            };
            vh.ibActive.setOnClickListener(onClickActive);

            vh.layoutClickable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TaskEditActivity.class);
                    intent.putExtra("taskID", task.getId());
                    if (project == null) {
                        Project prj = task.getProject();
                        intent.putExtra("projectID", prj.getId());
                    } else
                        intent.putExtra("projectID", project.getId());
                    context.startActivity(intent);
                }
            });
        }
    }


    public void update()
    {
        setModels(TaskActivity.setData(tasks));
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;

    }

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {
        final int position = viewHolder.getAdapterPosition();
        Task task = models.get(position).getTask();
        final Task deletedTask = realm.copyFromRealm(task);
        final int idProject = deletedTask.getProject().getId();
        deletedTask.setProject(realm.where(Project.class).equalTo("id", idProject).findFirst());

        realm.beginTransaction();
        task.deleteFromRealm();
        realm.commitTransaction();

        update();

        String name = deletedTask.getName();
        Snackbar snackbar = Snackbar.make(viewHolder.itemView,
                name + " is removed!", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.beginTransaction();
                realm.copyToRealm(deletedTask);
                realm.commitTransaction();
                update();
            }
        });

        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }
}