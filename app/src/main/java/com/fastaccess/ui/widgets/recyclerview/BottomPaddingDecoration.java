package com.fastaccess.ui.widgets.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.ViewHelper;

class BottomPaddingDecoration extends RecyclerView.ItemDecoration {
    private final int bottomPadding;

    private BottomPaddingDecoration(int bottomOffset) {
        bottomPadding = bottomOffset;
    }

    private BottomPaddingDecoration(@NonNull Context context) {
        this(ViewHelper.toPx(context, context.getResources().getDimensionPixelSize(R.dimen.fab_spacing)));
    }

    public static BottomPaddingDecoration with(int bottomPadding) {
        return new BottomPaddingDecoration(bottomPadding);
    }

    public static BottomPaddingDecoration with(@NonNull Context context) {
        return new BottomPaddingDecoration(context);
    }

    @Override public void getItemOffsets(@NonNull Rect outRect, View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int dataSize = state.getItemCount();
        int position = parent.getChildAdapterPosition(view);
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager grid = (GridLayoutManager) parent.getLayoutManager();
            if ((dataSize - position) <= grid.getSpanCount()) {
                outRect.set(0, 0, 0, bottomPadding);
            } else {
                outRect.set(0, 0, 0, 0);
            }
        } else if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            if (dataSize > 0 && position == dataSize - 1) {
                outRect.set(0, 0, 0, bottomPadding);
            } else {
                outRect.set(0, 0, 0, 0);
            }
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager grid = (StaggeredGridLayoutManager) parent.getLayoutManager();
            if ((dataSize - position) <= grid.getSpanCount()) {
                outRect.set(0, 0, 0, bottomPadding);
            } else {
                outRect.set(0, 0, 0, 0);
            }
        }
    }
}