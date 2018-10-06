package com.jaychang.srv.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LinearSpacingItemDecoration extends RecyclerView.ItemDecoration {

  private int spacing;
  private boolean includeEdge;
  private boolean isVertical;

  private LinearSpacingItemDecoration(Builder builder) {
    spacing = builder.spacing;
    includeEdge = builder.includeEdge;
    isVertical = builder.orientation == LinearLayoutManager.VERTICAL;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public void getItemOffsets(Rect outRect, View view,
                             RecyclerView parent, RecyclerView.State state) {
    int position = parent.getChildAdapterPosition(view);
    boolean isFirstCell = position == 0;
    boolean isLastCell = position == parent.getAdapter().getItemCount() - 1;

    if (isFirstCell) {
      if (isVertical) {
        outRect.top = includeEdge ? spacing : 0;
        outRect.bottom = isLastCell ? (includeEdge ? spacing : 0) : 0;
      } else {
        outRect.left = includeEdge ? spacing : 0;
        outRect.right = spacing / 2;
      }
    } else if (isLastCell) {
      if (isVertical) {
        outRect.top = spacing;
        outRect.bottom = includeEdge ? spacing : 0;
      } else {
        outRect.left = spacing / 2;
        outRect.right = includeEdge ? spacing : 0;
      }
    } else {
      if (isVertical) {
        outRect.top = spacing;
        outRect.bottom = 0;
      } else {
        outRect.left = spacing / 2;
        outRect.right = spacing / 2;
      }
    }
  }

  public static final class Builder {
    private int spacing;
    private boolean includeEdge;
    private int orientation;

    private Builder() {
    }

    public Builder spacing(int val) {
      spacing = val;
      return this;
    }

    public Builder includeEdge(boolean includeEdge) {
      this.includeEdge = includeEdge;
      return this;
    }

    public Builder orientation(int orientation) {
      this.orientation = orientation;
      return this;
    }

    public LinearSpacingItemDecoration build() {
      return new LinearSpacingItemDecoration(this);
    }
  }

}