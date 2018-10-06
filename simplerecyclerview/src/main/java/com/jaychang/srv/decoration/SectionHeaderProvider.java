package com.jaychang.srv.decoration;

import androidx.annotation.NonNull;
import android.view.View;

public interface SectionHeaderProvider<T> {
  @NonNull View getSectionHeaderView(@NonNull T item, int position);
  boolean isSameSection(@NonNull T item, @NonNull T nextItem);
  boolean isSticky();
  int getSectionHeaderMarginTop(@NonNull T item, int position);
}
