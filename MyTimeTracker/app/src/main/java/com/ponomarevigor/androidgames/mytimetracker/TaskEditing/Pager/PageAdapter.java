package com.ponomarevigor.androidgames.mytimetracker.TaskEditing.Pager;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.realm.Realm;

/**
 * Created by Igorek on 05.10.2017.
 */

public class PageAdapter extends FragmentPagerAdapter {

    int positionTask = 0;

    public PageAdapter(FragmentManager fm, int positionTask) {

        super(fm);
        this.positionTask = positionTask;
    }

    @Override
    public Fragment getItem(final int position) {
        Fragment fragment = FactoryPageFragment.createFragment(position, positionTask);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Description";
            case 1:
                return "Statistics";
        }
        return null;
    }
}
