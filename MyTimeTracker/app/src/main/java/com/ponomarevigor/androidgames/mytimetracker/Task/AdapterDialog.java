package com.ponomarevigor.androidgames.mytimetracker.Task;

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
    private String[] projectsName;
    int[] projectsColor;
    private int resource;

    public AdapterDialog(Context context, int recourse,
                         String[] projectsName, int[] projectsColor) {
        super(context, recourse, projectsName);
        this.context = context;
        this.resource = resource;
        this.projectsName = projectsName;
        this.projectsColor = projectsColor;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {
        View row = LayoutInflater.from(context).inflate(R.layout.dialog_project_row, parent, false);
        TextView name = (TextView) row.findViewById(R.id.tvNamePerson);
        TextView color = (TextView) row.findViewById(R.id.tvColor);

        name.setText(projectsName[position]);
        color.setBackgroundColor(projectsColor[position]);
        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}