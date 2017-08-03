package com.fastaccess.ui.widgets.recyclerview.layout_manager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Kosh on 17 May 2016, 10:02 PM
 */
public class GridManager extends GridLayoutManager {

    private int iconSize;

    public GridManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public GridManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public GridManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
            updateCount();
        } catch (Exception ignored) {}
    }

    @Override public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        try {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
        } catch (Exception ignored) {}
    }

    private void updateCount() {
        if (iconSize > 1) {
            int spanCount = Math.max(1, getWidth() / iconSize);
            if (spanCount < 1) {
                spanCount = 1;
            }
            this.setSpanCount(spanCount);
        }
    }

    public int getIconSize() {
        return iconSize;
    }

    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
        updateCount();
    }
}
