package com.ponomarevigor.androidgames.mytimetracker.ProjectInfo.Pager;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ponomarevigor.androidgames.mytimetracker.Database.Project;
import com.ponomarevigor.androidgames.mytimetracker.Database.Task;
import com.ponomarevigor.androidgames.mytimetracker.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Igorek on 01.11.2017.
 */

public class PageMain extends Fragment {
    TextView etName, etDescription, etWorkspace;
    TextView tvNumber;
    TextView tvStart, tvEnd;
    Realm realm;
    Project project;
    RealmResults<Task> tasks;

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
        View view = inflater.inflate(R.layout.project_page_main, container, false);

        Realm.init(this.getContext());
        realm = Realm.getDefaultInstance();
        int idProject = getActivity().getIntent().getIntExtra("projectID", 0);
        project = realm.where(Project.class).equalTo("id", idProject).findFirst();
        tasks = realm.where(Task.class).equalTo("project.id", project.getId())
                .findAllSorted("timeCreated", Sort.DESCENDING);

        etName = (TextView) view.findViewById(R.id.etName);
        etWorkspace = (TextView) view.findViewById(R.id.etWorkspace);
        etDescription = (TextView) view.findViewById(R.id.etDescription);
        tvNumber = (TextView)view.findViewById(R.id.tvNumber);
        tvStart = (TextView)view.findViewById(R.id.tvStart);
        tvEnd = (TextView)view.findViewById(R.id.tvEnd);

        etName.setText(project.getName());
        etDescription.setText(project.getDescription());
        etWorkspace.setText(project.getWorkspace().getName());
        tvNumber.setText(Integer.toString(realm.where(Task.class).equalTo("project.id", idProject).findAll().size()));

        String startDate = new SimpleDateFormat("dd MMMM yyyy").format(new Date(project.getStart()));
        tvStart.setText(startDate);

        if (tasks.size() != 0) {
            long date = Math.max(
                    Math.max(tasks.where().max("timeCreated").longValue(), tasks.where().max("timeStart").longValue()),
                    Math.max(tasks.where().max("timePause").longValue(), tasks.where().max("timeFinish").longValue())
            );
            String endDate = new SimpleDateFormat("dd MMMM yyyy").format(new Date(date));
            tvEnd.setText(endDate);
        }
        else
            tvEnd.setText(startDate);
        return view;
    }
}