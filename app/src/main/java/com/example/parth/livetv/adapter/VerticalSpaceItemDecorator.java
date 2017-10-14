package com.example.parth.livetv.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Parth on 25-10-2016.
 */

public class VerticalSpaceItemDecorator extends RecyclerView.ItemDecoration {
    private final int verticalSpaceHeight;

    public VerticalSpaceItemDecorator(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) <= 1) {
            outRect.top = verticalSpaceHeight / 2;
        }
        outRect.bottom = verticalSpaceHeight;
        if (parent.getChildAdapterPosition(view) %2==0) {
            outRect.left = 24;
            outRect.right = 8;
        }else{
            outRect.right = 24;
            outRect.left = 8;
        }
    }

}

