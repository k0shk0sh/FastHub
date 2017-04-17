package com.fastaccess.ui.widgets.recyclerview.scroll;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

/**
 * Created by thermatk on 17/04/2017.
 * Original source: https://github.com/plusCubed/recycler-fast-scroll
 */
public class RecyclerFastScroller extends FrameLayout {

    private static final int DEFAULT_AUTO_HIDE_DELAY = 1500;

    private final View mBar;
    private final View mHandle;
    final int mHiddenTranslationX;
    private final Runnable mHide;
    private final int mMinScrollHandleHeight;


    RecyclerView mRecyclerView;

    AnimatorSet mAnimator;
    boolean mAnimatingIn;

    private int mHideDelay;
    private boolean mHidingEnabled;
    private int mHandleNormalColor;
    private int mHandlePressedColor;
    private int mBarColor;
    private int mTouchTargetWidth;
    private int mBarInset;

    private boolean mHideOverride;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.AdapterDataObserver mAdapterObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            requestLayout();
        }
    };

    public RecyclerFastScroller(Context context) {
        this(context, null);
    }

    public RecyclerFastScroller(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        mBarColor = resolveColor(context, R.attr.colorControlNormal);

        mHandleNormalColor = resolveColor(context, R.attr.colorControlNormal);

        mHandlePressedColor = resolveColor(context, R.attr.colorAccent);

        mTouchTargetWidth = convertDpToPx(context, 24);

        mHideDelay = DEFAULT_AUTO_HIDE_DELAY;

        mHidingEnabled = true;

        int fortyEightDp = convertDpToPx(context, 48);
        setLayoutParams(new ViewGroup.LayoutParams(fortyEightDp, ViewGroup.LayoutParams.MATCH_PARENT));

        mBar = new View(context);
        mHandle = new View(context);
        addView(mBar);
        addView(mHandle);

        int eightDp = convertDpToPx(getContext(), 8);
        mBarInset = mTouchTargetWidth - eightDp;

        if (mTouchTargetWidth > fortyEightDp) {
            throw new RuntimeException("Touch target width cannot be larger than 48dp!");
        }

        mBar.setLayoutParams(new LayoutParams(mTouchTargetWidth, ViewGroup.LayoutParams.MATCH_PARENT, GravityCompat.END));
        mHandle.setLayoutParams(new LayoutParams(mTouchTargetWidth, ViewGroup.LayoutParams.MATCH_PARENT, GravityCompat.END));

        updateHandleColorsAndInset();
        updateBarColorAndInset();

        mMinScrollHandleHeight = fortyEightDp;

        mHiddenTranslationX = (isRTL(getContext()) ? -1 : 1) * eightDp;
        mHide = () -> {
            if (!mHandle.isPressed()) {
                if (mAnimator != null && mAnimator.isStarted()) {
                    mAnimator.cancel();
                }
                mAnimator = new AnimatorSet();
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(RecyclerFastScroller.this, View.TRANSLATION_X,
                        mHiddenTranslationX);
                animator2.setInterpolator(new FastOutLinearInInterpolator());
                animator2.setDuration(150);
                mHandle.setEnabled(false);
                mAnimator.play(animator2);
                mAnimator.start();
            }
        };

        mHandle.setOnTouchListener(new OnTouchListener() {
            private float mInitialBarHeight;
            private float mLastPressedYAdjustedToInitial;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    mHandle.setPressed(true);
                    mRecyclerView.stopScroll();

                    mInitialBarHeight = mBar.getHeight();
                    mLastPressedYAdjustedToInitial = event.getY() + mHandle.getY() + mBar.getY();
                } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                    float newHandlePressedY = event.getY() + mHandle.getY() + mBar.getY();
                    int barHeight = mBar.getHeight();
                    float newHandlePressedYAdjustedToInitial =
                            newHandlePressedY + (mInitialBarHeight - barHeight);

                    float deltaPressedYFromLastAdjustedToInitial =
                            newHandlePressedYAdjustedToInitial - mLastPressedYAdjustedToInitial;

                    int dY = (int) ((deltaPressedYFromLastAdjustedToInitial / mInitialBarHeight) *
                            (mRecyclerView.computeVerticalScrollRange()));

                    if (mRecyclerView != null) {
                        try {
                            mRecyclerView.scrollBy(0, dY);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }

                    mLastPressedYAdjustedToInitial = newHandlePressedYAdjustedToInitial;
                } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    mLastPressedYAdjustedToInitial = -1;

                    mRecyclerView.stopNestedScroll();

                    mHandle.setPressed(false);
                    postAutoHide();
                }

                return true;
            }
        });

        setTranslationX(mHiddenTranslationX);
    }

    private void updateHandleColorsAndInset() {
        StateListDrawable drawable = new StateListDrawable();

        if (!isRTL(getContext())) {
            drawable.addState(View.PRESSED_ENABLED_STATE_SET,
                    new InsetDrawable(new ColorDrawable(mHandlePressedColor), mBarInset, 0, 0, 0));
            drawable.addState(View.EMPTY_STATE_SET,
                    new InsetDrawable(new ColorDrawable(mHandleNormalColor), mBarInset, 0, 0, 0));
        } else {
            drawable.addState(View.PRESSED_ENABLED_STATE_SET,
                    new InsetDrawable(new ColorDrawable(mHandlePressedColor), 0, 0, mBarInset, 0));
            drawable.addState(View.EMPTY_STATE_SET,
                    new InsetDrawable(new ColorDrawable(mHandleNormalColor), 0, 0, mBarInset, 0));
        }
        mHandle.setBackground(drawable);
    }

    private void updateBarColorAndInset() {
        Drawable drawable;

        if (!isRTL(getContext())) {
            drawable = new InsetDrawable(new ColorDrawable(mBarColor), mBarInset, 0, 0, 0);
        } else {
            drawable = new InsetDrawable(new ColorDrawable(mBarColor), 0, 0, mBarInset, 0);
        }
        drawable.setAlpha(57);
        mBar.setBackground(drawable);
    }

    public void attachRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerFastScroller.this.show();
            }
        });
        if (recyclerView.getAdapter() != null && recyclerView.getAdapter() != mAdapter) {
            if (mAdapter != null) {
                mAdapter.unregisterAdapterDataObserver(mAdapterObserver);
            }
            recyclerView.getAdapter().registerAdapterDataObserver(mAdapterObserver);
            mAdapter = recyclerView.getAdapter();
        }
    }

    /**
     * Show the fast scroller and hide after delay
     */
    public void show() {
        requestLayout();

        post(new Runnable() {
            @Override
            public void run() {
                if (mHideOverride) {
                    return;
                }

                mHandle.setEnabled(true);
                if (!mAnimatingIn && getTranslationX() != 0) {
                    if (mAnimator != null && mAnimator.isStarted()) {
                        mAnimator.cancel();
                    }
                    mAnimator = new AnimatorSet();
                    ObjectAnimator animator = ObjectAnimator.ofFloat(RecyclerFastScroller.this, View.TRANSLATION_X, 0);
                    animator.setInterpolator(new LinearOutSlowInInterpolator());
                    animator.setDuration(100);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mAnimatingIn = false;
                        }
                    });
                    mAnimatingIn = true;
                    mAnimator.play(animator);
                    mAnimator.start();
                }
                postAutoHide();
            }
        });
    }

    void postAutoHide() {
        if (mRecyclerView != null && mHidingEnabled) {
            mRecyclerView.removeCallbacks(mHide);
            mRecyclerView.postDelayed(mHide, mHideDelay);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mRecyclerView == null) return;

        int scrollOffset = mRecyclerView.computeVerticalScrollOffset();
        int verticalScrollRange = mRecyclerView.computeVerticalScrollRange() + mRecyclerView.getPaddingBottom();

        int barHeight = mBar.getHeight();
        float ratio = (float) scrollOffset / (verticalScrollRange - barHeight);

        int calculatedHandleHeight = (int) ((float) barHeight / verticalScrollRange * barHeight);
        if (calculatedHandleHeight < mMinScrollHandleHeight) {
            calculatedHandleHeight = mMinScrollHandleHeight;
        }

        if (calculatedHandleHeight >= barHeight) {
            setTranslationX(mHiddenTranslationX);
            mHideOverride = true;
            return;
        }

        mHideOverride = false;

        float y = ratio * (barHeight - calculatedHandleHeight);

        mHandle.layout(mHandle.getLeft(), (int) y, mHandle.getRight(), (int) y + calculatedHandleHeight);
    }

    private static boolean isRTL(Context context) {
        return context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    @ColorInt
    public static int resolveColor(Context context, @AttrRes int color) {
        TypedArray a = context.obtainStyledAttributes(new int[]{color});
        int resId = a.getColor(0, 0);
        a.recycle();
        return resId;
    }

    public static int convertDpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}