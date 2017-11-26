package com.ponomarevigor.androidgames.mytimetracker.Tests.WorkSpaceTest1;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ponomarevigor.androidgames.mytimetracker.R;

/**
 * Created by Igorek on 21.10.2017.
 */

public class WorkspaceViewHolder extends RecyclerView.ViewHolder{
    public RelativeLayout baseLayout;
    public RelativeLayout layoutClickable;
    public RelativeLayout frontLayout;
    public ImageView imageIndicator;
    public TextView textName;
    public TextView textDescription;
    public TextView textNumber;

    public WorkspaceViewHolder(View view) {
        super(view);
        baseLayout = (RelativeLayout) view.findViewById(R.id.baseLayout);
        layoutClickable = (RelativeLayout) view.findViewById(R.id.layoutClickable);
        frontLayout = (RelativeLayout) view.findViewById(R.id.frontLayout);
        imageIndicator = (ImageView)view.findViewById(R.id.imageIndicator);
        textName = (TextView) view.findViewById(R.id.tvName);
        textDescription = (TextView) view.findViewById(R.id.tvDescription);
        textNumber = (TextView) view.findViewById(R.id.tvNumber);
    }
}
