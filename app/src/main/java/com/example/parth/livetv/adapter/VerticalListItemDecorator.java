package com.example.parth.livetv.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Parth on 25-10-2016.
 */

public class VerticalListItemDecorator extends RecyclerView.ItemDecoration {
    private final int verticalSpaceHeight;

    public VerticalListItemDecorator(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) < 1) {
            outRect.top = verticalSpaceHeight / 2;
        }
        outRect.bottom = verticalSpaceHeight;

    }

}

