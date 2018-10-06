package com.jaychang.srv.behavior;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.graphics.Canvas;
import android.os.Build;

import com.jaychang.srv.R;
import com.jaychang.srv.SimpleRecyclerView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.LinearLayoutManager.VERTICAL;
import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

@SuppressWarnings("unchecked")
class DragAndDropItemCallback extends ItemTouchHelper.Callback {

  private OnItemMoveListener callback;
  private DragAndDropOptions options;
  private SimpleRecyclerView simpleRecyclerView;
  private int fromPosition = -1;
  private int toPosition;
  private Object draggingItem;
  private int draggingItemPosition;
  private DragAndDropCallback dragAndDropCallback;
  private StateListAnimator animator;

  DragAndDropItemCallback(OnItemMoveListener callback,
                          DragAndDropOptions options) {
    this.callback = callback;
    this.options = options;
    this.dragAndDropCallback = options.getDragAndDropCallback();
  }

  @Override
  public boolean isLongPressDragEnabled() {
    return options.canLongPressToDrag();
  }

  @Override
  public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    // no op
  }

  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    if (simpleRecyclerView == null) {
      simpleRecyclerView = (SimpleRecyclerView) recyclerView;
    }

    // obtain current dragging item
    draggingItemPosition = viewHolder.getAdapterPosition();
    if (draggingItemPosition != NO_POSITION) {
      draggingItem = simpleRecyclerView.getCell(draggingItemPosition).getItem();
    }

    // setup flags
    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

    if (layoutManager instanceof GridLayoutManager) {
      int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END;
      return makeMovementFlags(dragFlags, 0);
    } else if (layoutManager instanceof LinearLayoutManager) {
      int dragFlags;
      int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
      if (orientation == VERTICAL) {
        dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
      } else {
        dragFlags = ItemTouchHelper.START | ItemTouchHelper.END;
      }
      return makeMovementFlags(dragFlags, 0);
    } else {
      return 0;
    }
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
    if (!target.getClass().equals(source.getClass())) {
      return false;
    }

    if (fromPosition == -1) {
      fromPosition = source.getAdapterPosition();
    }

    toPosition = target.getAdapterPosition();

    if (dragAndDropCallback != null) {
      dragAndDropCallback.onCellMoved((SimpleRecyclerView) recyclerView, source.itemView, draggingItem, source.getAdapterPosition(), target.getAdapterPosition());
    }

    callback.onItemMoved(source.getAdapterPosition(), target.getAdapterPosition());

    return true;
  }

  @Override
  public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
    super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
  }

  @Override
  public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    if (actionState != ItemTouchHelper.ACTION_STATE_DRAG) {
      super.onSelectedChanged(viewHolder, actionState);
      return;
    }

    if (dragAndDropCallback != null) {
      dragAndDropCallback.onCellDragStarted(simpleRecyclerView, viewHolder.itemView, draggingItem, draggingItemPosition);
    }

    if (options.isDefaultEffectEnabled()) {
      viewHolder.itemView.setSelected(true);
      viewHolder.itemView.setAlpha(0.95f);
      if (Build.VERSION.SDK_INT >= 21 && animator == null) {
        animator = AnimatorInflater.loadStateListAnimator(viewHolder.itemView.getContext(), R.animator.raise);
        viewHolder.itemView.setStateListAnimator(animator);
      }
    }

    super.onSelectedChanged(viewHolder, actionState);
  }

  @Override
  public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);

    if (dragAndDropCallback != null) {
      if (fromPosition == -1) {
        dragAndDropCallback.onCellDragCancelled((SimpleRecyclerView) recyclerView, viewHolder.itemView, draggingItem, draggingItemPosition);
      } else {
        dragAndDropCallback.onCellDropped((SimpleRecyclerView) recyclerView, viewHolder.itemView, draggingItem, fromPosition, toPosition);
      }
    }

    // reset after drag action is settled
    if (options.isDefaultEffectEnabled()) {
      viewHolder.itemView.setAlpha(1f);
      viewHolder.itemView.setSelected(false);
    }

    fromPosition = -1;
  }

}
