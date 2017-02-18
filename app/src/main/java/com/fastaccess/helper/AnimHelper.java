package com.fastaccess.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.Arrays;
import java.util.List;


/**
 * Created by Kosh on 27 May 2016, 9:04 PM
 */

public class AnimHelper {

    interface AnimationCallback {
        void onAnimationEnd();

        void onAnimationStart();
    }

    private static final Interpolator interpolator = new LinearInterpolator();

    @UiThread public static void animateVisibility(@Nullable final View view, final boolean show) {
        animateVisibility(view, show, null);
    }

    @SuppressWarnings("WeakerAccess") @UiThread
    public static void animateVisibility(@Nullable final View view, final boolean show, @Nullable final AnimationCallback callback) {
        if (view == null) {
            return;
        }
        if (!ViewCompat.isAttachedToWindow(view)) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    animateSafeVisibility(show, view, callback);
                    return true;
                }
            });
        } else {
            animateSafeVisibility(show, view, callback);
        }
    }

    @UiThread private static void animateSafeVisibility(final boolean show, @NonNull final View view, @Nullable final AnimationCallback callback) {
        view.clearAnimation();
        if (view.getAnimation() != null) view.getAnimation().cancel();
        ViewPropertyAnimator animator = view.animate().setDuration(200).alpha(show ? 1F : 0F).setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (callback != null) callback.onAnimationStart();
                        if (show) {
                            view.setScaleX(1);
                            view.setScaleY(1);
                            view.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override public void onAnimationEnd(@NonNull Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!show) {
                            view.setVisibility(View.GONE);
                            view.setScaleX(0);
                            view.setScaleY(0);
                        }
                        if (callback != null) callback.onAnimationEnd();
                        animation.removeListener(this);
                        view.clearAnimation();
                    }
                });
        animator.scaleX(show ? 1 : 0).scaleY(show ? 1 : 0);
    }

    @UiThread @NonNull private static List<ObjectAnimator> getBeats(@NonNull View view) {
        ObjectAnimator[] animator = new ObjectAnimator[]{
                ObjectAnimator.ofFloat(view, "scaleY", 1, 1.1f, 1),
                ObjectAnimator.ofFloat(view, "scaleX", 1, 1.1f, 1)
        };
        return Arrays.asList(animator);
    }

    @UiThread public static void startBeatsAnimation(@NonNull View view) {
        view.clearAnimation();
        if (view.getAnimation() != null) {
            view.getAnimation().cancel();
        }
        List<ObjectAnimator> animators = getBeats(view);
        for (ObjectAnimator anim : animators) {
            anim.setDuration(300).start();
            anim.setInterpolator(interpolator);
        }
    }

    @UiThread public static void circularReveal(final View mRevealView, final View from, final boolean show) {
        if (ViewCompat.isAttachedToWindow(mRevealView)) {
            if (show) {
                if (mRevealView.isShown()) return;
            } else {
                if (!mRevealView.isShown()) {
                    return;
                }
            }
            reveal(mRevealView, show, from);
        } else {
            mRevealView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override public boolean onPreDraw() {
                    mRevealView.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (show) {
                        if (mRevealView.isShown()) return true;
                    } else {
                        if (!mRevealView.isShown()) {
                            return true;
                        }
                    }
                    reveal(mRevealView, show, from);
                    return true;
                }
            });
        }
    }

    @UiThread private static void reveal(final View mRevealView, final boolean show, View from) {
        Rect rect = ViewHelper.getLayoutPosition(from);
        int x = (int) rect.exactCenterX();
        int y = (int) rect.exactCenterY();
        Animator animator = ViewAnimationUtils.createCircularReveal(mRevealView, x, y, 0, Math.max(rect.width(), rect.height()));
        animator.setDuration(400L);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        mRevealView.setVisibility(View.VISIBLE);
        if (!show) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mRevealView.setVisibility(View.GONE);
                    animation.removeListener(this);
                }
            });
            animator.start();
        }
    }

}
