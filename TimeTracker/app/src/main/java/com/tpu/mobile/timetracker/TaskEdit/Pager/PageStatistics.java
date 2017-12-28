package com.tpu.mobile.timetracker.TaskEdit.Pager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apollographql.apollo.ApolloClient;
import com.tpu.mobile.timetracker.Database.Controller.ProjectController;
import com.tpu.mobile.timetracker.Database.Controller.TaskController;
import com.tpu.mobile.timetracker.Database.Model.StatisticsTask;
import com.tpu.mobile.timetracker.Database.Model.Task;
import com.tpu.mobile.timetracker.MainApplication;
import com.tpu.mobile.timetracker.R;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Igorek on 01.11.2017.
 */

public class PageStatistics extends Fragment  {
    ApolloClient client;
    Realm realm;
    ProjectController projectController;
    TaskController taskController;
    RecyclerView recyclerView;
    RecyclerAdapterStatistics adapter;
    String idTask;

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
        View view = inflater.inflate(R.layout.task_page_stat, container, false);
        client = ((MainApplication)getActivity().getApplication()).getApolloClient();
        realm = ((MainApplication)getActivity().getApplication()).getRealm();
        projectController = new ProjectController(realm);
        taskController = new TaskController(realm);
        idTask = getActivity().getIntent().getStringExtra("taskID");
        Task task = taskController.getTask(idTask);
        if (task.getState() != Task.TASK_CREATED) {
            RealmResults<StatisticsTask> stats = realm.where(StatisticsTask.class).equalTo("task.id", task.getId()).findAllSorted("create");
            List<StatisticsTask> results = realm.copyFromRealm(stats);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(),
                    LinearLayoutManager.VERTICAL, false));
            if (results != null)
                recyclerView.setItemViewCacheSize(results.size());
            adapter = new RecyclerAdapterStatistics(this.getContext(), client, realm, results);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    //Не используется
    public void refresh()
    {
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        List<StatisticsTask> results = task.getStatistics();
        adapter = new RecyclerAdapterStatistics(this.getContext(), client, realm, results);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void Save()
    {
        adapter.Save();
    }
}