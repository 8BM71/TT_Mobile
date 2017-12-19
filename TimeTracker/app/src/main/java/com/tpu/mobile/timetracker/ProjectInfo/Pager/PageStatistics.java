package com.tpu.mobile.timetracker.ProjectInfo.Pager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tpu.mobile.timetracker.Database.Project;
import com.tpu.mobile.timetracker.Database.StatisticsTask;
import com.tpu.mobile.timetracker.Database.Task;
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

        Realm.init(this.getContext());
        realm = Realm.getDefaultInstance();

        int idProject = getActivity().getIntent().getIntExtra("projectID", 0);
        project = realm.where(Project.class).equalTo("id", idProject).findFirst();
        tasks = realm.where(Task.class).equalTo("project.id", project.getId())
                .findAllSorted("timeCreated", Sort.DESCENDING);

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