package ru.noties.markwon.spans;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class TableRowSpan extends ReplacementSpan {

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;

    @IntDef(value = {ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Alignment {
    }

    public interface Invalidator {
        void invalidate();
    }

    public static class Cell {

        final int alignment;
        final CharSequence text;

        public Cell(@Alignment int alignment, CharSequence text) {
            this.alignment = alignment;
            this.text = text;
        }

        @Alignment
        public int alignment() {
            return alignment;
        }

        public CharSequence text() {
            return text;
        }

        @Override
        public String toString() {
            return "Cell{" +
                    "alignment=" + alignment +
                    ", text=" + text +
                    '}';
        }
    }

    private final SpannableTheme theme;
    private final List<Cell> cells;
    private final List<StaticLayout> layouts;
    private final TextPaint textPaint;
    private final boolean header;
    private final boolean odd;

    private final Rect rect = ObjectsPool.rect();
    private final Paint paint = ObjectsPool.paint();

    private int width;
    private int height;
    private Invalidator invalidator;

    public TableRowSpan(
            @NonNull SpannableTheme theme,
            @NonNull List<Cell> cells,
            boolean header,
            boolean odd) {
        this.theme = theme;
        this.cells = cells;
        this.layouts = new ArrayList<>(cells.size());
        this.textPaint = new TextPaint();
        this.header = header;
        this.odd = odd;
    }

    @Override
    public int getSize(
            @NonNull Paint paint,
            CharSequence text,
            @IntRange(from = 0) int start,
            @IntRange(from = 0) int end,
            @Nullable Paint.FontMetricsInt fm) {

        // it's our absolute requirement to have width of the canvas here... because, well, it changes
        // the way we draw text. So, if we do not know the width of canvas we cannot correctly measure our text

        if (layouts.size() > 0) {

            if (fm != null) {

                int max = 0;
                for (StaticLayout layout : layouts) {
                    final int height = layout.getHeight();
                    if (height > max) {
                        max = height;
                    }
                }

                // we store actual height
                height = max;

                // but apply height with padding
                final int padding = theme.tableCellPadding() * 2;

                fm.ascent = -(max + padding);
                fm.descent = 0;

                fm.top = fm.ascent;
                fm.bottom = 0;
            }
        }

        return width;
    }

    @Override
    public void draw(
            @NonNull Canvas canvas,
            CharSequence text,
            @IntRange(from = 0) int start,
            @IntRange(from = 0) int end,
            float x,
            int top,
            int y,
            int bottom,
            @NonNull Paint paint) {

        if (recreateLayouts(canvas.getWidth())) {
            width = canvas.getWidth();
            textPaint.set(paint);
            makeNewLayouts();
        }

        int maxHeight = 0;

        final int padding = theme.tableCellPadding();

        final int size = layouts.size();

        final int w = width / size;

        // feels like magic...
        final int heightDiff = (bottom - top - height) / 4;

        if (odd) {
            final int save = canvas.save();
            try {
                rect.set(0, 0, width, bottom - top);
                theme.applyTableOddRowStyle(this.paint);
                canvas.translate(x, top - heightDiff);
                canvas.drawRect(rect, this.paint);
            } finally {
                canvas.restoreToCount(save);
            }
        }

        theme.applyTableBorderStyle(this.paint);

        final int borderWidth = theme.tableBorderWidth(paint);
        final boolean drawBorder = borderWidth > 0;
        if (drawBorder) {
            rect.set(0, 0, w, bottom - top);
        }

        StaticLayout layout;
        for (int i = 0; i < size; i++) {
            layout = layouts.get(i);
            final int save = canvas.save();
            try {

                canvas.translate(x + (i * w), top - heightDiff);

                if (drawBorder) {
                    canvas.drawRect(rect, this.paint);
                }

                canvas.translate(padding, padding + heightDiff);
                layout.draw(canvas);

                if (layout.getHeight() > maxHeight) {
                    maxHeight = layout.getHeight();
                }

            } finally {
                canvas.restoreToCount(save);
            }
        }

        if (height != maxHeight) {
            if (invalidator != null) {
                invalidator.invalidate();
            }
        }
    }

    private boolean recreateLayouts(int newWidth) {
        return width != newWidth;
    }

    private void makeNewLayouts() {

        textPaint.setFakeBoldText(header);

        final int columns = cells.size();
        final int padding = theme.tableCellPadding() * 2;
        final int w = (width / columns) - padding;

        this.layouts.clear();
        Cell cell;
        StaticLayout layout;
        for (int i = 0, size = cells.size(); i < size; i++) {
            cell = cells.get(i);
            layout = new StaticLayout(
                    cell.text,
                    textPaint,
                    w,
                    alignment(cell.alignment),
                    1.F,
                    .0F,
                    false
            );
            layouts.add(layout);
        }
    }

    @SuppressLint("SwitchIntDef")
    private static Layout.Alignment alignment(@Alignment int alignment) {
        final Layout.Alignment out;
        switch (alignment) {
            case ALIGN_CENTER:
                out = Layout.Alignment.ALIGN_CENTER;
                break;
            case ALIGN_RIGHT:
                out = Layout.Alignment.ALIGN_OPPOSITE;
                break;
            default:
                out = Layout.Alignment.ALIGN_NORMAL;
                break;
        }
        return out;
    }

    public TableRowSpan invalidator(Invalidator invalidator) {
        this.invalidator = invalidator;
        return this;
    }
}
