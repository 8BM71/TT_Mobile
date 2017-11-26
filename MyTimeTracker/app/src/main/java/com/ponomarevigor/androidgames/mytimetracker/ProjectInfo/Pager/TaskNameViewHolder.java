package com.ponomarevigor.androidgames.mytimetracker.ProjectInfo.Pager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ponomarevigor.androidgames.mytimetracker.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Igorek on 03.11.2017.
 */

public class TaskNameViewHolder extends RecyclerView.ViewHolder {
    TextView tvName;
    Context context;

    public TaskNameViewHolder(View view, Context context) {
        super(view);
        this.context = context;
        tvName = (TextView)view.findViewById(R.id.tvTaskName);
    }
}
