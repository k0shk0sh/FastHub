package com.jaychang.srv;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

public abstract class SimpleCell<T, VH extends SimpleViewHolder> {

  public interface OnCellClickListener<T> {
    void onCellClicked(@NonNull T item);
  }

  public interface OnCellLongClickListener<T> {
    void onCellLongClicked(@NonNull T item);
  }

  private int spanSize = 1;
  private T item;
  private OnCellClickListener onCellClickListener;
  private OnCellLongClickListener onCellLongClickListener;

  public SimpleCell(@NonNull T item) {
    this.item = item;
  }

  @LayoutRes protected abstract int getLayoutRes();

  @NonNull protected abstract VH onCreateViewHolder(@NonNull ViewGroup parent, @NonNull View cellView);

  protected abstract void onBindViewHolder(@NonNull VH holder, int position, @NonNull Context context, Object payload);

  protected void onUnbindViewHolder(@NonNull VH holder) {
  }

  @NonNull public T getItem() {
    return item;
  }

  public void setItem(@NonNull T item) {
    this.item = item;
  }

  protected long getItemId() {
    return item.hashCode();
  }

  public int getSpanSize() {
    return spanSize;
  }

  public void setSpanSize(int spanSize) {
    this.spanSize = spanSize;
  }

  public void setOnCellClickListener(@NonNull OnCellClickListener<T> onCellClickListener) {
    this.onCellClickListener = onCellClickListener;
  }

  public void setOnCellLongClickListener(@NonNull OnCellLongClickListener<T> onCellLongClickListener) {
    this.onCellLongClickListener = onCellLongClickListener;
  }

  public OnCellClickListener<T> getOnCellClickListener() {
    return onCellClickListener;
  }

  public OnCellLongClickListener<T> getOnCellLongClickListener() {
    return onCellLongClickListener;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SimpleCell<?, ?> cell = (SimpleCell<?, ?>) o;

    return getItemId() == cell.getItemId();

  }

  @Override
  public int hashCode() {
    return (int) (getItemId() ^ (getItemId() >>> 32));
  }

}
