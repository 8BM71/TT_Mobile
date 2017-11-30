package com.ponomarevigor.androidgames.mytimetracker.TaskEdit.Pager;

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
import com.ponomarevigor.androidgames.mytimetracker.R;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;


/**
 * Created by Igorek on 01.11.2017.
 */

public class RecyclerAdapterStatistics extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<StatisticsTask> stats;
    List<ChangeStat> changeStats;
    Realm realm;

    public RecyclerAdapterStatistics(Context context, List<StatisticsTask> stats, Realm realm) {
        this.context = context;
        this.stats = stats;
        this.realm = realm;
        changeStats = new ArrayList<ChangeStat>();
        if (changeStats != null)
            changeStats.clear();
        for (int i = 0; i < stats.size(); i++)
            changeStats.add(new ChangeStat());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TaskStatViewHolder(LayoutInflater.from(context).inflate(R.layout.task_item_stat, parent, false), context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final TaskStatViewHolder vh = (TaskStatViewHolder)holder;
        final StatisticsTask stat = stats.get(position);
        final ChangeStat changeStat = changeStats.get(position);
        changeStat.setId(stat.getId());

        if (stat.getState() == StatisticsTask.SET_AUTO) {
            vh.initAuto();
            vh.tvStartDate.setText(vh.calculateDate(stat.getStartAuto()));
            vh.tvEndDate.setText(vh.calculateDate(stat.getEndAuto()));
            vh.tvTime.setText(vh.calculateTime(stat.getDurationAuto()));
        }
        else {
            vh.initManual(stat.getStartManual(), stat.getEndManual());
            vh.tvStartDate.setText(vh.calculateDate(stat.getStartManual()));
            vh.tvEndDate.setText(vh.calculateDate(stat.getEndManual()));
            vh.tvTime.setText(vh.calculateTime(stat.getDurationManual()));
        }

        vh.etDescription.setText(stat.getDescription());
        vh.tvNote.setText("Note (" + (200 - vh.etDescription.length()) + "):");

        vh.bAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vh.initAuto();
                vh.tvStartDate.setText(vh.calculateDate(stat.getStartAuto()));
                vh.tvEndDate.setText(vh.calculateDate(stat.getEndAuto()));
                vh.tvTime.setText(vh.calculateTime(stat.getDurationAuto()));

                changeStat.setState(StatisticsTask.SET_AUTO);
                realm.beginTransaction();
                stat.setState(StatisticsTask.SET_AUTO);
                realm.commitTransaction();
            }
        });

        vh.bManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vh.initManual(stat.getStartManual(), stat.getEndManual());
                vh.tvStartDate.setText(vh.calculateDate(stat.getStartManual()));
                vh.tvEndDate.setText(vh.calculateDate(stat.getEndManual()));
                vh.tvTime.setText(vh.calculateTime(stat.getDurationManual()));

                changeStat.setState(StatisticsTask.SET_MANUAL);
                realm.beginTransaction();
                stat.setState(StatisticsTask.SET_MANUAL);
                realm.commitTransaction();
            }
        });

        vh.tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(vh, "start", stat, changeStat);
            }
        });

        vh.tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(vh, "end", stat, changeStat);
            }
        });

        vh.tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog(vh, stat, changeStat);
            }
        });

        vh.etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                vh.tvNote.setText("Note (" + (200 - vh.etDescription.length()) + "):");
            }

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c)
            { }
            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a)
            { }
        });

        vh.etDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                    updateDescriptionTask(stat, changeStat, vh.etDescription.getText().toString());
            }
        });

        vh.etDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE)
                    updateDescriptionTask(stat, changeStat, vh.etDescription.getText().toString());
                return false;
            }
        });

        vh.etDescription.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK)
                    updateDescriptionTask(stat, changeStat, vh.etDescription.getText().toString());
                return false;
            }
        });
    }

    private void updateDescriptionTask(StatisticsTask statistics,
                                       ChangeStat changeStat, String note)
    {
        if (note.equals(statistics.getDescription()))
            return;
        else
        {
            changeStat.setDescription(note);
            realm.beginTransaction();
            statistics.setDescription(note);
            realm.commitTransaction();
        }
    }

    private void showDateDialog(final TaskStatViewHolder vh, final String time,
                                final StatisticsTask stat, final ChangeStat changeStat) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_date_time, null, false);
        builder.setView(view);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        Button bDate = (Button) view.findViewById(R.id.bDate);
        Button bTime = (Button) view.findViewById(R.id.bTime);

        bDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.setVisibility(View.INVISIBLE);
                datePicker.setVisibility(View.VISIBLE);
            }
        });

        bTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.setVisibility(View.VISIBLE);
                datePicker.setVisibility(View.INVISIBLE);
            }
        });

        Calendar calendar = Calendar.getInstance();
        if (time.equals("start"))
            calendar.setTimeInMillis(stat.getStartManual());
        else
            calendar.setTimeInMillis(stat.getEndManual());

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);

        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

        builder
                .setTitle("Set the " + time + " Date")
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth();
                        int year = datePicker.getYear();
                        int hour = timePicker.getCurrentHour();
                        int min = timePicker.getCurrentMinute();
                        String date = String.format("%02d.%02d.%02d %02d:%02d", day, (month + 1), year, hour, min);

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, hour, min);

                        long dateLong = calendar.getTimeInMillis();
                        if (time.equals("start")) {
                            vh.tvStartDate.setText(date);
                            vh.start = dateLong;

                            changeStat.setStartManual(dateLong);
                            realm.beginTransaction();
                            stat.setStartManual(dateLong);
                            realm.commitTransaction();
                        }
                        else {
                            vh.tvEndDate.setText(date);
                            vh.end = calendar.getTimeInMillis();

                            changeStat.setEndManual(dateLong);
                            realm.beginTransaction();
                            stat.setEndManual(dateLong);
                            realm.commitTransaction();
                        }

                        long duration = vh.end - vh.start;
                        realm.beginTransaction();
                        if (duration > 0) {
                            changeStat.setDurationManual(vh.end - vh.start);
                            stat.setDurationManual(vh.end - vh.start);
                        }
                        else {
                            changeStat.setDurationManual(0);
                            stat.setDurationManual(0);
                        }
                        realm.commitTransaction();
                        vh.calculateTime();
                    }
                });
        builder.create();
        builder.show();
    }

    private void showTimeDialog(final TaskStatViewHolder vh,
                                final StatisticsTask stat, final ChangeStat changeStat) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_time_setting, null, false);
        final NumberPicker numHour = (NumberPicker)view.findViewById(R.id.numHour);
        final NumberPicker numMin = (NumberPicker)view.findViewById(R.id.numMin);
        final NumberPicker numSec = (NumberPicker)view.findViewById(R.id.numSec);
        //Если слишком много часов не удобно листать. Максимально 100 пунктов.
        if (vh.hourTask / 100 > 0)
        {
            String[] values = new String[101];
            int step = vh.hourTask / 100;
            for (int i = 0; i < values.length; i++) {
                values[i] = Integer.toString(step * i);
            }
            numHour.setMaxValue(100);
            numHour.setDisplayedValues(values);
        }
        else
        {
            String[] values = new String[vh.hourTask + 1];
            for (int i = 0; i < values.length; i++) {
                values[i] = Integer.toString(i);
            }
            numHour.setMaxValue(vh.hourTask);
            numHour.setDisplayedValues(values);
        }
        if (vh.hourTask > 0)
            numMin.setMaxValue(59);
        else
            numMin.setMaxValue(vh.minTask);
        numSec.setMaxValue(59);
        builder.setView(view);
        builder.setTitle("Set time")
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int h = Integer.valueOf(numHour.getDisplayedValues()[numHour.getValue()]);
                        int m = numMin.getValue();
                        int s = numSec.getValue();
                        String time = String.format("%02d:%02d:%02d", h, m, s);
                        vh.tvTime.setText(time);

                        long dateLong = (long)(h * 3600 + m * 60 + s) * 1000;
                        Log.d("myLog", "DateLong = " + dateLong + "; h = " + (h * 3600) + m + "(m * 60)");

                        changeStat.setDurationManual(dateLong);
                        realm.beginTransaction();
                        stat.setDurationManual(dateLong);
                        realm.commitTransaction();

                    }
                });
        builder.create();
        builder.show();
    }


    @Override
    public int getItemCount() {
        return stats.size();
    }


    public void Save()
    {

        //Description!!!!!!!! Переписать, потому что не фиксируется

        for (int i = 0; i < changeStats.size(); i++)
        {
            ChangeStat changeStat = changeStats.get(i);
            Log.d("MyChanges", "---------------");
            Log.d("MyChanges", "ChangeStat = " + i);
            Log.d("MyChanges", "ChangeStat.isChanged = " + changeStat.isChanged);
            Log.d("MyChanges", "ChangeStat.getState() = " + changeStat.getState());
            Log.d("MyChanges", "ChangeStat.getStartManual() = " + changeStat.getStartManual());
            Log.d("MyChanges", "ChangeStat.getEndManual() = " + changeStat.getEndManual());
            Log.d("MyChanges", "ChangeStat.getDurationManual() = " + changeStat.getDurationManual());
            Log.d("MyChanges", "ChangeStat.getDescription() = " + changeStat.getDescription());
        }


/*        for (int i = 0; i < changeStats.size(); i++)
        {
            ChangeStat change = changeStats.get(i);
            if (!change.isChanged)
                continue;
            StatisticsTask stat = realm.where(StatisticsTask.class).equalTo("id", change.getId()).findFirst();
            realm.beginTransaction();
            if (change.getState() != -1)
                stat.setState(change.getState());
            if (change.getDescription() != null)
                stat.setDescription(change.getDescription());
            if (change.getStartManual() != -1)
                stat.setStartManual(change.getStartManual());
            if (change.getEndManual() != -1)
                stat.setEndManual(change.getEndManual());
            if (change.getEndManual() != -1)
                stat.setDurationManual(change.getDurationManual());
            realm.beginTransaction();
        }*/
    }
}
