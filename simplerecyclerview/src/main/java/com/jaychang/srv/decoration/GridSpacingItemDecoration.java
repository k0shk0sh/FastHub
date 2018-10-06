package com.jaychang.srv.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

  private boolean includeEdge;
  private int horizontalSpacing;
  private int verticalSpacing;
  private GridLayoutManager gridLayoutManager;

  private GridSpacingItemDecoration(Builder builder) {
    includeEdge = builder.includeEdge;
    int spacing = builder.spacing;
    if (spacing != 0) {
      horizontalSpacing = spacing;
      verticalSpacing = spacing;
    } else {
      horizontalSpacing = builder.horizontalSpacing;
      verticalSpacing = builder.verticalSpacing;
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    if (gridLayoutManager == null) {
      gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
    }

    int spanCount = gridLayoutManager.getSpanCount();
    int position = parent.getChildAdapterPosition(view);
    int spanSize = gridLayoutManager.getSpanSizeLookup().getSpanSize(position);
    int column = gridLayoutManager.getSpanSizeLookup().getSpanIndex(position, spanCount);
    int totalChildCount = parent.getAdapter().getItemCount();
    boolean isLastRow = spanSize == 1 ?
      position + spanCount - column > totalChildCount - 1 :
      position - column / spanSize > totalChildCount - 1;
    boolean isFirstRow = gridLayoutManager.getSpanSizeLookup().getSpanGroupIndex(position, spanCount) == 0;

    if (includeEdge) {
      outRect.left = horizontalSpacing - column * horizontalSpacing / spanCount;
      outRect.right = (column + spanSize) * horizontalSpacing / spanCount;
      outRect.top = verticalSpacing;
      outRect.bottom = isLastRow ? verticalSpacing : 0;
    } else {
      outRect.left = column * horizontalSpacing / spanCount;
      outRect.right = horizontalSpacing - (column + spanSize) * horizontalSpacing / spanCount;
      outRect.top = isFirstRow ? 0 : verticalSpacing;
    }
  }

  public static final class Builder {
    private boolean includeEdge;
    private int spacing = 0;
    private int verticalSpacing;
    private int horizontalSpacing;

    private Builder() {
    }

    public Builder includeEdge(boolean includeEdge) {
      this.includeEdge = includeEdge;
      return this;
    }

    public Builder spacing(int spacing) {
      this.spacing = spacing;
      return this;
    }

    public Builder verticalSpacing(int verticalSpacing) {
      this.verticalSpacing = verticalSpacing;
      return this;
    }

    public Builder horizontalSpacing(int horizontalSpacing) {
      this.horizontalSpacing = horizontalSpacing;
      return this;
    }

    public GridSpacingItemDecoration build() {
      return new GridSpacingItemDecoration(this);
    }
  }

}