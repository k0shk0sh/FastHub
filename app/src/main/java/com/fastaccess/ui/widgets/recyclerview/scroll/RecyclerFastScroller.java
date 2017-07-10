package com.fastaccess.ui.widgets.recyclerview.scroll;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fastaccess.R;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;

/**
 * Created by thermatk on 17/04/2017.
 * Original source: https://github.com/plusCubed/recycler-fast-scroll
 */
public class RecyclerFastScroller extends FrameLayout {

    private static final int DEFAULT_AUTO_HIDE_DELAY = 1500;

    private final View bar;
    private final View handle;
    final int hiddenTranslationX;
    private final Runnable hideRunnable;
    private final int minScrollHandleHeight;
    private RecyclerView recyclerView;
    private AnimatorSet animator;
    boolean animatingIn;

    private int hideDelay;
    private boolean hidingEnabled;
    private int handleNormalColor;
    private int handlePressedColor;
    private int touchTargetWidth;
    private int barInset;
    private OnLoadMore onLoadMore;
    private boolean hideOverride;
    private RecyclerView.Adapter adapter;
    private RecyclerView.AdapterDataObserver adapterObserver = new RecyclerView.AdapterDataObserver() {
        @Override public void onChanged() {
            super.onChanged();
            requestLayout();
        }
    };

    public RecyclerFastScroller(Context context) {
        this(context, null);
    }

