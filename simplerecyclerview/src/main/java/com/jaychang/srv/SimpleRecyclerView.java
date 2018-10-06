package com.jaychang.srv;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.jaychang.srv.behavior.DragAndDropCallback;
import com.jaychang.srv.behavior.DragAndDropHelper;
import com.jaychang.srv.behavior.DragAndDropOptions;
import com.jaychang.srv.behavior.SnapAlignment;
import com.jaychang.srv.behavior.StartSnapHelper;
import com.jaychang.srv.behavior.SwipeDirection;
import com.jaychang.srv.behavior.SwipeToDismissCallback;
import com.jaychang.srv.behavior.SwipeToDismissHelper;
import com.jaychang.srv.behavior.SwipeToDismissOptions;
import com.jaychang.srv.decoration.DividerItemDecoration;
import com.jaychang.srv.decoration.GridSpacingItemDecoration;
import com.jaychang.srv.decoration.LinearSpacingItemDecoration;
import com.jaychang.srv.decoration.SectionHeaderItemDecoration;
import com.jaychang.srv.decoration.SectionHeaderProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.SnapHelper;

public class SimpleRecyclerView extends RecyclerView
  implements CellOperations {

  private int layoutMode;
  private int gridSpanCount;
  private String gridSpanSequence;
  private int spacing;
  private int verticalSpacing;
  private int horizontalSpacing;
  private boolean isSpacingIncludeEdge;
  private boolean showDivider;
  private boolean showLastDivider;
  private int dividerColor;
  private int dividerOrientation;
  private int dividerPaddingLeft;
  private int dividerPaddingRight;
  private int dividerPaddingTop;
  private int dividerPaddingBottom;
  private boolean isSnappyEnabled;
  private int snapAlignment;
  private int emptyStateViewRes;
  private boolean showEmptyStateView;
  private int loadMoreViewRes;

  private SimpleAdapter adapter;
  private AdapterDataObserver adapterDataObserver = new AdapterDataObserver() {
    @Override
    public void onChanged() {
      updateEmptyStateViewVisibility();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      updateEmptyStateViewVisibility();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      updateEmptyStateViewVisibility();
    }
  };

  private List<String> noDividerCellTypes;

  private InternalEmptyStateViewCell emptyStateViewCell;
  private boolean isEmptyViewShown;
  private boolean isRefreshing;

  private InternalLoadMoreViewCell loadMoreViewCell;
  private boolean isScrollUp;
  private int autoLoadMoreThreshold;
  private OnLoadMoreListener onLoadMoreListener;
  private boolean isLoadMoreToTop;
  private boolean isLoadingMore;
  private boolean isLoadMoreViewShown;

  public SimpleRecyclerView(Context context) {
    this(context, null);
  }

  public SimpleRecyclerView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SimpleRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    initAttrs(context, attrs, defStyle);

    if (!isInEditMode()) {
      setup();
    }
  }

  private void initAttrs(Context context, AttributeSet attrs, int defStyle) {
    TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SimpleRecyclerView, defStyle, 0);
    layoutMode = typedArray.getInt(R.styleable.SimpleRecyclerView_srv_layoutMode, 0);
    gridSpanCount = typedArray.getInt(R.styleable.SimpleRecyclerView_srv_gridSpanCount, 0);
    gridSpanSequence = typedArray.getString(R.styleable.SimpleRecyclerView_srv_gridSpanSequence);
    spacing = typedArray.getDimensionPixelSize(R.styleable.SimpleRecyclerView_srv_spacing, 0);
    verticalSpacing = typedArray.getDimensionPixelSize(R.styleable.SimpleRecyclerView_srv_verticalSpacing, 0);
    horizontalSpacing = typedArray.getDimensionPixelSize(R.styleable.SimpleRecyclerView_srv_horizontalSpacing, 0);
    isSpacingIncludeEdge = typedArray.getBoolean(R.styleable.SimpleRecyclerView_srv_isSpacingIncludeEdge, false);
    showDivider = typedArray.getBoolean(R.styleable.SimpleRecyclerView_srv_showDivider, false);
    showLastDivider = typedArray.getBoolean(R.styleable.SimpleRecyclerView_srv_showLastDivider, false);
    dividerColor = typedArray.getColor(R.styleable.SimpleRecyclerView_srv_dividerColor, 0);
    dividerOrientation = typedArray.getInt(R.styleable.SimpleRecyclerView_srv_dividerOrientation, 2);
    dividerPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.SimpleRecyclerView_srv_dividerPaddingLeft, 0);
    dividerPaddingRight = typedArray.getDimensionPixelSize(R.styleable.SimpleRecyclerView_srv_dividerPaddingRight, 0);
    dividerPaddingTop = typedArray.getDimensionPixelSize(R.styleable.SimpleRecyclerView_srv_dividerPaddingTop, 0);
    dividerPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.SimpleRecyclerView_srv_dividerPaddingBottom, 0);
    isSnappyEnabled = typedArray.getBoolean(R.styleable.SimpleRecyclerView_srv_snappy, false);
    snapAlignment = typedArray.getInt(R.styleable.SimpleRecyclerView_srv_snap_alignment, 0);
    showEmptyStateView = typedArray.getBoolean(R.styleable.SimpleRecyclerView_srv_showEmptyStateView, false);
    emptyStateViewRes = typedArray.getResourceId(R.styleable.SimpleRecyclerView_srv_emptyStateView, 0);
    loadMoreViewRes = typedArray.getResourceId(R.styleable.SimpleRecyclerView_srv_loadMoreView, 0);
    typedArray.recycle();
  }

  /**
   * setup
   */
  private void setup() {
    setupRecyclerView();
    setupDecorations();
    setupBehaviors();
  }

  private void setupRecyclerView() {
    setupAdapter();
    setupLayoutManager();
    setupEmptyView();
    setupLoadMore();
    disableChangeAnimations();
  }

  private void setupAdapter() {
    adapter = new SimpleAdapter();
    adapter.registerAdapterDataObserver(adapterDataObserver);
    setAdapter(adapter);
  }

  private void setupLayoutManager() {
    if (layoutMode == 0) {
      useLinearVerticalMode();
    } else if (layoutMode == 1) {
      useLinearHorizontalMode();
    } else if (layoutMode == 2) {
      if (!TextUtils.isEmpty(gridSpanSequence)) {
        try {
          useGridModeWithSequence(Utils.toIntList(gridSpanSequence));
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("gridSpanSequence must be digits. (e.g. 2233)");
        }
      } else {
        useGridMode(gridSpanCount);
      }
    }
  }

  private void setupEmptyView() {
    if (emptyStateViewRes != 0) {
      setEmptyStateView(emptyStateViewRes);
    }
    if (showEmptyStateView) {
      showEmptyStateView();
    }
  }

  private void setupLoadMore() {
    if (loadMoreViewRes != 0) {
      setLoadMoreView(loadMoreViewRes);
    }

    addOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (onLoadMoreListener == null) {
          return;
        }

        isScrollUp = dy < 0;

        checkLoadMoreThreshold();
      }
    });

    // trigger checkLoadMoreThreshold() if the recyclerview if not scrollable.
    setOnTouchListener(new OnTouchListener() {
      float preY;

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (onLoadMoreListener == null) {
          return false;
        }

        switch (event.getAction()) {
          case MotionEvent.ACTION_MOVE:
            isScrollUp = event.getY() > preY;
            preY = event.getY();
            checkLoadMoreThreshold();
        }

        if (Utils.isScrollable(SimpleRecyclerView.this)) {
          setOnTouchListener(null);
        }

        return false;
      }
    });
  }

  private void checkLoadMoreThreshold() {
    // check isEmpty() to prevent the case: removeAllCells triggers this call
    if (isEmptyViewShown || isLoadingMore || isEmpty()) {
      return;
    }

    if (isLoadMoreToTop && isScrollUp) {
      int topHiddenItemCount = getFirstVisibleItemPosition();

      if (topHiddenItemCount == -1) {
        return;
      }

      if (topHiddenItemCount <= autoLoadMoreThreshold) {
        handleLoadMore();
      }

      return;
    }

    if (!isLoadMoreToTop && !isScrollUp) {
      int bottomHiddenItemCount = getItemCount() - getLastVisibleItemPosition() - 1;

      if (bottomHiddenItemCount == -1) {
        return;
      }

      if (bottomHiddenItemCount <= autoLoadMoreThreshold) {
        handleLoadMore();
      }
    }
  }

  private void handleLoadMore() {
    if (onLoadMoreListener.shouldLoadMore()) {
      onLoadMoreListener.onLoadMore();
    }
  }

  private int getFirstVisibleItemPosition() {
    if (getLayoutManager() instanceof GridLayoutManager) {
      return ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
    } else if (getLayoutManager() instanceof LinearLayoutManager) {
      return ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
    } else {
      return -1;
    }
  }

  private int getLastVisibleItemPosition() {
    if (getLayoutManager() instanceof GridLayoutManager) {
      return ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
    } else if (getLayoutManager() instanceof LinearLayoutManager) {
      return ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
    } else {
      return -1;
    }
  }

  private void disableChangeAnimations() {
    ItemAnimator animator = getItemAnimator();
    if (animator instanceof SimpleItemAnimator) {
      ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
    }

    // todo temp fix: load more doesn't work good with grid layout mode
    setItemAnimator(null);
  }

  private void setupDecorations() {
    if (showDivider) {
      if (dividerColor != 0) {
        showDividerInternal(dividerColor, dividerPaddingLeft, dividerPaddingTop, dividerPaddingRight, dividerPaddingBottom);
      } else {
        showDivider();
      }
    }

    if (spacing != 0) {
      setSpacingInternal(spacing, spacing, isSpacingIncludeEdge);
    } else if (verticalSpacing != 0 || horizontalSpacing != 0) {
      setSpacingInternal(verticalSpacing, horizontalSpacing, isSpacingIncludeEdge);
    }
  }

  private void setupBehaviors() {
    if (isSnappyEnabled) {
      if (snapAlignment == 0) {
        enableSnappy(SnapAlignment.CENTER);
      } else if (snapAlignment == 1) {
        enableSnappy(SnapAlignment.START);
      }
    }
  }

  /**
   * layout modes
   */
  public void useLinearVerticalMode() {
    setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
  }

  public void useLinearHorizontalMode() {
    setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
  }

  public void useGridMode(int spanCount) {
    setGridSpanCount(spanCount);
    setLayoutManager(new GridLayoutManager(getContext(), spanCount));

    GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {
        try {
          return adapter.getCell(position).getSpanSize();
        } catch (Exception e) {
          return 1;
        }
      }
    };
    spanSizeLookup.setSpanIndexCacheEnabled(true);
    ((GridLayoutManager) getLayoutManager()).setSpanSizeLookup(spanSizeLookup);
  }

  public void useGridModeWithSequence(int first, int... rest) {
    useGridModeWithSequence(Utils.toIntList(first, rest));
  }

  public void useGridModeWithSequence(@NonNull List<Integer> sequence) {
    final int lcm = Utils.lcm(sequence);
    final ArrayList<Integer> sequenceList = new ArrayList<>();
    for (int i = 0; i < sequence.size(); i++) {
      int item = sequence.get(i);
      for (int j = 0; j < item; j++) {
        sequenceList.add(lcm / item);
      }
    }

    setGridSpanCount(lcm);
    setLayoutManager(new GridLayoutManager(getContext(), lcm));

    GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {
        try {
          return sequenceList.get(position % sequenceList.size());
        } catch (Exception e) {
          return 1;
        }
      }
    };
    spanSizeLookup.setSpanIndexCacheEnabled(true);
    ((GridLayoutManager) getLayoutManager()).setSpanSizeLookup(spanSizeLookup);
  }

  private void setGridSpanCount(int spanCount) {
    if (spanCount <= 0) {
      throw new IllegalArgumentException("spanCount must >= 1");
    }

    this.gridSpanCount = spanCount;
  }

  /**
   * divider
   */
  private void showDividerInternal(@ColorInt int color,
                                   int paddingLeft, int paddingTop,
                                   int paddingRight, int paddingBottom) {
    if (getLayoutManager() instanceof GridLayoutManager) {
      if (dividerOrientation == 0) {
        addDividerItemDecoration(color, DividerItemDecoration.HORIZONTAL,
          paddingLeft, paddingTop, paddingRight, paddingBottom);
      } else if (dividerOrientation == 1) {
        addDividerItemDecoration(color, DividerItemDecoration.VERTICAL,
          paddingLeft, paddingTop, paddingRight, paddingBottom);
      } else {
        addDividerItemDecoration(color, DividerItemDecoration.VERTICAL,
          paddingLeft, paddingTop, paddingRight, paddingBottom);
        addDividerItemDecoration(color, DividerItemDecoration.HORIZONTAL,
          paddingLeft, paddingTop, paddingRight, paddingBottom);
      }
    } else if (getLayoutManager() instanceof LinearLayoutManager) {
      int orientation = ((LinearLayoutManager) getLayoutManager()).getOrientation();
      addDividerItemDecoration(color, orientation,
        paddingLeft, paddingTop, paddingRight, paddingBottom);
    }
  }

  private void addDividerItemDecoration(@ColorInt int color, int orientation,
                                        int paddingLeft, int paddingTop,
                                        int paddingRight, int paddingBottom) {
    DividerItemDecoration decor = new DividerItemDecoration(getContext(), orientation);
    if (color != 0) {
      ShapeDrawable shapeDrawable = new ShapeDrawable(new RectShape());
      shapeDrawable.setIntrinsicHeight(Utils.dpToPx(getContext(), 1));
      shapeDrawable.setIntrinsicWidth(Utils.dpToPx(getContext(), 1));
      shapeDrawable.getPaint().setColor(color);
      InsetDrawable insetDrawable = new InsetDrawable(shapeDrawable, paddingLeft, paddingTop, paddingRight, paddingBottom);
      decor.setDrawable(insetDrawable);
    }
    decor.setShowLastDivider(showLastDivider);
    addItemDecoration(decor);
  }

  public void showDivider() {
    showDividerInternal(Color.parseColor("#e0e0e0"), dividerPaddingLeft, dividerPaddingTop, dividerPaddingRight, dividerPaddingBottom);
  }

  public void showDivider(@ColorRes int colorRes) {
    showDividerInternal(ContextCompat.getColor(getContext(), colorRes),
      dividerPaddingLeft, dividerPaddingTop, dividerPaddingRight, dividerPaddingBottom);
  }

  public void showDivider(@ColorRes int colorRes, int paddingLeftDp, int paddingTopDp, int paddingRightDp, int paddingBottomDp) {
    showDividerInternal(ContextCompat.getColor(getContext(), colorRes),
      Utils.dpToPx(getContext(), paddingLeftDp), Utils.dpToPx(getContext(), paddingTopDp),
      Utils.dpToPx(getContext(), paddingRightDp), Utils.dpToPx(getContext(), paddingBottomDp));
  }

  public void dontShowDividerForCellType(@NonNull Class<?>... classes) {
    if (noDividerCellTypes == null) {
      noDividerCellTypes = new ArrayList<>();
    }

    for (Class<?> aClass : classes) {
      noDividerCellTypes.add(aClass.getSimpleName());
    }
  }

  public List<String> getNoDividerCellTypes() {
    return noDividerCellTypes == null ? Collections.<String>emptyList() : noDividerCellTypes;
  }

  /**
   * spacing
   */
  private void setGridSpacingInternal(int verSpacing, int horSpacing, boolean includeEdge) {
    addItemDecoration(GridSpacingItemDecoration.newBuilder().verticalSpacing(verSpacing).horizontalSpacing(horSpacing).includeEdge(includeEdge).build());
  }

  private void setLinearSpacingInternal(int spacing, boolean includeEdge) {
    int orientation = ((LinearLayoutManager) getLayoutManager()).getOrientation();
    addItemDecoration(LinearSpacingItemDecoration.newBuilder().spacing(spacing).orientation(orientation).includeEdge(includeEdge).build());
  }

  private void setSpacingInternal(int verSpacing, int horSpacing, boolean includeEdge) {
    if (getLayoutManager() instanceof GridLayoutManager) {
      setGridSpacingInternal(verSpacing, horSpacing, includeEdge);
    } else if (getLayoutManager() instanceof LinearLayoutManager) {
      setLinearSpacingInternal(verSpacing, includeEdge);
    }
  }

  public void setSpacing(int spacingDp) {
    int spacing = Utils.dpToPx(getContext(), spacingDp);
    setSpacingInternal(spacing, spacing, false);
  }

  public void setSpacingIncludeEdge(int spacingDp) {
    int spacing = Utils.dpToPx(getContext(), spacingDp);
    setSpacingInternal(spacing, spacing, true);
  }

  public void setSpacing(int verticalSpacingDp, int horizontalSpacingDp) {
    int verticalSpacing = Utils.dpToPx(getContext(), verticalSpacingDp);
    int horizontalSpacing = Utils.dpToPx(getContext(), horizontalSpacingDp);
    setSpacingInternal(verticalSpacing, horizontalSpacing, false);
  }

  public void setSpacingIncludeEdge(int verticalSpacingDp, int horizontalSpacingDp) {
    int verticalSpacing = Utils.dpToPx(getContext(), verticalSpacingDp);
    int horizontalSpacing = Utils.dpToPx(getContext(), horizontalSpacingDp);
    setSpacingInternal(verticalSpacing, horizontalSpacing, true);
  }

  /**
   * empty view
   */
  private void updateEmptyStateViewVisibility() {
    adapter.unregisterAdapterDataObserver(adapterDataObserver);
    if (adapter.getItemCount() <= 0) {
      showEmptyStateView();
    } else {
      hideEmptyStateView();
    }
    adapter.registerAdapterDataObserver(adapterDataObserver);
  }

  public void showEmptyStateView() {
    if (isRefreshing) {
      isRefreshing = false;
      return;
    }

    if (isEmptyViewShown || emptyStateViewCell == null) {
      return;
    }

    addCell(emptyStateViewCell);

    isEmptyViewShown = true;
  }

  public void hideEmptyStateView() {
    if (!isEmptyViewShown || emptyStateViewCell == null) {
      return;
    }

    removeCell(emptyStateViewCell);

    isEmptyViewShown = false;
  }

  public void setEmptyStateView(@LayoutRes int emptyStateView) {
    View view = LayoutInflater.from(getContext()).inflate(emptyStateView, this, false);
    setEmptyStateView(view);
  }

  public void setEmptyStateView(@NonNull View emptyStateView) {
    this.emptyStateViewCell = new InternalEmptyStateViewCell(emptyStateView);
    emptyStateViewCell.setSpanSize(gridSpanCount);
  }

  /**
   * load more
   */
  public void setLoadMoreView(@LayoutRes int loadMoreView) {
    View view = LayoutInflater.from(getContext()).inflate(loadMoreView, this, false);
    setLoadMoreView(view);
  }

  public void setLoadMoreView(@NonNull View loadMoreView) {
    this.loadMoreViewCell = new InternalLoadMoreViewCell(loadMoreView);
    loadMoreViewCell.setSpanSize(gridSpanCount);
  }

  public void showLoadMoreView() {
    if (loadMoreViewCell == null || isLoadMoreViewShown) {
      isLoadingMore = true;
      return;
    }

    if (isLoadMoreToTop) {
      addCell(0, loadMoreViewCell);
    } else {
      addCell(loadMoreViewCell);
    }

    isLoadMoreViewShown = true;
    isLoadingMore = true;
  }

  public void hideLoadMoreView() {
    if (loadMoreViewCell == null || !isLoadMoreViewShown) {
      isLoadingMore = false;
      return;
    }

    removeCell(loadMoreViewCell);

    isLoadMoreViewShown = false;
    isLoadingMore = false;
  }

  public void setAutoLoadMoreThreshold(int hiddenCellCount) {
    if (hiddenCellCount < 0) {
      throw new IllegalArgumentException("hiddenCellCount must >= 0");
    }
    this.autoLoadMoreThreshold = hiddenCellCount;
  }

  public int getAutoLoadMoreThreshold() {
    return autoLoadMoreThreshold;
  }

  public void setLoadMoreToTop(boolean isLoadMoreForTop) {
    this.isLoadMoreToTop = isLoadMoreForTop;
  }

  public boolean isLoadMoreToTop() {
    return isLoadMoreToTop;
  }

  public void setOnLoadMoreListener(@NonNull OnLoadMoreListener listener) {
    this.onLoadMoreListener = listener;
  }

  @Deprecated
  public void setLoadMoreCompleted() {
    this.isLoadingMore = false;
  }

  /**
   * drag & drop
   */
  public void enableDragAndDrop(@NonNull DragAndDropCallback dragAndDropCallback) {
    enableDragAndDrop(0, dragAndDropCallback);
  }

  public void enableDragAndDrop(@IdRes int dragHandleId, @NonNull DragAndDropCallback dragAndDropCallback) {
    DragAndDropOptions options = new DragAndDropOptions();
    options.setDragHandleId(dragHandleId);
    options.setCanLongPressToDrag(dragHandleId == 0);
    options.setDragAndDropCallback(dragAndDropCallback);
    options.setEnableDefaultEffect(dragAndDropCallback.enableDefaultRaiseEffect());
    DragAndDropHelper dragAndDropHelper = DragAndDropHelper.create(adapter, options);
    adapter.setDragAndDropHelper(dragAndDropHelper);
    dragAndDropHelper.attachToRecyclerView(this);
  }

  /**
   * swipe to dismiss
   */
  public void enableSwipeToDismiss(@NonNull SwipeToDismissCallback swipeToDismissCallback, @NonNull SwipeDirection... directions) {
    enableSwipeToDismiss(swipeToDismissCallback, new HashSet<>(Arrays.asList(directions)));
  }

  public void enableSwipeToDismiss(@NonNull SwipeToDismissCallback swipeToDismissCallback, @NonNull Set<SwipeDirection> directions) {
    SwipeToDismissOptions options = new SwipeToDismissOptions();
    options.setEnableDefaultFadeOutEffect(swipeToDismissCallback.enableDefaultFadeOutEffect());
    options.setSwipeToDismissCallback(swipeToDismissCallback);
    options.setSwipeDirections(directions);
    SwipeToDismissHelper helper = SwipeToDismissHelper.create(adapter, options);
    helper.attachToRecyclerView(this);
  }

  /**
   * snappy
   */
  public void enableSnappy() {
    enableSnappy(SnapAlignment.CENTER);
  }

  public void enableSnappy(@NonNull SnapAlignment alignment) {
    SnapHelper snapHelper = alignment.equals(SnapAlignment.CENTER) ?
                            new LinearSnapHelper() : new StartSnapHelper(spacing);
    snapHelper.attachToRecyclerView(this);
  }

  /**
   * section header
   */
  public <T> void setSectionHeader(@NonNull SectionHeaderProvider<T> provider) {
    if (getLayoutManager() instanceof GridLayoutManager) {
      // todo
      return;
    }
    if (getLayoutManager() instanceof LinearLayoutManager) {
      addItemDecoration(new SectionHeaderItemDecoration(Utils.getTypeArgumentClass(provider.getClass()), provider));
    }
  }

  /**
   * cell operations
   */
  @Override
  public void addCell(@NonNull SimpleCell cell) {
    adapter.addCell(cell);
  }

  @Override
  public void addCell(int atPosition, @NonNull SimpleCell cell) {
    adapter.addCell(atPosition, cell);
  }

  @Override
  public void addCells(@NonNull List<? extends SimpleCell> cells) {
    adapter.addCells(cells);
  }

  @Override
  public void addCells(@NonNull SimpleCell... cells) {
    adapter.addCells(cells);
  }

  @Override
  public void addCells(int fromPosition, @NonNull List<? extends SimpleCell> cells) {
    adapter.addCells(fromPosition, cells);
  }

  @Override
  public void addCells(int fromPosition, @NonNull SimpleCell... cells) {
    adapter.addCells(fromPosition, cells);
  }

  @Override
  public <T extends SimpleCell & Updatable> void addOrUpdateCell(@NonNull T cell) {
    adapter.addOrUpdateCell(cell);
  }

  @Override
  public <T extends SimpleCell & Updatable> void addOrUpdateCells(@NonNull List<T> cells) {
    adapter.addOrUpdateCells(cells);
  }

  @Override
  public <T extends SimpleCell & Updatable> void addOrUpdateCells(@NonNull T... cells) {
    adapter.addOrUpdateCells(cells);
  }

  @Override
  public void removeCell(@NonNull SimpleCell cell) {
    adapter.removeCell(cell);
  }

  @Override
  public void removeCell(int atPosition) {
    adapter.removeCell(atPosition);
  }

  @Override
  public void removeCells(int fromPosition, int toPosition) {
    adapter.removeCells(fromPosition, toPosition);
  }

  @Override
  public void removeCells(int fromPosition) {
    adapter.removeCells(fromPosition);
  }

  @Override
  public void updateCell(int atPosition, @NonNull Object payload) {
    adapter.updateCell(atPosition, payload);
  }

  @Override
  public void updateCells(int fromPosition, int toPosition, @NonNull Object payloads) {
    adapter.updateCells(fromPosition, toPosition, payloads);
  }

  @Override
  public SimpleCell getCell(int atPosition) {
    return adapter.getCell(atPosition);
  }

  @Override
  public List<SimpleCell> getCells(int fromPosition, int toPosition) {
    return adapter.getCells(fromPosition, toPosition);
  }

  @Override
  public List<SimpleCell> getAllCells() {
    return adapter.getAllCells();
  }

  @Override
  public void removeAllCells() {
    removeAllCells(true);
  }

  // remove all cells and indicates that data is refreshing, so the empty view will not be shown.
  public void removeAllCells(boolean showEmptyStateView) {
    this.isRefreshing = !showEmptyStateView;
    this.isEmptyViewShown = false;
    adapter.removeAllCells();
  }

  public boolean isEmpty() {
    return getItemCount() <= 0;
  }

  public int getItemCount() {
    return isEmptyViewShown ? 0 : adapter.getItemCount();
  }

  public void smoothScrollToPosition(int position, ScrollPosition scrollPosition, boolean skipSpacing) {
    if (position < 0 || position >= getAllCells().size()) {
      return;
    }

    SimpleLinearSmoothScroller scroller = new SimpleLinearSmoothScroller(getContext(), skipSpacing);
    if (getLayoutManager().canScrollVertically()) {
      scroller.setVerticalScrollPosition(scrollPosition);
    } else if (getLayoutManager().canScrollHorizontally()) {
      scroller.setHorizontalScrollPosition(scrollPosition);
    }
    scroller.setTargetPosition(position);
    getLayoutManager().startSmoothScroll(scroller);
  }

  public void smoothScrollToPosition(int position, ScrollPosition scrollPosition) {
    smoothScrollToPosition(position, scrollPosition, false);
  }

  @Override
  public void smoothScrollToPosition(int position) {
    smoothScrollToPosition(position, ScrollPosition.TOP, false);
  }

  public void scrollToPosition(int position, ScrollPosition scrollPosition, boolean skipSpacing) {
    if (position < 0 || position >= getAllCells().size()) {
      return;
    }
    
    if (!(getLayoutManager() instanceof LinearLayoutManager)) {
      return;
    }

    LinearLayoutManager layoutManager = ((LinearLayoutManager) getLayoutManager());

    int padding = layoutManager.getOrientation() == HORIZONTAL ? layoutManager.getPaddingLeft() : layoutManager.getPaddingTop();

    if (scrollPosition == ScrollPosition.TOP) {
      int spacing = skipSpacing ? this.spacing : 0;
      layoutManager.scrollToPositionWithOffset(position, -(padding + spacing));
    } else if (scrollPosition == ScrollPosition.START) {
      int spacing = skipSpacing ? -this.spacing : this.spacing;
      layoutManager.scrollToPositionWithOffset(position, -padding + spacing / 2);
    }
  }

  /**
   * common
   */
  public int getGridSpanCount() {
    return gridSpanCount;
  }

}
