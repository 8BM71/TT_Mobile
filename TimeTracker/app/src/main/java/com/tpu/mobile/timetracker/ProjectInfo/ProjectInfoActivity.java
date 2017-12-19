package com.tpu.mobile.timetracker.ProjectInfo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tpu.mobile.timetracker.Database.Project;
import com.tpu.mobile.timetracker.ProjectInfo.Pager.PageAdapter;
import com.tpu.mobile.timetracker.R;

import io.realm.Realm;

/**
 * Created by Igorek on 06.11.2017.
 */

public class ProjectInfoActivity extends AppCompatActivity {
    public ViewPager viewPager;
    public PageAdapter pagerAdapter;
    public FragmentManager fragmentManager;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_activity_info);

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        int idProject = getIntent().getIntExtra("projectID", 0);
        Project project = realm.where(Project.class).equalTo("id", idProject).findFirst();
        TextView tvName = (TextView)findViewById(R.id.tvNameProject);
        tvName.setText(project.getName());
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
                onBackPressed();
            }
        });
    }
}
