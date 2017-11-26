package com.ponomarevigor.androidgames.mytimetracker.Task.ItemTaskTouchHelper;

import android.support.v7.widget.RecyclerView;

public interface ItemTaskTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
    void onItemDismiss(RecyclerView.ViewHolder viewHolder);
}
