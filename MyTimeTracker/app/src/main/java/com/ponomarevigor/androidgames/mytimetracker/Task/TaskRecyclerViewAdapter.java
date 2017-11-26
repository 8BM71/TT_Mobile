package com.ponomarevigor.androidgames.mytimetracker.Task;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.LoginFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ponomarevigor.androidgames.mytimetracker.Database.Project;
import com.ponomarevigor.androidgames.mytimetracker.Database.StatisticsTask;
import com.ponomarevigor.androidgames.mytimetracker.Database.Task;
import com.ponomarevigor.androidgames.mytimetracker.R;
import com.ponomarevigor.androidgames.mytimetracker.TaskEditing.TaskEditingActivity;
import com.ponomarevigor.androidgames.mytimetracker.Task.ItemTaskTouchHelper.ItemTaskTouchHelperAdapter;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Igorek on 12.10.2017.
 */

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTaskTouchHelperAdapter {
    Context context;
    List<Task> tasks;
    Project project;
    Realm realm;

    public TaskRecyclerViewAdapter(Context context, List<Task> tasks, Project project, Realm realm) {
        this.context = context;
        this.tasks = tasks;
        this.project = project;
        this.realm = realm;
    }

    public void setTasks(List<Task> tasks)
    {
        this.tasks = tasks;
    }

    public void setProject(Project project)
    {
        this.project = project;
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(context).inflate(R.layout.item_task_recyclerview_1, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final TaskViewHolder vh = (TaskViewHolder) holder;
        final Task task = tasks.get(position);
        if (task.getName().equals(""))
            vh.textName.setText("No name");
        else
            vh.textName.setText(task.getName());
        if (project == null)
        {
            Project project = task.getProject();
            vh.imageIndicator.setBackgroundColor(project.getColor());
            //vh.imageIndicator.getDrawable().setColorFilter(project.getColor(), PorterDuff.Mode.SRC_ATOP);
            vh.textDescription.setText(project.getName());
        }
        else {
            vh.imageIndicator.setBackgroundColor(project.getColor());
            if (task.getDescription().equals(""))
                vh.textDescription.setText("No description");
            else
                vh.textDescription.setText(task.getDescription());
        }

        if (task.getState() == Task.TASK_CREATED)
        {
            vh.create();
        }

        if (task.getState() == Task.TASK_RUNNING) {
            vh.setStep(task.getTimeStart());
            vh.start();
        }

        if (task.getState() == Task.TASK_PAUSED) {
            vh.setStep(task.getTimePause());
            vh.chronometer.setBase(SystemClock.elapsedRealtime() - vh.getStep());
            vh.pause();
        }

        if (task.getState() == Task.TASK_STOPPED) {
            vh.setStep(task.getDuration());
            vh.chronometer.setBase(SystemClock.elapsedRealtime() - vh.getStep());
            vh.stop();
        }

        View.OnClickListener onClickActive = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task.getState() == Task.TASK_PAUSED ||
                        task.getState() == Task.TASK_STOPPED ||
                        task.getState() == Task.TASK_CREATED) {

                    realm.beginTransaction();
                    task.setTimeStart(System.currentTimeMillis());
                    task.setState(Task.TASK_RUNNING);
                    task.setDuration(vh.getStep());
                    realm.commitTransaction();
                    vh.start();
                    return;
                }

                if (task.getState() == Task.TASK_RUNNING) {
                    realm.beginTransaction();
                    task.setState(Task.TASK_PAUSED);
                    task.setTimePause(vh.getCurrentStep());
                    realm.commitTransaction();
                    vh.pause();
                    return;
                }
            }
        };
        vh.ibActive.setOnClickListener(onClickActive);

        View.OnClickListener onClickFinish = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                if (task.getState() == Task.TASK_PAUSED)
                    task.setDuration(vh.getStep());
                else
                    task.setDuration(vh.getCurrentStep());
                task.setState(Task.TASK_STOPPED);
                task.setTimeFinish(System.currentTimeMillis());
                StatisticsTask stat = realm.createObject(StatisticsTask.class);
                stat.setId(realm.where(StatisticsTask.class).max("id").intValue() + 1);
                stat.setState(StatisticsTask.SET_AUTO);
                stat.setStartManual(task.getTimeCreated());
                stat.setStartAuto(task.getTimeCreated());
                stat.setEndManual(task.getTimeFinish());
                stat.setEndAuto(task.getTimeFinish());
                stat.setDurationManual(task.getDuration());
                stat.setDurationAuto(task.getDuration());
                task.getStatistics().add(stat);
                realm.commitTransaction();
                vh.stop();
            }
        };
        vh.ibFinish.setOnClickListener(onClickFinish);

        vh.layoutClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //!!!!!!!!!!!!!!!!!!!!!!!!!!! Если ALL TASK!!!!!!!!!!!!!!!!!!!!!!!
                Intent intent = new Intent(context, TaskEditingActivity.class);
                intent.putExtra("taskID", task.getId());
                if (project == null)
                {
                    Project prj = task.getProject();
                    intent.putExtra("projectID", prj.getId());
                }
                else
                    intent.putExtra("projectID", project.getId());
                //intent.putExtra("taskObj", (Parcelable) task);
                //intent.putExtra("taskObj", task.);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        Task task = tasks.get(position);
        realm.beginTransaction();
        task.deleteFromRealm();
        realm.commitTransaction();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tasks.size());
    }

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {
        final int position = viewHolder.getAdapterPosition();
        final Task deletedTask = realm.copyFromRealm(tasks.get(position));
        final int idProject = deletedTask.getProject().getId();
        deletedTask.setProject(realm.where(Project.class).equalTo("id", idProject).findFirst());
        realm.beginTransaction();
        tasks.get(position).deleteFromRealm();

        realm.commitTransaction();

        Log.d("myLogTest", "1id = " + idProject);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tasks.size());

        String name = deletedTask.getName();
        if (name.equals(""))
            name = "Task";

        Snackbar snackbar = Snackbar.make(viewHolder.itemView,
                name + " is removed!", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.beginTransaction();
                realm.copyToRealm(deletedTask);
                realm.commitTransaction();
                notifyItemInserted(position);
                notifyItemRangeChanged(position, tasks.size());
            }
        });

        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }
}