/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jaychang.srv.decoration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import com.jaychang.srv.SimpleAdapter;
import com.jaychang.srv.SimpleRecyclerView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
  public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
  public static final int VERTICAL = LinearLayout.VERTICAL;

  private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

  private Drawable mDivider;

  private int mOrientation;

  private boolean isShowLastDivider;

  private final Rect mBounds = new Rect();

  private List<String> noDividerCellTypes;
  private RecyclerView.LayoutManager layoutManager;
  private LinearLayoutManager linearLayoutManager;
  private GridLayoutManager gridLayoutManager;

  public DividerItemDecoration(Context context, int orientation) {
    final TypedArray a = context.obtainStyledAttributes(ATTRS);
    mDivider = a.getDrawable(0);
    a.recycle();
    setOrientation(orientation);
  }

  public void setOrientation(int orientation) {
    if (orientation != HORIZONTAL && orientation != VERTICAL) {
      throw new IllegalArgumentException(
        "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
    }
    mOrientation = orientation;
  }

  public void setShowLastDivider(boolean isShowLastDivider) {
    this.isShowLastDivider = isShowLastDivider;
  }

  public void setDrawable(@NonNull Drawable drawable) {
    if (drawable == null) {
      throw new IllegalArgumentException("Drawable cannot be null.");
    }
    mDivider = drawable;
  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    if (parent.getLayoutManager() == null) {
      return;
    }

    if (mOrientation == VERTICAL) {
      drawHorizontalDivider(c, parent);
    } else {
      drawVerticalDivider(c, parent);
    }
  }

  @SuppressLint("NewApi")
  private void drawHorizontalDivider(Canvas canvas, RecyclerView parent) {
    canvas.save();
    final int left;
    final int right;
    if (parent.getClipToPadding()) {
      left = parent.getPaddingLeft();
      right = parent.getWidth() - parent.getPaddingRight();
      canvas.clipRect(left, parent.getPaddingTop(), right,
        parent.getHeight() - parent.getPaddingBottom());
    } else {
      left = 0;
      right = parent.getWidth();
    }

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);

      if (ignoreDrawDividerForCellTypes(parent, i)) {
        continue;
      }

      if (isLastRow(parent, child) && !isShowLastDivider) {
        continue;
      }

      parent.getDecoratedBoundsWithMargins(child, mBounds);
      final int bottom = mBounds.bottom;
      final int top = bottom - mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(canvas);
    }
    canvas.restore();
  }

  @SuppressLint("NewApi")
  private void drawVerticalDivider(Canvas canvas, RecyclerView parent) {
    canvas.save();
    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);

      if (ignoreDrawDividerForCellTypes(parent, i)) {
        continue;
      }

      if (isLastColumn(parent, child) && !isShowLastDivider) {
        continue;
      }

      if (isLastColumn(parent, child) && isGridMode(parent)) {
        continue;
      }

      parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
      final int right = mBounds.right;
      final int left = right - mDivider.getIntrinsicWidth();
      final int bottom = child.getBottom();
      final int top = child.getTop();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(canvas);
    }
    canvas.restore();
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                             RecyclerView.State state) {
    if (mOrientation == VERTICAL) {
      if (isLastRow(parent, view) && !isShowLastDivider) {
        outRect.set(0, 0, 0, 0);
      } else {
        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
      }
    } else {
      if ((isLastColumn(parent, view) && isGridMode(parent)) ||
        (isLastColumn(parent, view) && !isShowLastDivider)) {
        outRect.set(0, 0, 0, 0);
      } else {
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
      }
    }
  }

  private boolean ignoreDrawDividerForCellTypes(RecyclerView parent, int position) {
    if (noDividerCellTypes == null) {
      noDividerCellTypes = ((SimpleRecyclerView) parent).getNoDividerCellTypes();
    }

    if (noDividerCellTypes.size() <= 0) {
      return false;
    }

    if (isLinearMode(parent)) {
      int pos = getLinearLayoutManager().findFirstVisibleItemPosition();

      if (pos == -1) {
        return false;
      }

      String type = ((SimpleAdapter) parent.getAdapter()).getCell(pos + position).getClass().getSimpleName();
      return noDividerCellTypes.contains(type);
    }

    return false;
  }

  private boolean isLastColumn(RecyclerView parent, View view) {
    int position = parent.getChildAdapterPosition(view);
    int totalChildCount = parent.getAdapter().getItemCount();

    boolean isLastColumn = false;
    if (isGridMode(parent)) {
      int spanCount = getGridLayoutManager().getSpanCount();
      int spanIndex = getGridLayoutManager().getSpanSizeLookup().getSpanIndex(position, spanCount);
      int spanSize = getGridLayoutManager().getSpanSizeLookup().getSpanSize(position);
      isLastColumn = spanIndex == spanCount - spanSize;
    } else if (isLinearMode(parent)) {
      isLastColumn = position == totalChildCount - 1;
    }

    return isLastColumn;
  }

  private boolean isLastRow(RecyclerView parent, View view) {
    int position = parent.getChildAdapterPosition(view);
    int totalChildCount = parent.getAdapter().getItemCount();

    boolean isLastRow = false;
    if (isGridMode(parent)) {
      int spanCount = getGridLayoutManager().getSpanCount();
      int spanIndex = getGridLayoutManager().getSpanSizeLookup().getSpanIndex(position, spanCount);
      int spanSize = getGridLayoutManager().getSpanSizeLookup().getSpanSize(position);
      int column = (spanIndex + spanSize) / spanSize - 1;
      // check if next row first item's index is the last index
      if (spanSize == 1) {
        isLastRow = position + spanCount - column > totalChildCount - 1;
      } else {
        int maxColumns = totalChildCount - position + column;
        int columns = spanCount / spanSize > maxColumns ? maxColumns : spanCount / spanSize;
        isLastRow = position + columns - column > totalChildCount - 1;
      }
    } else if (isLinearMode(parent)) {
      isLastRow = position == totalChildCount - 1;
    }

    return isLastRow;
  }

  private boolean isGridMode(RecyclerView parent) {
    if (layoutManager == null) {
      layoutManager = parent.getLayoutManager();
    }

    return layoutManager instanceof GridLayoutManager;
  }

  private boolean isLinearMode(RecyclerView parent) {
    if (layoutManager == null) {
      layoutManager = parent.getLayoutManager();
    }

    return layoutManager instanceof LinearLayoutManager;
  }

  public GridLayoutManager getGridLayoutManager() {
    if (gridLayoutManager == null) {
      gridLayoutManager = (GridLayoutManager) layoutManager;
    }

    return gridLayoutManager;
  }

  public LinearLayoutManager getLinearLayoutManager() {
    if (linearLayoutManager == null) {
      linearLayoutManager = (LinearLayoutManager) layoutManager;
    }

    return linearLayoutManager;
  }
}

