package com.fastaccess.ui.widgets.recyclerview.scroll;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;

/**
 * Created by Kosh on 8/2/2015. copyrights are reserved @
 */
public abstract class InfiniteScroll extends RecyclerView.OnScrollListener {
    private int visibleThreshold = 3;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;
    private int startingPageIndex = 0;
    private RecyclerView.LayoutManager layoutManager;
    private BaseRecyclerAdapter adapter;
    private boolean newlyAdded = true;

    public InfiniteScroll() {}

    private void initLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        if (layoutManager instanceof GridLayoutManager) {
            visibleThreshold = visibleThreshold * ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            visibleThreshold = visibleThreshold * ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
    }

    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (newlyAdded) {
            newlyAdded = false;
            return;
        }
        onScrolled(dy > 0);
        if (layoutManager == null) {
            initLayoutManager(recyclerView.getLayoutManager());
        }
        if (adapter == null) {
            if (recyclerView.getAdapter() instanceof BaseRecyclerAdapter) {
                adapter = (BaseRecyclerAdapter) recyclerView.getAdapter();
            }
        }
        if (adapter != null && adapter.isProgressAdded()) return;

        int lastVisibleItemPosition = 0;
        int totalItemCount = layoutManager.getItemCount();
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
        } else if (layoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }
        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            currentPage++;
            boolean isCallingApi = onLoadMore(currentPage, totalItemCount);
            loading = true;
            if (adapter != null && isCallingApi) {
                adapter.addProgress();
            }
        }
    }

    public void reset() {
        this.currentPage = this.startingPageIndex;
        this.previousTotalItemCount = 0;
        this.loading = true;
    }

    public void initialize(int page, int previousTotal) {
        this.currentPage = page;
        this.previousTotalItemCount = previousTotal;
        this.loading = true;
    }

    public abstract boolean onLoadMore(int page, int totalItemsCount);

    public void onScrolled(boolean isUp) {}

}