    public RecyclerFastScroller(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        handleNormalColor = resolveColor(context, R.attr.colorControlNormal);
        handlePressedColor = resolveColor(context, R.attr.colorAccent);
        touchTargetWidth = convertDpToPx(context, 24);
        hideDelay = DEFAULT_AUTO_HIDE_DELAY;
        hidingEnabled = true;
        int fortyEightDp = convertDpToPx(context, 48);
        setLayoutParams(new ViewGroup.LayoutParams(fortyEightDp, ViewGroup.LayoutParams.MATCH_PARENT));
        bar = new View(context);
        handle = new View(context);
        addView(bar);
        addView(handle);
        int eightDp = convertDpToPx(getContext(), 8);
        barInset = touchTargetWidth - eightDp;
        if (touchTargetWidth > fortyEightDp) {
            throw new RuntimeException("Touch target width cannot be larger than 48dp!");
        }
        bar.setLayoutParams(new LayoutParams(touchTargetWidth, ViewGroup.LayoutParams.MATCH_PARENT, GravityCompat.END));
        handle.setLayoutParams(new LayoutParams(touchTargetWidth, convertDpToPx(context, 80), GravityCompat.END));
        updateHandleColorsAndInset();
        updateBarColorAndInset();
        minScrollHandleHeight = fortyEightDp;
        hiddenTranslationX = ((isRTL(getContext()) ? -1 : 1) * eightDp) + convertDpToPx(getContext(), 4);
        hideRunnable = () -> {
            if (!handle.isPressed()) {
                if (animator != null && animator.isStarted()) {
                    animator.cancel();
                }
                animator = new AnimatorSet();
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(RecyclerFastScroller.this, View.TRANSLATION_X,
                        hiddenTranslationX);
                animator2.setInterpolator(new FastOutLinearInInterpolator());
                animator2.setDuration(150);
                handle.setEnabled(false);
                animator.play(animator2);
                animator.start();
            }
        };

        handle.setOnTouchListener(new OnTouchListener() {
            private float mInitialBarHeight;
            private float mLastPressedYAdjustedToInitial;

            @SuppressLint("ClickableViewAccessibility")
            @Override public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    handle.setPressed(true);
                    recyclerView.stopScroll();
                    mInitialBarHeight = bar.getHeight();
                    mLastPressedYAdjustedToInitial = event.getY() + handle.getY() + bar.getY();
                } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                    float newHandlePressedY = event.getY() + handle.getY() + bar.getY();
                    int barHeight = bar.getHeight();
                    float newHandlePressedYAdjustedToInitial = newHandlePressedY + (mInitialBarHeight - barHeight);
                    float deltaPressedYFromLastAdjustedToInitial = newHandlePressedYAdjustedToInitial - mLastPressedYAdjustedToInitial;
                    int dY = (int) ((deltaPressedYFromLastAdjustedToInitial / mInitialBarHeight) * (recyclerView.computeVerticalScrollRange()));
                    if (recyclerView != null) {
                        try {
                            recyclerView.scrollBy(0, dY);
                            if (onLoadMore != null) {
                                onLoadMore.onScrollStateChanged(recyclerView, RecyclerView.SCROLL_STATE_DRAGGING);
                            }
                        } catch (Exception t) {
                            t.printStackTrace();
                        }
                    }
                    mLastPressedYAdjustedToInitial = newHandlePressedYAdjustedToInitial;
                } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    mLastPressedYAdjustedToInitial = -1;
                    recyclerView.stopNestedScroll();
                    handle.setPressed(false);
                    postAutoHide();
                }
                return true;
            }
        });
        setTranslationX(hiddenTranslationX);
    }

    @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateScroller();
    }

    private void updateScroller() {
        if (recyclerView == null) return;
        int scrollOffset = recyclerView.computeVerticalScrollOffset();
        int verticalScrollRange = recyclerView.computeVerticalScrollRange() + recyclerView.getPaddingBottom();
        int barHeight = bar.getHeight();
        float ratio = (float) scrollOffset / (verticalScrollRange - barHeight);
        int calculatedHandleHeight = (int) ((float) barHeight / verticalScrollRange * barHeight);
        if (calculatedHandleHeight < minScrollHandleHeight) {
            calculatedHandleHeight = minScrollHandleHeight;
        }
        if (calculatedHandleHeight >= barHeight) {
            setTranslationX(hiddenTranslationX);
            hideOverride = true;
            return;
        }
        hideOverride = false;
        float y = ratio * (barHeight - calculatedHandleHeight);
        handle.setY(y);
    }

    private void updateHandleColorsAndInset() {
        StateListDrawable drawable = new StateListDrawable();

        if (!isRTL(getContext())) {
            drawable.addState(View.PRESSED_ENABLED_STATE_SET,
                    new InsetDrawable(new ColorDrawable(handlePressedColor), barInset, 0, 0, 0));
            drawable.addState(View.EMPTY_STATE_SET,
                    new InsetDrawable(new ColorDrawable(handleNormalColor), barInset, 0, 0, 0));
        } else {
            drawable.addState(View.PRESSED_ENABLED_STATE_SET,
                    new InsetDrawable(new ColorDrawable(handlePressedColor), 0, 0, barInset, 0));
            drawable.addState(View.EMPTY_STATE_SET,
                    new InsetDrawable(new ColorDrawable(handleNormalColor), 0, 0, barInset, 0));
        }
        handle.setBackground(drawable);
    }

    private void updateBarColorAndInset() {
//        Drawable drawable;
//
//        if (!isRTL(getContext())) {
//            drawable = new InsetDrawable(new ColorDrawable(mBarColor), barInset, 0, 0, 0);
//        } else {
//            drawable = new InsetDrawable(new ColorDrawable(mBarColor), 0, 0, barInset, 0);
//        }
//        drawable.setAlpha(57);
//        bar.setBackground(drawable);
    }

    public void attachRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerFastScroller.this.show();
            }
        });
        if (recyclerView.getAdapter() != null && recyclerView.getAdapter() != adapter) {
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(adapterObserver);
            }
            recyclerView.getAdapter().registerAdapterDataObserver(adapterObserver);
            adapter = recyclerView.getAdapter();
        }
    }

    public void show() {
        requestLayout();

        post(new Runnable() {
            @Override
            public void run() {
                if (hideOverride) {
                    return;
                }

                handle.setEnabled(true);
                if (!animatingIn && getTranslationX() != 0) {
                    if (animator != null && animator.isStarted()) {
                        animator.cancel();
                    }
                    animator = new AnimatorSet();
                    ObjectAnimator animator = ObjectAnimator.ofFloat(RecyclerFastScroller.this, View.TRANSLATION_X, 0);
                    animator.setInterpolator(new LinearOutSlowInInterpolator());
                    animator.setDuration(100);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            animatingIn = false;
                        }
                    });
                    animatingIn = true;
                    RecyclerFastScroller.this.animator.play(animator);
                    RecyclerFastScroller.this.animator.start();
                }
                postAutoHide();
            }
        });
    }

    void postAutoHide() {
        if (recyclerView != null && hidingEnabled) {
            recyclerView.removeCallbacks(hideRunnable);
            recyclerView.postDelayed(hideRunnable, hideDelay);
        }
    }

    @ColorInt private int resolveColor(Context context, @AttrRes int color) {
        TypedArray a = context.obtainStyledAttributes(new int[]{color});
        int resId = a.getColor(0, 0);
        a.recycle();
        return resId;
    }

    private boolean isRTL(Context context) {
        return context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    private int convertDpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public void setOnLoadMore(OnLoadMore onLoadMore) {
        this.onLoadMore = onLoadMore;
    }
}