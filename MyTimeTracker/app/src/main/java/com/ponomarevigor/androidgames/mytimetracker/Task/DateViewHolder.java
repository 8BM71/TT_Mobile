package com.ponomarevigor.androidgames.mytimetracker.Task;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ponomarevigor.androidgames.mytimetracker.R;

/**
 * Created by Igorek on 21.10.2017.
 */

public class DateViewHolder extends RecyclerView.ViewHolder{
    TextView tvName;
    Context context;

    public DateViewHolder(View view, Context context) {
        super(view);
        this.context = context;
        tvName = (TextView)view.findViewById(R.id.tvTaskName);
    }
}

