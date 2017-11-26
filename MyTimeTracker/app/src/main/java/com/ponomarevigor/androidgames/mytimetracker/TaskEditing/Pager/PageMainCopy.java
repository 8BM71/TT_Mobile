package com.ponomarevigor.androidgames.mytimetracker.TaskEditing.Pager;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ponomarevigor.androidgames.mytimetracker.Database.Project;
import com.ponomarevigor.androidgames.mytimetracker.Database.StatisticsTask;
import com.ponomarevigor.androidgames.mytimetracker.Database.Task;
import com.ponomarevigor.androidgames.mytimetracker.R;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Igorek on 01.11.2017.
 */

public class PageMainCopy extends Fragment {

    EditText etName, etDescription, etProject;
    Chronometer chronometer;
    ImageButton ibActive, ibFinish;
    ImageView imageIndicator;
    List<Project> projects;
    long step = 0;

    Realm realm;
    Task task;
    Project project;

    int positionTask;

    public PageMainCopy(int positionTask) {
        this.positionTask = positionTask;
    }

    public static PageMainCopy newInstance(int positionTask) {
        PageMainCopy fragment = new PageMainCopy(positionTask);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_page_main, container, false);

        Realm.init(this.getContext());
        realm = Realm.getDefaultInstance();
        //task = realm.where(Task.class).equalTo("position", positionTask).findFirst();
        task = realm.where(Task.class).equalTo("id", positionTask).findFirst();
        int idProject = getActivity().getIntent().getIntExtra("projectID", 0);
        project = task.getProject();

        etName = (EditText) view.findViewById(R.id.etName);
        etProject = (EditText) view.findViewById(R.id.etProject);
        etDescription = (EditText) view.findViewById(R.id.etDescription);
        chronometer = (Chronometer) view.findViewById(R.id.tvTimeTask);
        ibActive = (ImageButton) view.findViewById(R.id.ibActive);
        ibFinish = (ImageButton) view.findViewById(R.id.ibFinish);
        imageIndicator = (ImageView) view.findViewById(R.id.imageIndicator);
        ibFinish.setEnabled(false);

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


        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) updateNameTask();

            }
        });

        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE) {
                    updateNameTask();
                }
                return false;
            }
        });

        etName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    updateNameTask();
                }
                return false;
            }
        });

        etDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) updateDescriptionTask();

            }
        });

        etDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE) {
                    updateDescriptionTask();
                }
                return false;
            }
        });

        etDescription.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    updateDescriptionTask();
                }
                return false;
            }
        });


        if (task.getState() == Task.TASK_CREATED) {
            create();
        }

        if (task.getState() == Task.TASK_RUNNING) {
            setStep(task.getTimeStart());
            start();
        }

        if (task.getState() == Task.TASK_PAUSED) {
            setStep(task.getTimePause());
            chronometer.setBase(SystemClock.elapsedRealtime() - getStep());
            pause();
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
                    task.setTimeCreated(System.currentTimeMillis()); ////////////!!!!!!!!!
                    task.setTimeStart(System.currentTimeMillis());
                    task.setState(Task.TASK_RUNNING);
                    task.setDuration(0);
                    realm.commitTransaction();
                    start();
                    return;
                }

                if (task.getState() == Task.TASK_PAUSED) {
                    realm.beginTransaction();
                    task.setTimeStart(System.currentTimeMillis());
                    task.setState(Task.TASK_RUNNING);
                    task.setDuration(getStep());
                    realm.commitTransaction();
                    start();
                    return;
                }

                if (task.getState() == Task.TASK_RUNNING) {
                    realm.beginTransaction();
                    task.setState(Task.TASK_PAUSED);
                    task.setTimePause(getCurrentStep());
                    realm.commitTransaction();
                    pause();
                    return;
                }

            }
        };
        ibActive.setOnClickListener(onClickListener1);

        View.OnClickListener onClickListener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("myLog12", "task: " + task.getName() + "size = " + task.getStatistics().size());

                realm.beginTransaction();
                if (task.getState() == Task.TASK_PAUSED)
                    //task.setTimeFinish(getStep());
                    task.setDuration(getStep());
                else
                    //task.setTimeFinish(getCurrentStep());
                    task.setDuration(getCurrentStep());
                task.setState(Task.TASK_STOPPED);
                task.setTimeFinish(System.currentTimeMillis());

                StatisticsTask stat = new StatisticsTask();
                stat.setState(StatisticsTask.SET_AUTO);
                stat.setStartManual(task.getTimeCreated());
                stat.setStartAuto(task.getTimeCreated());
                stat.setEndManual(task.getTimeFinish());
                stat.setEndAuto(task.getTimeFinish());
                stat.setDurationManual(task.getDuration());
                stat.setDurationAuto(task.getDuration());
                task.getStatistics().add(stat);
                realm.commitTransaction();
                stop();

                PageStatistics pageStatistics = (PageStatistics) getFragmentManager().getFragments().get(1);
                pageStatistics.refreshData();
            }
        };
        ibFinish.setOnClickListener(onClickListener2);

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
                    realm.beginTransaction();
                    Task delTask = realm.copyFromRealm(task);
                    projects.get(item).getTasks().add(delTask);
                    //task.setId(-1);
                    //realm.where(Task.class).equalTo("id", -1).findFirst().deleteFromRealm();
                    task.deleteFromRealm();
                    task = realm.where(Task.class).equalTo("id", positionTask).findFirst();
                    project = projects.get(item);
                    realm.commitTransaction();
                    Log.d("myLog12", "task: " + task.getName() + "size = " + task.getStatistics().size());
                    PageStatistics pageStatistics = (PageStatistics) getFragmentManager().getFragments().get(1);
                    pageStatistics.refresh();
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
        ibActive.setImageResource(R.drawable.start);
        ibActive.setEnabled(true);
        ibFinish.setEnabled(false);
        step = 0;
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        chronometer.setTextColor(Color.DKGRAY);
    }

    public void start() {
        ibActive.setImageResource(R.drawable.ic_pause);
        chronometer.setBase(SystemClock.elapsedRealtime() - step);
        chronometer.start();
        chronometer.setTextColor(Color.parseColor("#1abc9c"));
        imageIndicator.setImageResource(R.drawable.cgreen);
        ibFinish.setEnabled(true);
    }

    public void pause() {
        ibActive.setImageResource(R.drawable.ic_play);
        chronometer.stop();
        step = getCurrentStep();
        chronometer.setTextColor(Color.GRAY);
        imageIndicator.setImageResource(R.drawable.cgray);
        ibFinish.setEnabled(true);
    }

    public void stop() {
        ibActive.setImageResource(R.drawable.ic_play);
        chronometer.stop();
        step = 0;
        chronometer.setTextColor(Color.RED);
        imageIndicator.setImageResource(R.drawable.cred);
        ibFinish.setEnabled(false);
    }

    private void updateNameTask() {
        if (task.getName().equals(etName.getText().toString()))
            return;
        else {
            realm.beginTransaction();
            task.setName(etName.getText().toString());
            realm.commitTransaction();
        }
    }

    private void updateDescriptionTask() {
        if (task.getDescription().equals(etDescription.getText().toString()))
            return;
        else {
            realm.beginTransaction();
            task.setDescription(etDescription.getText().toString());
            realm.commitTransaction();
        }
    }
}