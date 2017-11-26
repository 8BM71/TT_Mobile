package com.ponomarevigor.androidgames.mytimetracker.Tests.WorkSpaceTest1.ItemWorkspaceTouchHelper;

import android.support.v7.widget.RecyclerView;

public interface ItemWorkspaceTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
    void onItemDismiss(RecyclerView.ViewHolder viewHolder);
}
