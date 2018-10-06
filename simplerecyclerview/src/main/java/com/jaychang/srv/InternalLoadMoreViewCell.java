package com.jaychang.srv;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

class InternalLoadMoreViewCell extends SimpleCell<View, SimpleViewHolder> {

  InternalLoadMoreViewCell(View view) {
    super(view);
  }

  @Override
  public int getLayoutRes() {
    return R.layout.interval_view;
  }

  @NonNull
  @Override
  public SimpleViewHolder onCreateViewHolder(ViewGroup parent, View cellView) {
    return new SimpleViewHolder(getItem());
  }

  @Override
  public void onBindViewHolder(SimpleViewHolder holder, int position, Context context, Object payload) {
  }

  @Override
  protected long getItemId() {
    return getItem().getId();
  }

}
