package com.ponomarevigor.androidgames.mytimetracker.Tests.TaskTest1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ponomarevigor.androidgames.mytimetracker.Database.Project;
import com.ponomarevigor.androidgames.mytimetracker.R;

import java.util.List;

/**
 * Created by Igorek on 31.10.2017.
 */

public class AdapterDialog extends ArrayAdapter {
    private Context context;
    private List<Project> projects;
    private int resource;

    public AdapterDialog(Context context, int recourse,
                         List<Project> projects) {
        super(context, recourse, projects);
        this.context = context;
        this.resource = resource;
        this.projects = projects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {
        View row = LayoutInflater.from(context).inflate(R.layout.dialog_project_row, parent, false);
        Project project = projects.get(position);
        TextView name = (TextView) row.findViewById(R.id.tvNamePerson);
        name.setText(project.getName());
        TextView user = (TextView) row.findViewById(R.id.tvEmail);
        user.setText(project.getUserHost());
        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}