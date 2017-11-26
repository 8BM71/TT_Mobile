package com.ponomarevigor.androidgames.mytimetracker.Tests.TaskTest5.ItemTaskTouchHelper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.ponomarevigor.androidgames.mytimetracker.Tests.TaskTest5.TaskViewHolder;

/**
 * Created by Ponomarev Igor on 20.10.2017.
 */

public class ItemTaskTouchHelper extends ItemTouchHelper.Callback {
    ItemTaskTouchHelperAdapter adapter;

    public ItemTaskTouchHelper(ItemTaskTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeFlag = ItemTouchHelper.START;
        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlag, swipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemDismiss(viewHolder);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View view = ((TaskViewHolder) viewHolder).frontLayout;
            getDefaultUIUtil().onDraw(c, recyclerView, view, dX, dY, actionState, isCurrentlyActive);
        } else
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View view = ((TaskViewHolder) viewHolder).frontLayout;
            getDefaultUIUtil().onDraw(c, recyclerView, view, dX, dY, actionState, isCurrentlyActive);
        } else
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        View view1 = ((TaskViewHolder) viewHolder).baseLayout;
        View view2 = ((TaskViewHolder) viewHolder).frontLayout;
        getDefaultUIUtil().clearView(view1);
        getDefaultUIUtil().clearView(view2);
        ItemTaskTouchHelperViewHolder itemViewHolder = (ItemTaskTouchHelperViewHolder) viewHolder;
        itemViewHolder.onItemClear();
    }


  @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (viewHolder instanceof ItemTaskTouchHelperViewHolder) {
                ItemTaskTouchHelperViewHolder itemViewHolder = (ItemTaskTouchHelperViewHolder) viewHolder;
                itemViewHolder.onItemSelected();
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }
}
