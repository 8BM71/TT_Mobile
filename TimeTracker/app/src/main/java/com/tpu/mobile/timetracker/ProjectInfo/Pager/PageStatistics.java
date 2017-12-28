package com.tpu.mobile.timetracker.ProjectInfo.Pager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tpu.mobile.timetracker.Database.Controller.ProjectController;
import com.tpu.mobile.timetracker.Database.Controller.TaskController;
import com.tpu.mobile.timetracker.Database.Model.Project;
import com.tpu.mobile.timetracker.Database.Model.StatisticsTask;
import com.tpu.mobile.timetracker.Database.Model.Task;
import com.tpu.mobile.timetracker.MainApplication;
import com.tpu.mobile.timetracker.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Igorek on 01.11.2017.
 */

public class PageStatistics extends Fragment  {
    RecyclerView recyclerView;
    RecyclerAdapterStatistics adapter;
    Realm realm;
    ProjectController projectController;
    TaskController taskController;
    Project project;
    RealmResults<Task> tasks;
    RealmList<StatisticsTask> stats;
    List<ModelTaskStat> models;

    public PageStatistics() {
    }


    public static PageStatistics newInstance() {
        PageStatistics fragment = new PageStatistics();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.project_page_stat, container, false);

        realm = ((MainApplication)getActivity().getApplication()).getRealm();
        projectController = new ProjectController(realm);
        taskController = new TaskController(realm);
        String idProject = getActivity().getIntent().getStringExtra("projectID");
        project = projectController.getProject(idProject);
        tasks = taskController.getTasksOfProject(idProject);

        models = new ArrayList<ModelTaskStat>();
        for (Task task : tasks)
        {
            List<StatisticsTask> stats = task.getStatistics();
            models.add(new ModelTaskStat(task));

            for (StatisticsTask stat: stats)
                models.add(new ModelTaskStat(stat));
        }

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.VERTICAL, false));
        adapter = new RecyclerAdapterStatistics(this.getContext(), tasks, stats, models, realm);
        recyclerView.setAdapter(adapter);
        return view;
    }
}