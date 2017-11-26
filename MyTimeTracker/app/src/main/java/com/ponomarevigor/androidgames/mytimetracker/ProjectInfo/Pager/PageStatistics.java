package com.ponomarevigor.androidgames.mytimetracker.ProjectInfo.Pager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ponomarevigor.androidgames.mytimetracker.Database.Project;
import com.ponomarevigor.androidgames.mytimetracker.Database.StatisticsTask;
import com.ponomarevigor.androidgames.mytimetracker.Database.Task;
import com.ponomarevigor.androidgames.mytimetracker.R;

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
        View view = inflater.inflate(R.layout.task_page_statistics, container, false);

        Realm.init(this.getContext());
        realm = Realm.getDefaultInstance();

        int idProject = getActivity().getIntent().getIntExtra("projectID", 0);
        project = realm.where(Project.class).equalTo("id", idProject).findFirst();
        tasks = realm.where(Task.class).equalTo("project.id", project.getId())
                .findAllSorted("timeCreated", Sort.DESCENDING);

        stats = new RealmList<StatisticsTask>();
        models = new ArrayList<ModelTaskStat>();
        for (int i = 0; i < tasks.size(); i++)
        {
            stats.addAll(tasks.get(i).getStatistics());
            List<StatisticsTask> taskStats = tasks.get(i).getStatistics();

            ModelTaskStat m1 = new ModelTaskStat();
            m1.setTask(tasks.get(i));
            models.add(m1);

            for (int j = 0; j < taskStats.size(); j++)
            {
                ModelTaskStat m2 = new ModelTaskStat();
                m2.setStat(taskStats.get(j));
                models.add(m2);
            }
        }

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.VERTICAL, false));
        adapter = new RecyclerAdapterStatistics(this.getContext(), tasks, stats, models, realm);
        recyclerView.setAdapter(adapter);
        return view;
    }
}