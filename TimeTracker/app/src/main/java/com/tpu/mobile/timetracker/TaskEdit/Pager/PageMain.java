package com.tpu.mobile.timetracker.TaskEdit.Pager;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


import com.tpu.mobile.timetracker.Database.Model.Project;
import com.tpu.mobile.timetracker.Database.Model.StatisticsTask;
import com.tpu.mobile.timetracker.Database.Model.Task;
import com.tpu.mobile.timetracker.R;


import java.util.List;

import io.realm.Realm;

/**
 * Created by Igorek on 01.11.2017.
 */

public class PageMain extends Fragment {

    EditText etName, etDescription, etProject;
    Chronometer chronometer;
    ImageButton ibActive;
    ImageView imageIndicator;
    List<Project> projects;
    long step = 0;

    Realm realm;
    Task task;
    Project project;

    public PageMain() {
    }

    public static PageMain newInstance() {
        PageMain fragment = new PageMain();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_page_info, container, false);
        Realm.init(this.getContext());
        realm = Realm.getDefaultInstance();

        int idTask = getActivity().getIntent().getIntExtra("taskID", 0);
        task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        project = task.getProject();

        etName = (EditText) view.findViewById(R.id.etName);
        etProject = (EditText) view.findViewById(R.id.etProject);
        etDescription = (EditText) view.findViewById(R.id.etDescription);
        chronometer = (Chronometer) view.findViewById(R.id.tvTimeTask);
        ibActive = (ImageButton) view.findViewById(R.id.ibActive);
        imageIndicator = (ImageView) view.findViewById(R.id.imageIndicator);

        etProject.setKeyListener(null);
        etProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProjectsDialog();
            }
        });

        projects = realm.where(Project.class).findAll();

        etName.setText(task.getName());
        etDescription.setText(task.getDescription());
        etProject.setText(project.getName());

        if (task.getState() == Task.TASK_CREATED)
            create();

        if (task.getState() == Task.TASK_RUNNING) {
            setStep(task.getTimeStart());
            start();
        }

        if (task.getState() == Task.TASK_STOPPED) {
            setStep(task.getDuration());
            chronometer.setBase(SystemClock.elapsedRealtime() - getStep());
            stop();
        }

        View.OnClickListener onClickListener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task.getState() == Task.TASK_CREATED ||
                        task.getState() == Task.TASK_STOPPED) {
                    realm.beginTransaction();
                    task.setTimeCreated(System.currentTimeMillis());
                    task.setTimeStart(System.currentTimeMillis());
                    task.setState(Task.TASK_RUNNING);
                    task.setDuration(0);
                    realm.commitTransaction();
                    start();
                    return;
                }

                if (task.getState() == Task.TASK_RUNNING) {
                    realm.beginTransaction();
                    task.setDuration(getCurrentStep());
                    task.setState(Task.TASK_STOPPED);
                    task.setTimeFinish(System.currentTimeMillis());

                    StatisticsTask stat = realm.createObject(StatisticsTask.class);
                    stat.setId(realm.where(StatisticsTask.class).max("id").intValue() + 1);
                    stat.setStart(task.getTimeCreated());
                    stat.setEnd(task.getTimeFinish());
                    stat.setDuration(task.getDuration());
                    task.getStatistics().add(stat);
                    realm.commitTransaction();
                    stop();

                    PageStatistics pageStatistics = (PageStatistics) getFragmentManager().getFragments().get(1);
                    pageStatistics.refresh();
                    return;
                }
            }
        };
        ibActive.setOnClickListener(onClickListener1);
        return view;
    }

    private void showProjectsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Select a project").setIcon(R.drawable.project);
        AdapterDialog adapter = new AdapterDialog(this.getContext(), R.layout.dialog_project_row, projects);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (project != projects.get(item)) {
                    etProject.setText(projects.get(item).getName());
                    project = projects.get(item);
                }
            }
        });
        builder.setCancelable(true);
        builder.create();
        builder.show();
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

    public void create() {
        ibActive.setImageResource(R.drawable.ic_play);
        ibActive.setEnabled(true);
        step = 0;
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        chronometer.setTextColor(Color.DKGRAY);
    }

    public void start() {
        ibActive.setImageResource(R.drawable.ic_stop);
        chronometer.setBase(SystemClock.elapsedRealtime() - step);
        chronometer.start();
        chronometer.setTextColor(Color.parseColor("#1abc9c"));
        imageIndicator.setImageResource(R.drawable.cgreen);
    }

    public void stop() {
        ibActive.setImageResource(R.drawable.ic_play);
        chronometer.stop();
        step = 0;
        chronometer.setTextColor(Color.RED);
        imageIndicator.setImageResource(R.drawable.cred);
    }

    public void Save() {
        realm.beginTransaction();
        task.setName(etName.getText().toString());
        task.setDescription(etDescription.getText().toString());
        task.setProject(realm.where(Project.class).equalTo("id", project.getId()).findFirst());
        realm.commitTransaction();
    }
}