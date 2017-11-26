package com.ponomarevigor.androidgames.mytimetracker.ProjectInfo.Pager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ponomarevigor.androidgames.mytimetracker.Database.StatisticsTask;
import com.ponomarevigor.androidgames.mytimetracker.R;

import io.realm.Realm;
import io.realm.RealmList;


/**
 * Created by Igorek on 01.11.2017.
 */

public class RecyclerAdapterStatisticsCopy extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    RealmList<StatisticsTask> stats;
    Realm realm;

    public RecyclerAdapterStatisticsCopy(Context context, RealmList<StatisticsTask> stats, Realm realm) {
        this.context = context;
        this.stats = stats;
        this.realm = realm;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TaskStatViewHolder(LayoutInflater.from(context).inflate(R.layout.item_project_statistics, parent, false), context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final TaskStatViewHolder vh = (TaskStatViewHolder)holder;
        final StatisticsTask stat = stats.get(position);

        if (stat.getState() == StatisticsTask.SET_AUTO) {
            vh.tvStartDate.setText(vh.calculateDate(stat.getStartAuto()));
            vh.tvEndDate.setText(vh.calculateDate(stat.getEndAuto()));
            vh.tvTime.setText(vh.calculateTime(stat.getDurationAuto()));
        }
        else {
            vh.tvStartDate.setText(vh.calculateDate(stat.getStartManual()));
            vh.tvEndDate.setText(vh.calculateDate(stat.getEndManual()));
            vh.tvTime.setText(vh.calculateTime(stat.getDurationManual()));
        }

        String note = stat.getDescription();
        if (note != null) {
            vh.layoutNote.setVisibility(View.VISIBLE);
            vh.tvNote.setText(note);
        }
        else
        vh.layoutNote.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return stats.size();
    }

}
