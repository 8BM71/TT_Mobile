package com.ponomarevigor.androidgames.mytimetracker.ProjectInfo.Pager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ponomarevigor.androidgames.mytimetracker.Database.StatisticsTask;
import com.ponomarevigor.androidgames.mytimetracker.Database.Task;
import com.ponomarevigor.androidgames.mytimetracker.R;

import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


/**
 * Created by Igorek on 01.11.2017.
 */

public class RecyclerAdapterStatistics extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<ModelTaskStat> models;
    List<Task> tasks;
    RealmList<StatisticsTask> stats;
    Realm realm;
    Task task = null;
    int types[];

    public RecyclerAdapterStatistics(Context context, List<Task> tasks, RealmList<StatisticsTask> stats, List<ModelTaskStat> models,
                                     Realm realm) {
        this.context = context;
        this.stats = stats;
        this.tasks = tasks;
        this.models = models;
        this.realm = realm;
        types = new int[models.size()];
        for (int i = 0; i < models.size(); i++)
        {
            if (models.get(i).getTask() == null)
                types[i] = 1;
            else
                types[i] = 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (types[position] == 1) ? 1:0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new TaskNameViewHolder(LayoutInflater.from(context).inflate(
                    R.layout.item_project_statistics_name, parent, false), context);
        else
            return new TaskStatViewHolder(LayoutInflater.from(context).inflate(
                    R.layout.item_project_statistics, parent, false), context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == 0) {
            final TaskNameViewHolder vh = (TaskNameViewHolder) holder;
            vh.tvName.setText(models.get(position).getTask().getName());
        }
        else {
            final TaskStatViewHolder vh = (TaskStatViewHolder) holder;
            final StatisticsTask stat = models.get(position).getStat();

            if (stat.getState() == StatisticsTask.SET_AUTO) {
                vh.tvStartDate.setText(vh.calculateDate(stat.getStartAuto()));
                vh.tvEndDate.setText(vh.calculateDate(stat.getEndAuto()));
                vh.tvTime.setText(vh.calculateTime(stat.getDurationAuto()));
            } else {
                vh.tvStartDate.setText(vh.calculateDate(stat.getStartManual()));
                vh.tvEndDate.setText(vh.calculateDate(stat.getEndManual()));
                vh.tvTime.setText(vh.calculateTime(stat.getDurationManual()));
            }

            String note = stat.getDescription();
            if (note != null) {
                if (!note.isEmpty()) {
                    vh.layoutNote.setVisibility(View.VISIBLE);
                    vh.tvNote.setText(note);
                }
                else
                    vh.layoutNote.setVisibility(View.GONE);
            } else
                vh.layoutNote.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return models.size();
    }

}
