package com.jaychang.srv.behavior;

import android.graphics.Canvas;

import com.jaychang.srv.SimpleRecyclerView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

@SuppressWarnings("unchecked")
class SwipeToDismissItemCallback extends ItemTouchHelper.Callback {

  private OnItemDismissListener callback;
  private SwipeToDismissOptions options;
  private SimpleRecyclerView simpleRecyclerView;
  private Object swipeItem;
  private int swipeItemPosition;
  private boolean isItemSettled;
  private boolean isItemSwiped;

  SwipeToDismissItemCallback(OnItemDismissListener callback,
                             SwipeToDismissOptions options) {
    this.callback = callback;
    this.options = options;
  }

  @Override
  public boolean isItemViewSwipeEnabled() {
    return true;
  }

  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    if (simpleRecyclerView == null) {
      simpleRecyclerView = (SimpleRecyclerView) recyclerView;
    }

    // obtain current item
    swipeItemPosition = viewHolder.getAdapterPosition();
    if (swipeItemPosition != NO_POSITION) {
      swipeItem = simpleRecyclerView.getCell(swipeItemPosition).getItem();
    }

    // reset
    isItemSettled = false;
    isItemSwiped = false;

    int swipeFlags = 0;

    if (options.canSwipeLeft()) {
      swipeFlags = swipeFlags | ItemTouchHelper.START;
    }
    if (options.canSwipeRight()) {
      swipeFlags = swipeFlags | ItemTouchHelper.END;
    }
    if (options.canSwipeUp()) {
      swipeFlags = swipeFlags | ItemTouchHelper.UP;
    }
    if (options.canSwipeDown()) {
      swipeFlags = swipeFlags | ItemTouchHelper.DOWN;
    }

    return makeMovementFlags(0, swipeFlags);
  }

  @Override
  public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    isItemSwiped = true;
    callback.onItemDismissed(viewHolder.getAdapterPosition());
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
    // no-op
    return false;
  }

  @Override
  public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isControlledByUser) {
    super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isControlledByUser);

    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
      if (options.getSwipeToDismissCallback() != null && !isItemSettled) {
        options.getSwipeToDismissCallback().onCellSwiping(simpleRecyclerView, viewHolder.itemView, swipeItem, swipeItemPosition, canvas, dX, dY, isControlledByUser);
      }

      if (options.isDefaultFadeOutEffectEnabled()) {
        boolean isHorizontal = dX != 0;
        float offset = isHorizontal ? dX : dY;
        float dimension = isHorizontal ? viewHolder.itemView.getWidth() : viewHolder.itemView.getHeight();
        final float alpha = 1.0f - Math.abs(offset) / dimension;
        viewHolder.itemView.setAlpha(alpha);
      }

      if (options.canSwipeLeft() | options.canSwipeRight()) {
        viewHolder.itemView.setTranslationX(dX);
      } else {
        viewHolder.itemView.setTranslationY(dY);
      }
    }
  }

  @Override
  public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    super.onSelectedChanged(viewHolder, actionState);
  }

  @Override
  public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);

    isItemSettled = true;

    if (options.getSwipeToDismissCallback() != null) {
      if (isItemSwiped) {
        options.getSwipeToDismissCallback().onCellDismissed(simpleRecyclerView, swipeItem, swipeItemPosition);
      } else {
        options.getSwipeToDismissCallback().onCellSettled(simpleRecyclerView, viewHolder.itemView, swipeItem, swipeItemPosition);
      }
    }

    if (options.isDefaultFadeOutEffectEnabled()) {
      viewHolder.itemView.setAlpha(1.0f);
    }
  }

}
