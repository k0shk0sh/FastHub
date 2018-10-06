package com.jaychang.srv.behavior;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

public class StartSnapHelper extends LinearSnapHelper {

  private OrientationHelper verticalHelper;
  private OrientationHelper horizontalHelper;
  private int cellSpacing;

  public StartSnapHelper(int cellSpacing) {
    this.cellSpacing = cellSpacing;
  }

  @Override
  public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                            @NonNull View targetView) {
    int[] out = new int[2];

    if (layoutManager.canScrollHorizontally()) {
      out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager));
    } else {
      out[0] = 0;
    }

    if (layoutManager.canScrollVertically()) {
      out[1] = distanceToStart(targetView, getVerticalHelper(layoutManager));
    } else {
      out[1] = 0;
    }

    return out;
  }

  private int distanceToStart(View targetView, OrientationHelper helper) {
    return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding() - cellSpacing / 2;
  }

  @Override
  public View findSnapView(RecyclerView.LayoutManager layoutManager) {
    if (!(layoutManager instanceof LinearLayoutManager)) {
      return super.findSnapView(layoutManager);
    }

    if (layoutManager.canScrollHorizontally()) {
      return getStartView(layoutManager, getHorizontalHelper(layoutManager));
    } else {
      return getStartView(layoutManager, getVerticalHelper(layoutManager));
    }
  }

  private View getStartView(RecyclerView.LayoutManager layoutManager,
                            OrientationHelper helper) {
    if (!(layoutManager instanceof LinearLayoutManager)) {
      return super.findSnapView(layoutManager);
    }

    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;

    int firstChild = linearLayoutManager.findFirstVisibleItemPosition();

    boolean isLastItem = linearLayoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1;

    if (firstChild == RecyclerView.NO_POSITION || isLastItem) {
      return null;
    }

    View child = layoutManager.findViewByPosition(firstChild);

    if (helper.getDecoratedEnd(child) >= helper.getDecoratedMeasurement(child) / 2
      && helper.getDecoratedEnd(child) > 0) {
      return child;
    } else {
      return layoutManager.findViewByPosition(firstChild + 1);
    }
  }

  private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager layoutManager) {
    if (verticalHelper == null) {
      verticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
    }
    return verticalHelper;
  }

  private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
    if (horizontalHelper == null) {
      horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
    }
    return horizontalHelper;
  }

}