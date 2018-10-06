package com.jaychang.srv;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleViewHolder extends RecyclerView.ViewHolder {

  private SimpleCell cell;

  public SimpleViewHolder(@NonNull View itemView) {
    super(itemView);
  }

  void bind(SimpleCell cell) {
    this.cell = cell;
  }

  void unbind() {
    cell = null;
  }

  SimpleCell getCell() {
    return cell;
  }

}
