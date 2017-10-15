package com.ponomarevigor.androidgames.mytimetracker.Task;

import android.util.Log;
import android.widget.TextView;

/**
 * Created by Igorek on 12.10.2017.
 */

public class Task {
    public String name;
    public String description;
    public MyTimerTask timer;
    public long timeCreated;
    public long timeStart;
    public long timePause;
    public long timeFinish;
    public boolean isActive;

    public static final int TASK_CREATED = 0;
    public static final int TASK_RUNNING = 1;
    public static final int TASK_PAUSED = 2;
    public static final int TASK_STOPPED = 3;

    public int state;
    public long duration;

    public Task(String name, String description)
    {
        this.name = name;
        this.description = description;
        isActive = false;
        timer = new MyTimerTask();
        state = TASK_CREATED;
        duration = 0;
    }

    public void setTextView(TextView textView)
    {
        timer.setTextView(textView);
    }

    public void setActive(boolean isActive)
    {
        this.isActive = isActive;
    }

    public boolean getActive()
    {
        return isActive;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimePause() {
        return timePause;
    }

    public void setTimePause(long timePause) {
        this.timePause = timePause;
    }

    public long getTimeFinish() {
        return timeFinish;
    }

    public void setTimeFinish(long timeFinish) {
        this.timeFinish = timeFinish;
    }

    public void start()
    {
        timer.start();
    }

    public void pause()
    {
        timer.pause();
    }

    public void stop()
    {
        timer.stop();
    }

    public void setTimeStart()
    {
        /* Нужно для того, чтобы в момент запуска приложения корректно
        отображало пройденное время.
        Вычисляем разницу между запуском задания и текущим временем,
        эту разницу прибавляем к уже пройденному времени.
        Вроде работает... */
        long currentTime = System.currentTimeMillis() / 1000;
        long step = currentTime - timeStart;
        duration += step;
        timer.setStep(duration);
        Log.d("myLog", "startStep = " + duration);
    }

    public void setTimePause()
    {
        timer.setStep(getTimePause());
    }

    public void setTimeStop()
    {
        timer.setStep(getTimeFinish());
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
