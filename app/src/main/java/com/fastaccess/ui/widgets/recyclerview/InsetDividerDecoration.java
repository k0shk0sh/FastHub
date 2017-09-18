
package com.fastaccess.ui.widgets.recyclerview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * A decoration which draws a horizontal divider between {@link RecyclerView.ViewHolder}s of a given
 * type; with a left inset.
 * this class adopted from Plaid
 */
class InsetDividerDecoration extends RecyclerView.ItemDecoration {

    @NonNull private final Paint paint;
    private final int inset;
    private final int height;
    private final Class toDivide;

    InsetDividerDecoration(int divider, int leftInset, @ColorInt int dividerColor) {
        this(divider, leftInset, dividerColor, null);
    }

    InsetDividerDecoration(int divider, int leftInset, @ColorInt int dividerColor, @Nullable Class toDivide) {
        this.inset = leftInset;
        this.height = divider;
        this.paint = new Paint();
        this.paint.setColor(dividerColor);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(divider);
        this.toDivide = toDivide;
    }

    @Override public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();
        if (childCount < 2) return;
        RecyclerView.LayoutManager lm = parent.getLayoutManager();
        float[] lines = new float[childCount * 4];
        boolean hasDividers = false;
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(child);
            if (!(viewHolder instanceof ProgressBarViewHolder)) {
                boolean canDivide = toDivide == null || viewHolder.getClass() == toDivide;
                if (canDivide) {
                    int position = parent.getChildAdapterPosition(child);
                    if (child.isActivated() || (i + 1 < childCount && parent.getChildAt(i + 1).isActivated())) {
                        continue;
                    }
                    if (position != (state.getItemCount() - 1)) {
                        lines[i * 4] = inset == 0 ? inset : inset + lm.getDecoratedLeft(child);
                        lines[(i * 4) + 2] = lm.getDecoratedRight(child);
                        int y = lm.getDecoratedBottom(child) + (int) child.getTranslationY() - height;
                        lines[(i * 4) + 1] = y;
                        lines[(i * 4) + 3] = y;
                        hasDividers = true;
                    }
                }
            }
        }
        if (hasDividers) {
            canvas.drawLines(lines, paint);
        }
    }
}
