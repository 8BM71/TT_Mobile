package com.tpu.mobile.timetracker.TaskEdit.Pager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tpu.mobile.timetracker.Database.Model.StatisticsTask;
import com.tpu.mobile.timetracker.Database.Model.Task;
import com.tpu.mobile.timetracker.R;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Igorek on 01.11.2017.
 */

public class PageStatistics extends Fragment  {
    RecyclerView recyclerView;
    RecyclerAdapterStatistics adapter;
    Realm realm;
    int idTask;

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

        Realm.init(this.getContext());
        realm = Realm.getDefaultInstance();
        idTask = getActivity().getIntent().getIntExtra("taskID", 0);
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        List<StatisticsTask> results = task.getStatistics();
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.VERTICAL, false));
        adapter = new RecyclerAdapterStatistics(this.getContext(), results, realm);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void refresh()
    {
        Task task = realm.where(Task.class).equalTo("id", idTask).findFirst();
        List<StatisticsTask> results = task.getStatistics();
        adapter = new RecyclerAdapterStatistics(this.getContext(), results, realm);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void Save()
    {
        adapter.Save();
    }
}