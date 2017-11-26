package com.ponomarevigor.androidgames.mytimetracker.TaskEditing.Pager;

import android.support.v4.app.Fragment;

import io.realm.Realm;

/**
 * Created by Igorek on 26.10.2017.
 */

public class FactoryPageFragment {
    public static final int TYPE_DESCRIPTION = 0;
    public static final int TYPE_STATISTICS = 1;

    public static Fragment createFragment(int type, int positionTask)
    {
        switch (type)
        {
            case(TYPE_DESCRIPTION):
                return PageMain.newInstance(positionTask);
            case(TYPE_STATISTICS):
                return PageStatistics.newInstance(positionTask);
            default:
                return null;
        }
    }
}
