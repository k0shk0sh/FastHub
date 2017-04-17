package com.pluscubed.recyclerfastscroll;

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
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class RecyclerFastScroller extends FrameLayout {

    private static final int DEFAULT_AUTO_HIDE_DELAY = 1500;

    protected final View mBar;
    protected final View mHandle;
    final int mHiddenTranslationX;
    private final Runnable mHide;
    private final int mMinScrollHandleHeight;
    protected OnTouchListener mOnTouchListener;

    int mAppBarLayoutOffset;

    RecyclerView mRecyclerView;
    CoordinatorLayout mCoordinatorLayout;
    AppBarLayout mAppBarLayout;

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
        this(context, null, 0);
    }

    public RecyclerFastScroller(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerFastScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RecyclerFastScroller(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerFastScroller, defStyleAttr, defStyleRes);

        mBarColor = a.getColor(
                R.styleable.RecyclerFastScroller_rfs_barColor,
                RecyclerFastScrollerUtils.resolveColor(context, R.attr.colorControlNormal));

        mHandleNormalColor = a.getColor(
                R.styleable.RecyclerFastScroller_rfs_handleNormalColor,
                RecyclerFastScrollerUtils.resolveColor(context, R.attr.colorControlNormal));

        mHandlePressedColor = a.getColor(
                R.styleable.RecyclerFastScroller_rfs_handlePressedColor,
                RecyclerFastScrollerUtils.resolveColor(context, R.attr.colorAccent));

        mTouchTargetWidth = a.getDimensionPixelSize(
                R.styleable.RecyclerFastScroller_rfs_touchTargetWidth,
                RecyclerFastScrollerUtils.convertDpToPx(context, 24));

        mHideDelay = a.getInt(R.styleable.RecyclerFastScroller_rfs_hideDelay,
                DEFAULT_AUTO_HIDE_DELAY);

        mHidingEnabled = a.getBoolean(R.styleable.RecyclerFastScroller_rfs_hidingEnabled, true);

        a.recycle();

        int fortyEightDp = RecyclerFastScrollerUtils.convertDpToPx(context, 48);
        setLayoutParams(new ViewGroup.LayoutParams(fortyEightDp, ViewGroup.LayoutParams.MATCH_PARENT));

        mBar = new View(context);
        mHandle = new View(context);
        addView(mBar);
        addView(mHandle);

        setTouchTargetWidth(mTouchTargetWidth);

        mMinScrollHandleHeight = fortyEightDp;

        int eightDp = RecyclerFastScrollerUtils.convertDpToPx(getContext(), 8);
        mHiddenTranslationX = (RecyclerFastScrollerUtils.isRTL(getContext()) ? -1 : 1) * eightDp;
        mHide = new Runnable() {
            @Override
            public void run() {
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
            }
        };

        mHandle.setOnTouchListener(new OnTouchListener() {
            private float mInitialBarHeight;
            private float mLastPressedYAdjustedToInitial;
            private int mLastAppBarLayoutOffset;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mOnTouchListener != null) {
                    mOnTouchListener.onTouch(v, event);
                }
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    mHandle.setPressed(true);
                    mRecyclerView.stopScroll();

                    int nestedScrollAxis = ViewCompat.SCROLL_AXIS_NONE;
                    nestedScrollAxis |= ViewCompat.SCROLL_AXIS_VERTICAL;

//                    mRecyclerView.startNestedScroll(nestedScrollAxis);

                    mInitialBarHeight = mBar.getHeight();
                    mLastPressedYAdjustedToInitial = event.getY() + mHandle.getY() + mBar.getY();
                    mLastAppBarLayoutOffset = mAppBarLayoutOffset;
                } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                    float newHandlePressedY = event.getY() + mHandle.getY() + mBar.getY();
                    int barHeight = mBar.getHeight();
                    float newHandlePressedYAdjustedToInitial =
                            newHandlePressedY + (mInitialBarHeight - barHeight);

                    float deltaPressedYFromLastAdjustedToInitial =
                            newHandlePressedYAdjustedToInitial - mLastPressedYAdjustedToInitial;

                    int dY = (int) ((deltaPressedYFromLastAdjustedToInitial / mInitialBarHeight) *
                            (mRecyclerView.computeVerticalScrollRange() + (mAppBarLayout == null ? 0 : mAppBarLayout.getTotalScrollRange())));

                    if (mCoordinatorLayout != null && mAppBarLayout != null) {
                        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
                        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                        if (behavior != null) {
                            behavior.onNestedPreScroll(mCoordinatorLayout, mAppBarLayout,
                                    RecyclerFastScroller.this, 0, dY, new int[2]);
                        }
                    }

                    updateRvScroll(dY + mLastAppBarLayoutOffset - mAppBarLayoutOffset);

                    mLastPressedYAdjustedToInitial = newHandlePressedYAdjustedToInitial;
                    mLastAppBarLayoutOffset = mAppBarLayoutOffset;
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

    @ColorInt
    public int getHandlePressedColor() {
        return mHandlePressedColor;
    }

    public void setHandlePressedColor(@ColorInt int colorPressed) {
        mHandlePressedColor = colorPressed;
        updateHandleColorsAndInset();
    }

    @ColorInt
    public int getHandleNormalColor() {
        return mHandleNormalColor;
    }

    public void setHandleNormalColor(@ColorInt int colorNormal) {
        mHandleNormalColor = colorNormal;
        updateHandleColorsAndInset();
    }

    @ColorInt
    public int getBarColor() {
        return mBarColor;
    }

    /**
     * @param scrollBarColor
     *         Scroll bar color. Alpha will be set to ~22% to match stock scrollbar.
     */
    public void setBarColor(@ColorInt int scrollBarColor) {
        mBarColor = scrollBarColor;
        updateBarColorAndInset();
    }

    public int getHideDelay() {
        return mHideDelay;
    }

    /**
     * @param hideDelay
     *         the delay in millis to hide the scrollbar
     */
    public void setHideDelay(int hideDelay) {
        mHideDelay = hideDelay;
    }

    public int getTouchTargetWidth() {
        return mTouchTargetWidth;
    }

    /**
     * @param touchTargetWidth
     *         In pixels, less than or equal to 48dp
     */
    public void setTouchTargetWidth(int touchTargetWidth) {
        mTouchTargetWidth = touchTargetWidth;

        int eightDp = RecyclerFastScrollerUtils.convertDpToPx(getContext(), 8);
        mBarInset = mTouchTargetWidth - eightDp;

        int fortyEightDp = RecyclerFastScrollerUtils.convertDpToPx(getContext(), 48);
        if (mTouchTargetWidth > fortyEightDp) {
            throw new RuntimeException("Touch target width cannot be larger than 48dp!");
        }

        mBar.setLayoutParams(new LayoutParams(touchTargetWidth, ViewGroup.LayoutParams.MATCH_PARENT, GravityCompat.END));
        mHandle.setLayoutParams(new LayoutParams(touchTargetWidth, ViewGroup.LayoutParams.MATCH_PARENT, GravityCompat.END));

        updateHandleColorsAndInset();
        updateBarColorAndInset();
    }

    public boolean isHidingEnabled() {
        return mHidingEnabled;
    }

    /**
     * @param hidingEnabled
     *         whether hiding is enabled
     */
    public void setHidingEnabled(boolean hidingEnabled) {
        mHidingEnabled = hidingEnabled;
        if (hidingEnabled) {
            postAutoHide();
        }
    }

    private void updateHandleColorsAndInset() {
        StateListDrawable drawable = new StateListDrawable();

        if (!RecyclerFastScrollerUtils.isRTL(getContext())) {
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
        RecyclerFastScrollerUtils.setViewBackground(mHandle, drawable);
    }

    private void updateBarColorAndInset() {
        Drawable drawable;

        if (!RecyclerFastScrollerUtils.isRTL(getContext())) {
            drawable = new InsetDrawable(new ColorDrawable(mBarColor), mBarInset, 0, 0, 0);
        } else {
            drawable = new InsetDrawable(new ColorDrawable(mBarColor), 0, 0, mBarInset, 0);
        }
        drawable.setAlpha(57);
        RecyclerFastScrollerUtils.setViewBackground(mBar, drawable);
    }

    public void attachRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerFastScroller.this.show(true);
            }
        });
        if (recyclerView.getAdapter() != null) attachAdapter(recyclerView.getAdapter());
    }

    public void attachAdapter(@Nullable RecyclerView.Adapter adapter) {
        if (mAdapter == adapter) return;
        if (mAdapter != null) {
            mAdapter.unregisterAdapterDataObserver(mAdapterObserver);
        }
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mAdapterObserver);
        }
        mAdapter = adapter;
    }

    public void attachAppBarLayout(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout) {
        mCoordinatorLayout = coordinatorLayout;
        mAppBarLayout = appBarLayout;

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                show(true);

                MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
                layoutParams.topMargin = mAppBarLayout.getHeight() + verticalOffset; //AppBarLayout actual height

                mAppBarLayoutOffset = -verticalOffset;

                setLayoutParams(layoutParams);
            }
        });
    }

    public void setOnHandleTouchListener(OnTouchListener listener) {
        mOnTouchListener = listener;
    }

    /**
     * Show the fast scroller and hide after delay
     *
     * @param animate
     *         whether to animate showing the scroller
     */
    public void show(final boolean animate) {
        requestLayout();

        post(new Runnable() {
            @Override
            public void run() {
                if (mHideOverride) {
                    return;
                }

                mHandle.setEnabled(true);
                if (animate) {
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
                } else {
                    setTranslationX(0);
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

        int scrollOffset = mRecyclerView.computeVerticalScrollOffset() + mAppBarLayoutOffset;
        int verticalScrollRange = mRecyclerView.computeVerticalScrollRange() + (mAppBarLayout == null ? 0 : mAppBarLayout.getTotalScrollRange())
                + mRecyclerView.getPaddingBottom();

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

    void updateRvScroll(int dY) {
        if (mRecyclerView != null && mHandle != null) {
            try {
                mRecyclerView.scrollBy(0, dY);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}