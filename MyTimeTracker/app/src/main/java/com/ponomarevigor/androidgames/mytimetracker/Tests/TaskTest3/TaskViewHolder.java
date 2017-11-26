package com.ponomarevigor.androidgames.mytimetracker.Tests.TaskTest3;

import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ponomarevigor.androidgames.mytimetracker.R;

/**
 * Created by Igorek on 21.10.2017.
 */

public class TaskViewHolder extends RecyclerView.ViewHolder{
    public RelativeLayout baseLayout;
    public RelativeLayout layoutClickable;
    public RelativeLayout frontLayout;
    public ImageView imageIndicator;
    public TextView textName;
    public TextView textDescription;
    public Chronometer chronometer;
    public ImageButton ibActive;
    public ImageButton ibFinish;
    long step = 0;

    public TaskViewHolder(View view) {
        super(view);
        baseLayout = (RelativeLayout) view.findViewById(R.id.baseLayout);
        layoutClickable = (RelativeLayout) view.findViewById(R.id.layoutClickable);
        frontLayout = (RelativeLayout) view.findViewById(R.id.frontLayout);
        imageIndicator = (ImageView)view.findViewById(R.id.imageIndicator);
        textName = (TextView) view.findViewById(R.id.tvNameTask);
        textDescription = (TextView) view.findViewById(R.id.tvDescriptionTask);
        chronometer = (Chronometer) view.findViewById(R.id.tvTimeTask);
        ibActive = (ImageButton) view.findViewById(R.id.ibActive);
        ibFinish = (ImageButton) view.findViewById(R.id.ibFinish);
        ibFinish.setEnabled(false);

        /* chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                chronometer.setText(getTime(elapsedMillis/1000)); //можно без деления, но тогда исправить код
            }
        });*/
    }

    private String getTime(long step) {
        String time;
        int days = (int) step / 86400;
        int hours = (int) (step % (86400)) / 3600;
        int minutes = (int) (step % 3600) / 60;
        int seconds = (int) (step % 60);
        if (days == 0)
            time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            time = String.format("%dd. %02d:%02d:%02d", days, hours, minutes, seconds);
        return time;
    }

    public void create() {
        ibActive.setImageResource(R.drawable.start);
        ibActive.setEnabled(true);
        ibFinish.setEnabled(false);
        frontLayout.setBackgroundColor(Color.WHITE);
        imageIndicator.setBackgroundColor(Color.WHITE);
        step = 0;
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        chronometer.setTextColor(Color.DKGRAY);
    }

    public void start() {
        ibActive.setImageResource(R.drawable.pause);
        chronometer.setBase(SystemClock.elapsedRealtime() - step);
        chronometer.start();
        chronometer.setTextColor(Color.parseColor("#1ABC9C"));
        imageIndicator.setBackgroundColor(Color.parseColor("#1ABC9C"));
        ibFinish.setEnabled(true);
    }

    public void pause() {
        ibActive.setImageResource(R.drawable.start);
        chronometer.stop();
        step = getCurrentStep();
        chronometer.setTextColor(Color.GRAY);
        imageIndicator.setBackgroundColor(Color.LTGRAY);
        ibFinish.setEnabled(true);
    }

    public void stop() {
        ibActive.setImageResource(R.drawable.start);
        chronometer.stop();
        step = 0;
        chronometer.setTextColor(Color.RED);
        imageIndicator.setBackgroundColor(Color.parseColor("#99FF0000"));
        ibFinish.setEnabled(false);
    }

    public long getCurrentStep() {
        long r = SystemClock.elapsedRealtime() - chronometer.getBase();
        r = r / 1000 * 1000;
        return r;
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }
}
