package com.jaychang.srv.behavior;

import java.util.HashSet;
import java.util.Set;

public class SwipeToDismissOptions {

  private Set<SwipeDirection> swipeDirections = new HashSet<>();
  private boolean enableDefaultFadeOutEffect = true;
  private SwipeToDismissCallback swipeToDismissCallback;

  public boolean isDefaultFadeOutEffectEnabled() {
    return enableDefaultFadeOutEffect;
  }

  public void setEnableDefaultFadeOutEffect(boolean enableDefaultFadeOutEffect) {
    this.enableDefaultFadeOutEffect = enableDefaultFadeOutEffect;
  }

  public SwipeToDismissCallback getSwipeToDismissCallback() {
    return swipeToDismissCallback;
  }

  public void setSwipeToDismissCallback(SwipeToDismissCallback swipeToDismissCallback) {
    this.swipeToDismissCallback = swipeToDismissCallback;
  }

  public void setSwipeDirections(Set<SwipeDirection> swipeDirections) {
    this.swipeDirections = new HashSet<>(swipeDirections);
  }

  public boolean canSwipeLeft() {
    return swipeDirections.contains(SwipeDirection.LEFT);
  }


  public boolean canSwipeRight() {
    return swipeDirections.contains(SwipeDirection.RIGHT);
  }

  public boolean canSwipeUp() {
    return swipeDirections.contains(SwipeDirection.UP);
  }

  public boolean canSwipeDown() {
    return swipeDirections.contains(SwipeDirection.DOWN);
  }

}
