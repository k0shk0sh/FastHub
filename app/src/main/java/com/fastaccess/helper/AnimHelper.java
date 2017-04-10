package com.fastaccess.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.PopupWindow;

import java.util.Arrays;
import java.util.List;


/**
 * Created by Kosh on 27 May 2016, 9:04 PM
 */

public class AnimHelper {

    public interface AnimationCallback {
        void onAnimationEnd();

        void onAnimationStart();
    }

    private static final Interpolator interpolator = new LinearInterpolator();

    @UiThread private static void animateVisibility(@Nullable final View view, final boolean show, int visibility) {
        animateVisibility(view, show, visibility, null);
    }

    @UiThread public static void animateVisibility(@Nullable final View view, final boolean show) {
        animateVisibility(view, show, View.GONE);
    }

    @UiThread private static void animateVisibility(@Nullable final View view, final boolean show, int visibility,
                                                    @Nullable final AnimationCallback callback) {
        if (view == null) {
            return;
        }
        if (!ViewCompat.isAttachedToWindow(view)) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    animateSafeVisibility(show, view, visibility, callback);
                    return true;
                }
            });
        } else {
            animateSafeVisibility(show, view, visibility, callback);
        }
    }

    @UiThread private static void animateSafeVisibility(final boolean show, @NonNull final View view, int visibility,
                                                        @Nullable final AnimationCallback callback) {
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
                            view.setVisibility(visibility);
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

    @UiThread public static void revealPopupWindow(@NonNull PopupWindow popupWindow, @NonNull View from) {
        if (PrefGetter.isPopupAnimationEnabled()) {
            Rect rect = ViewHelper.getLayoutPosition(from);
            int x = (int) rect.exactCenterX();
            int y = (int) rect.exactCenterY();
            if (popupWindow.getContentView() != null) {
                View view = popupWindow.getContentView();
                if (view != null) {
                    popupWindow.showAsDropDown(from);
                    view.post(() -> {
                        Animator animator = ViewAnimationUtils.createCircularReveal(view, x, y, 0,
                                (float) Math.hypot(rect.width(), rect.height()));
                        animator.setDuration(view.getResources().getInteger(android.R.integer.config_shortAnimTime));
                        animator.start();
                    });
                }
            }
        }
    }

    @UiThread public static void revealDialog(@NonNull Dialog dialog, int animDuration) {
        if (PrefGetter.isPopupAnimationEnabled()) {
            if (dialog.getWindow() != null) {
                View view = dialog.getWindow().getDecorView();
                if (view != null) {
                    int centerX = view.getWidth() / 2;
                    int centerY = view.getHeight() / 2;
                    Animator animator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, 20, view.getHeight());
                    animator.setDuration(animDuration);
                    animator.start();
                }
            }
        }
    }

    @UiThread public static void dismissDialog(@NonNull DialogFragment dialogFragment, int duration, AnimatorListenerAdapter listenerAdapter) {
        if (PrefGetter.isPopupAnimationEnabled()) {
            Dialog dialog = dialogFragment.getDialog();
            if (dialog != null) {
                if (dialog.getWindow() != null) {
                    View view = dialog.getWindow().getDecorView();
                    if (view != null) {
                        int centerX = view.getWidth() / 2;
                        int centerY = view.getHeight() / 2;
                        float radius = (float) Math.sqrt(view.getWidth() * view.getWidth() / 4 + view.getHeight() * view.getHeight() / 4);
                        view.post(() -> {
                            if (ViewCompat.isAttachedToWindow(view)) {
                                Animator animator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, radius, 0);
                                animator.setDuration(duration);
                                animator.addListener(listenerAdapter);
                                animator.start();
                            } else {
                                listenerAdapter.onAnimationEnd(null);
                            }
                        });
                    }
                }
            }
        } else {
            listenerAdapter.onAnimationEnd(null);
        }
    }
}
