package com.ponomarevigor.androidgames.mytimetracker;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ponomarevigor.androidgames.mytimetracker.Database.Task;
import com.ponomarevigor.androidgames.mytimetracker.ItemTaskTouchHelper.ItemTaskTouchHelperAdapter;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Igorek on 12.10.2017.
 */

public class RecyclerViewTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTaskTouchHelperAdapter {
    Context context;
    List<Task> tasks;
    Realm realm;

    public RecyclerViewTaskAdapter(Context context, List<Task> tasks, Realm realm) {
        this.context = context;
        this.tasks = tasks;
        this.realm = realm;
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderTask(LayoutInflater.from(context).inflate(R.layout.recyclerview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolderTask vh = (ViewHolderTask) holder;
        final Task task = tasks.get(position);
        vh.textName.setText(task.getName());
        vh.textDescription.setText(task.getDescription());

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
            vh.setStep(task.getTimeFinish());
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
                    task.setTimeFinish(vh.getStep());
                else
                    task.setTimeFinish(vh.getCurrentStep());
                task.setState(Task.TASK_STOPPED);
                realm.commitTransaction();
                vh.stop();
            }
        };
        vh.ibFinish.setOnClickListener(onClickFinish);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        realm.beginTransaction();
        Task taskModel1 = tasks.get(fromPosition);
        Task taskModel2 = tasks.get(toPosition);
        taskModel1.setPosition(toPosition);
        taskModel2.setPosition(fromPosition);
        realm.commitTransaction();
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        realm.beginTransaction();
        realm.where(Task.class).equalTo("position", position).findFirst().deleteFromRealm();
        for (int i = 0; i < tasks.size() - position; i++)
        {
            Task task = tasks.get(position + i);
            task.setPosition(task.getPosition() - 1);
        }
        realm.commitTransaction();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tasks.size());
    }

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder viewHolder) {
        final int position = viewHolder.getAdapterPosition();
        final Task deletedTask = realm.copyFromRealm(tasks.get(position));
        realm.beginTransaction();
        realm.where(Task.class).equalTo("position", position).findFirst().deleteFromRealm();
        for (int i = 0; i < tasks.size() - position; i++)
        {
            Task task = tasks.get(position + i);
            task.setPosition(task.getPosition() - 1);
        }
        realm.commitTransaction();

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tasks.size());

        Snackbar snackbar = Snackbar.make(viewHolder.itemView,
                deletedTask.getName() + " is removed!", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.beginTransaction();
                for (int i = tasks.size(); i > position; i--)
                {
                    Task task = tasks.get(i - 1);
                    task.setPosition(task.getPosition() + 1);
                }
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