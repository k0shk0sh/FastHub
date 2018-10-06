package com.jaychang.srv.behavior;

public class DragAndDropOptions {

  private boolean canLongPressToDrag = true;
  private int dragHandleId;
  private DragAndDropCallback dragAndDropCallback;
  private boolean enableDefaultEffect = true;

  public void setCanLongPressToDrag(boolean canLongPressToDrag) {
    this.canLongPressToDrag = canLongPressToDrag;
  }

  public boolean canLongPressToDrag() {
    return canLongPressToDrag;
  }

  public int getDragHandleId() {
    return dragHandleId;
  }

  public void setDragHandleId(int dragHandleId) {
    this.dragHandleId = dragHandleId;
  }

  public DragAndDropCallback getDragAndDropCallback() {
    return dragAndDropCallback;
  }

  public void setDragAndDropCallback(DragAndDropCallback dragAndDropCallback) {
    this.dragAndDropCallback = dragAndDropCallback;
  }

  public boolean isDefaultEffectEnabled() {
    return enableDefaultEffect;
  }

  public void setEnableDefaultEffect(boolean enableDefaultEffect) {
    this.enableDefaultEffect = enableDefaultEffect;
  }

}
