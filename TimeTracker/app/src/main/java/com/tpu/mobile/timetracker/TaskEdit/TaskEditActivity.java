package com.tpu.mobile.timetracker.TaskEdit;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tpu.mobile.timetracker.R;
import com.tpu.mobile.timetracker.TaskEdit.Pager.PageAdapter;
import com.tpu.mobile.timetracker.TaskEdit.Pager.PageMain;
import com.tpu.mobile.timetracker.TaskEdit.Pager.PageStatistics;

import io.realm.Realm;

/**
 * Created by Igorek on 06.11.2017.
 */

public class TaskEditActivity extends AppCompatActivity {
    public ViewPager viewPager;
    public PageAdapter pagerAdapter;
    public FragmentManager fragmentManager;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_activity_edit);

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        fragmentManager = getSupportFragmentManager();
        viewPager = (ViewPager)findViewById(R.id.container);
        tabLayout.setupWithViewPager(viewPager);
        pagerAdapter = new PageAdapter(this.getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TextView tvBack = (TextView)findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(TaskEditActivity.this, com.ponomarevigor.androidgames.mytimetracker.Task.TaskFragment.class);;
                //startActivity(intent);
                onBackPressed();
            }
        });

        Button bSave = (Button) findViewById(R.id.tvSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PageMain pageMain = (PageMain) fragmentManager.getFragments().get(0);
                pageMain.Save();

                PageStatistics pageStat = (PageStatistics) fragmentManager.getFragments().get(1);
                pageStat.Save();

                //Intent intent = new Intent(TaskEditActivity.this, com.ponomarevigor.androidgames.mytimetracker.Task.TaskFragment.class);;
                //startActivity(intent);
                onBackPressed();
            }
        });
    }
}
