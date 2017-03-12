package com.fastaccess.ui.widgets.recyclerview.scroll;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Kosh on 8/2/2015. copyrights are reserved @Innov8tif
 */
@SuppressWarnings("FieldCanBeLocal") public abstract class InfiniteScroll extends RecyclerView.OnScrollListener {
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 2;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;
    private int current_page = 0;
    private RecyclerViewPositionHelper mRecyclerViewHelper;
    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    public InfiniteScroll() {}

    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mRecyclerViewHelper.getItemCount();
        firstVisibleItem = mRecyclerViewHelper.findFirstVisibleItemPosition();
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            current_page++;
            onLoadMore(current_page, previousTotal);
            loading = true;
        }
        if (firstVisibleItem + visibleItemCount >= totalItemCount) {
            onScrollToLast(recyclerView);
        }
    }

    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            onHide(recyclerView);
            controlsVisible = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            onShow(recyclerView);
            controlsVisible = true;
            scrolledDistance = 0;
        }
        if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
            scrolledDistance += dy;
        }
    }

    @SuppressWarnings("WeakerAccess") protected void onScrollToLast(RecyclerView recyclerView) {}

    @SuppressWarnings("WeakerAccess") protected void onShow(RecyclerView recyclerView) {}

    @SuppressWarnings("WeakerAccess") protected void onHide(RecyclerView recyclerView) {

    }

    protected void onLoadMore(int page, int previousTotal) {}

    public void reset() {
        this.previousTotal = 0;
        this.loading = true;
        this.current_page = 0;
    }

    public void setCurrent_page(int page, int previousTotal) {
        this.current_page = page;
        this.previousTotal = previousTotal;
        this.loading = true;

    }
}

