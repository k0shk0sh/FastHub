package com.jaychang.srv.decoration;

import android.view.View;

import androidx.annotation.NonNull;

public abstract class SimpleSectionHeaderProvider<T> implements SectionHeaderProvider<T> {

  @NonNull
  public abstract View getSectionHeaderView(@NonNull T item, int position);

  public abstract boolean isSameSection(@NonNull T item, @NonNull T nextItem);

  @Override
  public boolean isSticky() {
    return false;
  }

  @Override
  public int getSectionHeaderMarginTop(@NonNull T item, int position) {
    return 0;
  }

}
