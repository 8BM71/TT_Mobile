package com.ponomarevigor.androidgames.mytimetracker.TaskEditing;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ponomarevigor.androidgames.mytimetracker.R;
import com.ponomarevigor.androidgames.mytimetracker.TaskEditing.Pager.PageAdapter;
import com.ponomarevigor.androidgames.mytimetracker.TaskEditing.Pager.PageMain;
import com.ponomarevigor.androidgames.mytimetracker.TaskEditing.Pager.PageStatistics;

import io.realm.Realm;

/**
 * Created by Igorek on 06.11.2017.
 */

public class TaskEditingActivity extends AppCompatActivity {
    public ViewPager viewPager;
    public PageAdapter pagerAdapter;
    public FragmentManager fragmentManager;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_editing);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        //Task task = (Serializable)getIntent().getSerializableExtra("task");
        //int position = getIntent().getIntExtra("task", 0);
        //int position = 0;
        int position = getIntent().getIntExtra("taskID", 0);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        fragmentManager = getSupportFragmentManager();
        viewPager = (ViewPager)findViewById(R.id.container);
        tabLayout.setupWithViewPager(viewPager);
        pagerAdapter = new PageAdapter(this.getSupportFragmentManager(), position);
        viewPager.setAdapter(pagerAdapter);

        TextView tvBack = (TextView)findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(TaskEditingActivity.this, com.ponomarevigor.androidgames.mytimetracker.Task.TaskActivity.class);;
                //startActivity(intent);
                onBackPressed();
            }
        });

        Button bSave = (Button) findViewById(R.id.tvSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //!!!!!!!!!!!!!!!//
                PageMain pageMain = (PageMain) fragmentManager.getFragments().get(0);
                pageMain.Save();

                PageStatistics pageStat = (PageStatistics) fragmentManager.getFragments().get(1);
                pageStat.Save();


                //Intent intent = new Intent(TaskEditingActivity.this, com.ponomarevigor.androidgames.mytimetracker.Task.TaskActivity.class);;
                //startActivity(intent);
                onBackPressed();
            }
        });
    }
}
