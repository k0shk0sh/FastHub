package com.jaychang.srv.behavior;

import android.graphics.Canvas;
import android.view.View;

import com.jaychang.srv.SimpleRecyclerView;

import androidx.annotation.NonNull;

public abstract class SwipeToDismissCallback<T> {

  public boolean enableDefaultFadeOutEffect() {
    return true;
  }

  public void onCellSwiping(@NonNull SimpleRecyclerView simpleRecyclerView, @NonNull View itemView, @NonNull T item, int position, @NonNull Canvas canvas, float dX, float dY, boolean isControlledByUser) {
  }

  public void onCellSettled(@NonNull SimpleRecyclerView simpleRecyclerView, @NonNull View itemView, @NonNull T item, int position) {
  }

  public void onCellDismissed(@NonNull SimpleRecyclerView simpleRecyclerView, @NonNull T item, int position) {
  }

}
