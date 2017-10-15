package com.ponomarevigor.androidgames.mytimetracker.Task;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Ponomarev Igor on 04.10.2017.
 */

public class MyTimerTask implements Runnable {

    public int days, hours, minutes, seconds;
    public long step;
    Handler handler;
    TextView textView;
    private String time;

    public MyTimerTask()
    {
        handler = new Handler();
        step = 0;
    }

    public void setTextView(TextView textView)
    {
        this.textView = textView;
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    }

    public void start() {
        handler.postDelayed(this, 1000);
    }

    public void pause() {
        handler.removeCallbacks(this);
        textView.setText(calculateTime(step));
    }

    public void stop() {
        handler.removeCallbacks(this);
        textView.setText(calculateTime(step));
        clear();
    }

    private void clear() {
        step = 0;
        seconds = 0;
        minutes = 0;
        hours = 0;
    }

    @Override
    public void run() {
        step++;
        time = calculateTime(step);
        textView.setText(time);
        //Log.d("myLog", "TIME: " + time);
        handler.postDelayed(this, 1000);
    }

    private String calculateTime(long step)
    {
        days = (int)step / 86400;
        hours = (int)(step % (86400)) / 3600;
        minutes = (int)(step % 3600) / 60;
        seconds = (int)(step % 60);
        if (days == 0)
            time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            time = String.format("%dd. %02d:%02d:%02d", days, hours, minutes, seconds);
        return time;
    }
}
