package com.ponomarevigor.androidgames.mytimetracker.Tests.WorkSpaceTest1.ItemWorkspaceTouchHelper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.ponomarevigor.androidgames.mytimetracker.Tests.WorkSpaceTest1.WorkspaceViewHolder;

/**
 * Created by Ponomarev Igor on 20.10.2017.
 */

public class ItemWorkspaceTouchHelper extends ItemTouchHelper.Callback {
    ItemWorkspaceTouchHelperAdapter adapter;

    public ItemWorkspaceTouchHelper(ItemWorkspaceTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeFlag = ItemTouchHelper.START;
        int dragFlag = 0;
        return makeMovementFlags(dragFlag, swipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemDismiss(viewHolder);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View view = ((WorkspaceViewHolder) viewHolder).frontLayout;
            getDefaultUIUtil().onDraw(c, recyclerView, view, dX, dY, actionState, isCurrentlyActive);
        } else
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View view = ((WorkspaceViewHolder) viewHolder).frontLayout;
            getDefaultUIUtil().onDraw(c, recyclerView, view, dX, dY, actionState, isCurrentlyActive);
        } else
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        View view1 = ((WorkspaceViewHolder) viewHolder).baseLayout;
        View view2 = ((WorkspaceViewHolder) viewHolder).frontLayout;
        getDefaultUIUtil().clearView(view1);
        getDefaultUIUtil().clearView(view2);
    }
}
