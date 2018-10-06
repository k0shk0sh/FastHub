package com.jaychang.srv;


import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

@SuppressWarnings("unchecked")
class SimpleDiffCallbackDelegate extends DiffUtil.Callback {

  private List<SimpleCell> newList = new ArrayList<>();
  private List<SimpleCell> oldList = new ArrayList<>();

  public SimpleDiffCallbackDelegate(SimpleAdapter adapter, List<? extends SimpleCell> newCells) {
    this.oldList.addAll(adapter.getAllCells());
    this.newList.addAll(oldList);
    insertOrUpdateNewList(newCells);
    adapter.setCells(newList);
  }

  private void insertOrUpdateNewList(List<? extends SimpleCell> newCells) {
    for (SimpleCell newCell : newCells) {
      int index = indexOf(newList, newCell);
      if (index != -1) {
        newList.set(index, newCell);
      } else {
        newList.add(newCell);
      }
    }
  }

  private int indexOf(List<? extends SimpleCell> cells, SimpleCell cell) {
    for (SimpleCell c : cells) {
      if (c.getItemId() == cell.getItemId()) {
        return cells.indexOf(c);
      }
    }
    return -1;
  }

  @Override
  public int getOldListSize() {
    return oldList.size();
  }

  @Override
  public int getNewListSize() {
    return newList.size();
  }

  @Override
  public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
    return oldList.get(oldItemPosition).getItemId() == newList.get(newItemPosition).getItemId();
  }

  public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
    return ((Updatable) oldList.get(oldItemPosition)).areContentsTheSame(newList.get(newItemPosition).getItem());
  }

  public Object getChangePayload(int oldItemPosition, int newItemPosition) {
    return ((Updatable) oldList.get(oldItemPosition)).getChangePayload(newList.get(newItemPosition).getItem());
  }

}
