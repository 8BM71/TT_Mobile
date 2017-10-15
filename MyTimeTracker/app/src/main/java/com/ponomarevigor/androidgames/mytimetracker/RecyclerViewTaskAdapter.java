package com.ponomarevigor.androidgames.mytimetracker;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ponomarevigor.androidgames.mytimetracker.Database.TaskModel;
import com.ponomarevigor.androidgames.mytimetracker.Task.Task;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Igorek on 12.10.2017.
 */

/*
    Привести в человеческих вид.
    Переписать слушатели кнопок.
    Объединить в функции. Убрать мусор.

    Откорректировать обновление адаптера при добавлении или удалении тасков.
 */

public class RecyclerViewTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    List<Task> tasks;
    RealmResults<TaskModel> taskModels;
    Realm realm;
    int[] checkPositions;

    public RecyclerViewTaskAdapter(Context context, List<Task> tasks, Realm realm) {
        this.context = context;
        this.tasks = tasks;
        this.realm = realm;
        checkPositions = new int[100];
    }

    @Override
    public long getItemId(int position) {
        return position;
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
        tasks.get(position).setTextView(vh.textTime);
        final Task task = tasks.get(position);

        //Чтобы ничего не сбивалось при добавлении элемента.
        //Костыль, который нужно избежать
        if (checkPositions[position] == 1) return;
        checkPositions[position] = 1;

        vh.textName.setText(task.getName());

        if (task.getName().equals(""))
        {
            vh.textName.setText("Task: " + position);
        }

        vh.textDescription.setText(task.getDescription());
        if (task.getDescription().equals(""))
        {
            vh.textDescription.setText("Description: " + position);
        }


        if (task.getState() == Task.TASK_RUNNING)
            start(task, vh);

        if (task.getState() == Task.TASK_PAUSED)
            pause(task, vh);

        if (task.getState() == Task.TASK_STOPPED)
            stop(task, vh);


        View.OnClickListener onClickListener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!task.getActive()) {
                    realm.beginTransaction();
                    TaskModel taskModel = realm.where(TaskModel.class).findAll().get(position);
                    taskModel.setTimeStart(System.currentTimeMillis()/1000);
                    taskModel.setState(TaskModel.TASK_RUNNING);
                    taskModel.setDuration(task.timer.getStep());
                    realm.commitTransaction();

                    start(task, vh);
                    return;
                }

                if (task.getActive()) {
                    realm.beginTransaction();
                    TaskModel taskModel = realm.where(TaskModel.class).findAll().get(position);
                    taskModel.setState(TaskModel.TASK_PAUSED);
                    taskModel.setTimePause(task.timer.getStep());
                    realm.commitTransaction();

                    pause(task, vh);
                    return;
                }

            }
        };
        vh.ibActive.setOnClickListener(onClickListener1);

        View.OnClickListener onClickListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                TaskModel taskModel = realm.where(TaskModel.class).findAll().get(position);
                taskModel.setState(TaskModel.TASK_STOPPED);
                taskModel.setTimeFinish(task.timer.getStep());
                realm.commitTransaction();

                stop(task, vh);
                vh.ibFinish.setEnabled(false);
            }
        };
        vh.ibFinish.setOnClickListener(onClickListener2);

        View.OnClickListener onClickListener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.stop();
                tasks.remove(position);
                realm.beginTransaction();
                TaskModel taskModel = realm.where(TaskModel.class).findAll().get(position);
                realm.where(TaskModel.class).findAll().get(position).deleteFromRealm(taskModel);
                realm.commitTransaction();
                //notifyItemRemoved(position);
                //notifyDataSetChanged();
            }
        };
        vh.ibRemove.setOnClickListener(onClickListener3);
    }

    private void start(Task task, ViewHolderTask vh)
    {
        task.pause();
        task.start();
        vh.ibFinish.setEnabled(true);
        vh.ibActive.setImageResource(R.drawable.pause);
        task.setActive(true);
        vh.textTime.setTextColor(Color.parseColor("#FF16A085"));
        vh.layout.setBackgroundColor(Color.parseColor("#3316A085"));
        vh.ibFinish.setEnabled(true);
    }

    private void pause(Task task, ViewHolderTask vh)
    {
        task.pause();
        vh.ibFinish.setEnabled(true);
        vh.ibActive.setImageResource(R.drawable.start);
        task.setActive(false);
        vh.textTime.setTextColor(Color.GRAY);
        vh.layout.setBackgroundColor(Color.parseColor("#33777777"));
        vh.ibFinish.setEnabled(true);
    }

    private void stop(Task task, ViewHolderTask vh)
    {
        task.stop();
        vh.ibActive.setImageResource(R.drawable.start);
        task.setActive(false);
        vh.textTime.setTextColor(Color.RED);
        vh.layout.setBackgroundColor(Color.parseColor("#33FF8855"));
        vh.ibFinish.setEnabled(false);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    class ViewHolderTask extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView textName;
        TextView textDescription;
        TextView textTime;
        ImageButton ibActive;
        ImageButton ibFinish;
        ImageButton ibRemove;

        public ViewHolderTask(View view) {
            super(view);
            layout = (LinearLayout) view.findViewById(R.id.linear);
            textName = (TextView) view.findViewById(R.id.tvNameTask);
            textDescription = (TextView) view.findViewById(R.id.tvDescriptionTask);
            textTime = (TextView) view.findViewById(R.id.tvTimeTask);
            ibActive = (ImageButton)view.findViewById(R.id.ibActive);
            ibFinish = (ImageButton)view.findViewById(R.id.ibFinish);
            ibFinish.setEnabled(false);
            ibRemove = (ImageButton)view.findViewById(R.id.ibRemove);
            ibRemove.setVisibility(View.INVISIBLE);
            ibRemove.setEnabled(false);
        }
    }
}