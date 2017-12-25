package com.tpu.mobile.timetracker.Main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tpu.mobile.timetracker.Database.Model.Project;
import com.tpu.mobile.timetracker.Database.Model.Task;
import com.tpu.mobile.timetracker.Database.Model.Workspace;
import com.tpu.mobile.timetracker.Project.ProjectCreateActivity;
import com.tpu.mobile.timetracker.Project.ProjectEditActivity;
import com.tpu.mobile.timetracker.R;
import com.tpu.mobile.timetracker.Task.AdapterDialog;
import com.tpu.mobile.timetracker.Task.ItemTaskTouchHelper.ItemTaskTouchHelper;
import com.tpu.mobile.timetracker.Task.ModelTask;
import com.tpu.mobile.timetracker.Task.TaskRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class TaskFragment extends Fragment {

    Realm realm;
    RealmResults<Task> tasks;
    RealmResults<Project> projects;
    RecyclerView recyclerView;
    TaskRecyclerViewAdapter taskAdapter;
    ItemTouchHelper touchHelper;
    TextView tvProject;
    Project project;
    List<ModelTask> models;

    String[] projectsName;
    int[] projectsColor;
    int pos = 0;
    int idProject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.xxx_task_app_bar_main, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTask();
            }
        });
        Realm.init(getContext());
        realm = Realm.getDefaultInstance();
        projects = realm.where(Project.class).findAll().sort("id");
        if (projects.size() == 0) initProject();
        idProject = getActivity().getIntent().getIntExtra("projectID", -1);
        if (idProject == -1)
            tasks = realm.where(Task.class).findAllSorted("timeCreated", Sort.DESCENDING);
        else {
            project = realm.where(Project.class).equalTo("id", idProject).findFirst();
            tasks = realm.where(Task.class).equalTo("project.id", project.getId())
                    .findAllSorted("timeCreated", Sort.DESCENDING);
        }

        if (tasks.size() != 0)
            models = setData(tasks);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        taskAdapter = new TaskRecyclerViewAdapter(getContext(), models, tasks, project, realm);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false); //Если используется совместно с ScrollView
        recyclerView.setAdapter(taskAdapter);
        ItemTouchHelper.Callback callback = new ItemTaskTouchHelper(taskAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        if (models != null)
            recyclerView.setItemViewCacheSize(models.size());
        createProjects(projects);

        tvProject = (TextView) view.findViewById(R.id.tvProject);
        if (idProject == -1)
            tvProject.setText(projectsName[0]);
        else
            tvProject.setText(project.getName());
        tvProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProjectDialog();
            }
        });

        return view;
    }

    private void createProjects(RealmResults<Project> projects)
    {
        projectsName = new String[projects.size() + 1];
        projectsName[0] = "All tasks";
        projectsColor = new int[projects.size() + 1];
        projectsColor[0] = Color.WHITE;

        for (int i = 0; i < projects.size(); i++)
        {
            projectsName[i + 1] = projects.get(i).getName();
            projectsColor[i + 1] = projects.get(i).getColor();
        }
    }

    private void initProject()
    {
        Workspace workspace = new Workspace();
        workspace.setName("No workspace");
        workspace.setDescription("No description");
        workspace.setId(1);
        realm.beginTransaction();
        realm.copyToRealm(workspace);
        realm.commitTransaction();

        Project project = new Project();
        project.setName("No project");
        project.setDescription("No description");
        project.setId(1);
        project.setColor(Color.LTGRAY);
        project.setWorkspace(realm.where(Workspace.class).findFirst());
        project.setStart(System.currentTimeMillis());
        realm.beginTransaction();
        realm.copyToRealm(project);
        realm.commitTransaction();
    }

    private void showProjectDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select a project").setIcon(R.drawable.project);
        AdapterDialog adapter = new AdapterDialog(getContext(), R.layout.dialog_project_row, projectsName, projectsColor);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                loadTasks(item);
            }
        });
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }

    private void loadTasks(int pos)
    {
        this.pos = pos;
        tvProject.setText(projectsName[pos]);
        if (pos == 0) {
            tasks = realm.where(Task.class).findAllSorted("timeCreated", Sort.DESCENDING);
            project = null;
            idProject = -1;
        }
        else {
            project = projects.get(pos - 1);
            idProject = project.getId();
            tasks = realm.where(Task.class).equalTo("project.id", project.getId())
                    .findAllSorted("timeCreated", Sort.DESCENDING);
        }

        if (models != null)
            models.clear();
        models = setData(tasks);
        taskAdapter.setTasks(tasks);
        taskAdapter.setProject(project);
        taskAdapter.setModels(models);
        recyclerView.setItemViewCacheSize(tasks.size());
        taskAdapter.notifyDataSetChanged();
    }

    private void createTask()
    {
        final View v = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_task_create, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("  Create new task!")
                .setIcon(R.drawable.task)
                .setCancelable(true)
                .setNegativeButton("Add task",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                TextView tvName = (TextView) v.findViewById(R.id.tvNameTask);
                                TextView tvDesc = (TextView) v.findViewById(R.id.tvDescriptionTask);
                                String name = tvName.getText().toString();
                                String description = tvDesc.getText().toString();

                                if (name.isEmpty() && project == null)
                                    name = "Task: " + realm.where(Project.class).equalTo("id", 1).findFirst().getName();
                                if (name.isEmpty())
                                    name = "Task: " + project.getName();

                                realm.beginTransaction();
                                Task taskModel = realm.createObject(Task.class);
                                taskModel.setName(name);
                                taskModel.setDescription(description);
                                taskModel.setTimeCreated(System.currentTimeMillis());
                                taskModel.setState(Task.TASK_CREATED);
                                taskModel.setDuration(0);
                                taskModel.setId(realm.where(Task.class).max("id").intValue() + 1);
                                if (project == null)
                                    taskModel.setProject(realm.where(Project.class).equalTo("id", 1).findFirst());
                                else
                                    taskModel.setProject(realm.where(Project.class).equalTo("id", project.getId()).findFirst());
                                realm.commitTransaction();

                                if (models != null)
                                    models.clear();
                                models = setData(tasks);
                                recyclerView.setItemViewCacheSize(models.size());
                                taskAdapter.setModels(models);
                                taskAdapter.notifyDataSetChanged();
                            }
                        });
        builder.setView(v);
        AlertDialog alert = builder.create();
        alert.show();
    }





    public static List<ModelTask> setData(List<Task> tasks)
    {
        long day = 3600000; //Пока час для тестирования
        List<ModelTask> models = new ArrayList<ModelTask>();
        Task task;
        if (tasks.size() != 0)
            task = tasks.get(0);
        else
            return null;

        long time = task.getTimeCreated() / day;
        models.add(new ModelTask(time));
        //models.add(new ModelTask(task));

        for (Task t : tasks)
        {
            long d = t.getTimeCreated() / day;
            if (d != time)
            {
                time = d;
                models.add(new ModelTask(time));
                models.add(new ModelTask(t));
            }
            else
                models.add(new ModelTask(t));
        }
        return models;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.actionCreate) {
            Intent intent = new Intent(getActivity(), ProjectCreateActivity.class);
            startActivity(intent);
            return true;
        }

        if (idProject == -1 || idProject == 1)
        {
            Toast.makeText(getContext(), "Select a project", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (id == R.id.actionEdit) {
            Intent intent = new Intent(getActivity(), ProjectEditActivity.class);
            intent.putExtra("projectID", idProject);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        createProjects(realm.where(Project.class).findAll().sort("id"));
        if (pos != 0)
            tvProject.setText(projectsName[pos]);
        taskAdapter.notifyDataSetChanged();
        super.onResume();
    }
}
