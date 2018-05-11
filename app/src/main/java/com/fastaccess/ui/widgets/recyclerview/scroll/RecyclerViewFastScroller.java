package com.fastaccess.ui.widgets.recyclerview.scroll;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.Logger;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class RecyclerViewFastScroller extends FrameLayout {

    private static final int TRACK_SNAP_RANGE = 5;
    private ImageView scrollerView;
    private ImageButton scrollTop;
    private int height;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AppBarLayout appBarLayout;
    private BottomNavigation bottomNavigation;
    private boolean toggled;
    private boolean registeredObserver = false;

    public RecyclerViewFastScroller(Context context) {
        super(context);
        init();
    }

    public RecyclerViewFastScroller(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewFastScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
    }

    @SuppressLint("ClickableViewAccessibility") @Override public boolean onTouchEvent(@NonNull MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < (scrollerView.getX() - scrollerView.getPaddingStart())) return false;
                scrollerView.setSelected(true);
                hideAppbar();
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                setScrollerHeight(y);
                setRecyclerViewPosition(y);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                scrollerView.setSelected(false);
                showAppbar();
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override protected void onDetachedFromWindow() {
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(onScrollListener);
            safelyUnregisterObserver();
        }
        appBarLayout = null;
        bottomNavigation = null;
        super.onDetachedFromWindow();
    }

    private void safelyUnregisterObserver() {
        try {// rare case
            if (registeredObserver && recyclerView.getAdapter() != null) {
                recyclerView.getAdapter().unregisterAdapterDataObserver(observer);
            }
        } catch (Exception ignored) {}
    }

    protected void init() {
        setVisibility(GONE);
        setClipChildren(false);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.fastscroller_layout, this);
        scrollerView = findViewById(R.id.fast_scroller_handle);
        setVisibility(VISIBLE);
        Activity activity = ActivityHelper.getActivity(getContext());
        if (activity != null) {
            appBarLayout = activity.findViewById(R.id.appbar);
            bottomNavigation = activity.findViewById(R.id.bottomNavigation);
        }
    }

    protected void hideAppbar() {
        if (!toggled) {
            if (appBarLayout != null) {
                appBarLayout.setExpanded(false, true);
            }
            if (bottomNavigation != null) {
                bottomNavigation.setExpanded(false, true);
            }
            toggled = true;
        }
    }

    protected void showAppbar() {
        if (toggled) {
            if (scrollerView.getY() == 0) {
                if (appBarLayout != null) {
                    appBarLayout.setExpanded(true, true);
                }
                if (bottomNavigation != null) {
                    bottomNavigation.setExpanded(true, true);
                }
                toggled = false;
            }
        }
    }

    public void attachRecyclerView(final RecyclerView recyclerView) {
        if (this.recyclerView == null) {
            this.recyclerView = recyclerView;
            this.layoutManager = recyclerView.getLayoutManager();
            this.recyclerView.addOnScrollListener(onScrollListener);
            if (recyclerView.getAdapter() != null && !registeredObserver) {
                recyclerView.getAdapter().registerAdapterDataObserver(observer);
                registeredObserver = true;
            }
            hideShow();
            initScrollHeight();
        }
    }

    private void initScrollHeight() {
        if (recyclerView.computeVerticalScrollOffset() == 0) {
            this.recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override public boolean onPreDraw() {
                    RecyclerViewFastScroller.this.recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                    iniHeight();
                    return true;
                }
            });
        } else {
            iniHeight();
        }
    }

    protected void iniHeight() {
        if (scrollerView.isSelected()) return;
        int verticalScrollOffset = RecyclerViewFastScroller.this.recyclerView.computeVerticalScrollOffset();
        int verticalScrollRange = RecyclerViewFastScroller.this.computeVerticalScrollRange();
        float proportion = (float) verticalScrollOffset / ((float) verticalScrollRange - height);
        setScrollerHeight(height * proportion);
    }

    private void setRecyclerViewPosition(float y) {
        Logger.e(y);
        if (recyclerView != null) {
            int itemCount = recyclerView.getAdapter().getItemCount();
            float proportion;
            if (scrollerView.getY() == 0) {
                proportion = 0f;
            } else if (scrollerView.getY() + scrollerView.getHeight() >= height - TRACK_SNAP_RANGE) {
                proportion = 1f;
            } else {
                proportion = y / (float) height;
            }
            int targetPos = getValueInRange(itemCount - 1, (int) (proportion * (float) itemCount));
            if (layoutManager instanceof StaggeredGridLayoutManager) {
                ((StaggeredGridLayoutManager) layoutManager).scrollToPositionWithOffset(targetPos, 0);
            } else if (layoutManager instanceof GridLayoutManager) {
                ((GridLayoutManager) layoutManager).scrollToPositionWithOffset(targetPos, 0);
            } else {
                ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(targetPos, 0);
            }
        }
    }

    private static int getValueInRange(int max, int value) {
        return Math.min(Math.max(0, value), max);
    }

    private void setScrollerHeight(float y) {
        int handleHeight = scrollerView.getHeight();
        scrollerView.setY(getValueInRange(height - handleHeight, (int) (y - handleHeight / 2)));
    }

    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (scrollerView.isSelected()) return;
            int verticalScrollOffset = recyclerView.computeVerticalScrollOffset();
            int verticalScrollRange = recyclerView.computeVerticalScrollRange();
            float proportion = (float) verticalScrollOffset / ((float) verticalScrollRange - height);
            setScrollerHeight(height * proportion);
        }
    };

    private final RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            hideShow();
        }

        @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            hideShow();
        }

        @Override public void onChanged() {
            super.onChanged();
            hideShow();

        }
    };

    protected void hideShow() {
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            setVisibility(recyclerView.getAdapter().getItemCount() > 10 ? VISIBLE : GONE);
        } else {
            setVisibility(GONE);
        }
    }
}