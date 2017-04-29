package com.fastaccess.ui.widgets;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import it.sephiroth.android.library.bottomnavigation.VerticalScrollingBehavior;

public class TabletBehavior extends VerticalScrollingBehavior<BottomNavigation> {

    public TabletBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLayoutValues(int bottomNavWidth, int topInset, boolean translucentStatus) {}

    public boolean layoutDependsOn(CoordinatorLayout parent, BottomNavigation child, View dependency) {
        return AppBarLayout.class.isInstance(dependency) || Toolbar.class.isInstance(dependency);
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, BottomNavigation child, View dependency) {
        return true;
    }

    public void onDependentViewRemoved(CoordinatorLayout parent, BottomNavigation child, View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
    }

    public boolean onLayoutChild(CoordinatorLayout parent, BottomNavigation child, int layoutDirection) {
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    public void onNestedVerticalOverScroll(CoordinatorLayout coordinatorLayout, BottomNavigation child, int direction, int currentOverScroll, int
            totalOverScroll) {
    }

    public void onDirectionNestedPreScroll(CoordinatorLayout coordinatorLayout, BottomNavigation child, View target, int dx, int dy, int[]
            consumed, int scrollDirection) {
    }

    protected boolean onNestedDirectionFling(CoordinatorLayout coordinatorLayout, BottomNavigation child, View target, float velocityX, float
            velocityY, int scrollDirection) {
        return false;
    }
}