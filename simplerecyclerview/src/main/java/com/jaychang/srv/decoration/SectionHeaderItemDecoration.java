package com.jaychang.srv.decoration;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import com.jaychang.srv.SimpleRecyclerView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

@SuppressWarnings("unchecked")
public class SectionHeaderItemDecoration extends RecyclerView.ItemDecoration {

  private SectionHeaderProvider provider;
  private SimpleRecyclerView simpleRecyclerView;
  private LinearLayoutManager layoutManager;
  private int sectionHeight;
  private boolean isHeaderOverlapped;
  private int firstHeaderTop;
  private int secondHeaderTop;
  private boolean isClipToPadding;
  private Class clazz;

  public SectionHeaderItemDecoration(Class clazz, SectionHeaderProvider provider) {
    this.clazz = clazz;
    this.provider = provider;
  }

  @SuppressLint("NewApi")
  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    // init
    if (simpleRecyclerView == null) {
      simpleRecyclerView = ((SimpleRecyclerView) parent);
    }
    if (layoutManager == null) {
      layoutManager = (LinearLayoutManager) parent.getLayoutManager();
    }
    isClipToPadding = parent.getClipToPadding();

    int position = parent.getChildAdapterPosition(view);

    if (position == NO_POSITION || !isSectionType(position)) {
      return;
    }

    if (sectionHeight == 0) {
      View sectionHeader = getAndMeasureSectionHeader(parent, position);
      sectionHeight = sectionHeader.getMeasuredHeight();
    }

    if (!isSameSection(position)) {
      outRect.top = sectionHeight + provider.getSectionHeaderMarginTop(getItem(position), position);
    } else {
      outRect.top = 0;
    }
  }

  // draw section header
  @SuppressLint("NewApi")
  @Override
  public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    int topPadding = isClipToPadding ? parent.getPaddingTop() : 0;
    int left = parent.getPaddingLeft();
    int right = parent.getWidth() - parent.getPaddingRight();

    boolean isFirst = false;
    for (int i = 0; i < parent.getChildCount(); i++) {
      View view = parent.getChildAt(i);
      int position = parent.getChildAdapterPosition(view);
      int top = view.getTop() - sectionHeight;

      if (position != NO_POSITION
              && !isSameSection(position)
              && !isItemCoveredBySection(view.getHeight(), top)) {
        if (!isSectionType(position)) {
          continue;
        }
        View sectionHeader = getAndMeasureSectionHeader(parent, position);
        int bottom = view.getTop();
        boolean isHeaderExit = top <= topPadding;

        if (!isFirst) {
          firstHeaderTop = top;
        }
        if (!isFirst && position != 0) {
          secondHeaderTop = top;
          if (isHeaderExit) {
            isHeaderOverlapped = false;
          } else {
            isHeaderOverlapped = secondHeaderTop <= sectionHeight + topPadding;
          }
        }
        isFirst = true;

        sectionHeader.layout(left, top, right, bottom);
        canvas.save();
        if (isClipToPadding && isHeaderExit) {
          canvas.clipRect(left, topPadding, right, bottom);
        }
        canvas.translate(left, top);
        sectionHeader.draw(canvas);
        canvas.restore();
      }
    }
  }

  // draw sticky section header
  @SuppressLint("NewApi")
  @Override
  public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    if (!provider.isSticky()) {
      return;
    }

    if (layoutManager == null) {
      layoutManager = (LinearLayoutManager) parent.getLayoutManager();
    }

    int position = layoutManager.findFirstVisibleItemPosition();

    if (position == NO_POSITION) {
      return;
    }

    if (!isSectionType(position)) {
      return;
    }

    int topPadding = isClipToPadding ? parent.getPaddingTop() : 0;
    int left = parent.getPaddingLeft();
    int right = parent.getWidth() - parent.getPaddingRight();
    int top = topPadding;
    int bottom = top + sectionHeight;

    // if android:isClipToPadding="false", first header can be scroll up till reaching top.
    if (!isClipToPadding && position == 0) {
      top = firstHeaderTop > 0 ? firstHeaderTop : 0;
      bottom = top + sectionHeight;
    }

    if (isHeaderOverlapped) {
      top = top - topPadding - (sectionHeight - secondHeaderTop);
      bottom = top + sectionHeight;
    }

    boolean isHeaderExit = top <= topPadding;

    if (isHeaderExit) {
      isHeaderOverlapped = false;
    }

    View sectionHeader = getAndMeasureSectionHeader(parent, position);
    sectionHeader.layout(left, top, right, bottom);
    canvas.save();
    if (isClipToPadding && isHeaderExit) {
      canvas.clipRect(left, topPadding, right, bottom);
    }
    canvas.translate(left, top);
    sectionHeader.draw(canvas);
    canvas.restore();
  }

  private boolean isItemCoveredBySection(int itemHeight, int sectionTop) {
    return itemHeight + sectionTop <= 0;
  }
  
  private View getAndMeasureSectionHeader(RecyclerView parent, int position) {
    View sectionHeader = provider.getSectionHeaderView(getItem(position), position);
    int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
    int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.AT_MOST);
    sectionHeader.measure(widthSpec, heightSpec);
    return sectionHeader;
  }

  private boolean isSameSection(int position) {
    if (position == 0) {
      return false;
    }

    return isSectionType(position) && isSectionType(position - 1) &&
      provider.isSameSection(getItem(position), getItem(position - 1));
  }

  private boolean isSectionType(int position) {
    Class<?> aClass = getItem(position).getClass();

    // handle realm proxy class
    if (aClass.getName().endsWith("Proxy")) {
      aClass = aClass.getSuperclass();
    }

    return clazz.getCanonicalName().equals(aClass.getCanonicalName());
  }

  private Object getItem(int position) {
    return simpleRecyclerView.getCell(position).getItem();
  }

}
