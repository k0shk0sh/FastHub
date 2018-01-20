package com.fastaccess.provider.timeline.handler;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.AlignmentSpan;
import android.text.style.ImageSpan;

import net.nightwhistler.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles simple HTML tables.
 * <p>
 * Since it renders these tables itself, it needs to know things like font size
 * and text colour to use.
 *
 * @author Alex Kuiper
 */
public class TableHandler extends TagNodeHandler {

    private int tableWidth = 500;
    private Typeface typeFace = Typeface.DEFAULT;
    private float textSize = 28f;
    private int textColor = Color.BLACK;
    private static final int PADDING = 20;

    @Override public boolean rendersContent() {
        return true;
    }

    @Override public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end) {
        Table table = getTable(node);
        for (int i = 0; i < table.getRows().size(); i++) {
            List<Spanned> row = table.getRows().get(i);
            builder.append("\uFFFC");
            TableRowDrawable drawable = new TableRowDrawable(row, table.isDrawBorder());
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            builder.setSpan(new ImageSpan(drawable), start + i, builder.length(), 33);

        }
        builder.append("\uFFFC");
        Drawable drawable = new TableRowDrawable(new ArrayList<Spanned>(), table.isDrawBorder());
        drawable.setBounds(0, 0, tableWidth, 1);
        builder.setSpan(new ImageSpan(drawable), builder.length() - 1, builder.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan((AlignmentSpan) () -> Alignment.ALIGN_CENTER, start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append("\n");
    }

    public void setTableWidth(int tableWidth) {
        this.tableWidth = tableWidth;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    private void readNode(Object node, Table table) {
        if (node instanceof TagNode) {
            TagNode tagNode = (TagNode) node;
            if (tagNode.getName().equals("td") || tagNode.getName().equals("th")) {
                Spanned result = this.getSpanner().fromTagNode(tagNode);
                table.addCell(result);
                return;
            }
            if (tagNode.getName().equals("tr")) {
                table.addRow();
            }
            for (Object child : tagNode.getChildTags()) {
                readNode(child, table);
            }
        }

    }

    private Table getTable(TagNode node) {

        String border = node.getAttributeByName("border");

        boolean drawBorder = !"0".equals(border);

        Table result = new Table(drawBorder);

        readNode(node, result);

        return result;
    }

    private TextPaint getTextPaint() {
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(this.textColor);
        textPaint.linkColor = this.textColor;
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(this.textSize);
        textPaint.setTypeface(this.typeFace);

        return textPaint;
    }

    private int calculateRowHeight(List<Spanned> row) {

        if (row.size() == 0) {
            return 0;
        }

        TextPaint textPaint = getTextPaint();

        int columnWidth = tableWidth / row.size();

        int rowHeight = 0;

        for (Spanned cell : row) {

            StaticLayout layout = new StaticLayout(cell, textPaint, columnWidth
                    - 2 * PADDING, Alignment.ALIGN_NORMAL, 1.5f, 0.5f, true);

            if (layout.getHeight() > rowHeight) {
                rowHeight = layout.getHeight();
            }
        }

        return rowHeight;
    }

    private class TableRowDrawable extends Drawable {

        private List<Spanned> tableRow;

        private int rowHeight;
        private boolean paintBorder;

        TableRowDrawable(List<Spanned> tableRow, boolean paintBorder) {
            this.tableRow = tableRow;
            this.rowHeight = calculateRowHeight(tableRow);
            this.paintBorder = paintBorder;
        }

        @Override public void draw(@NonNull Canvas canvas) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setStyle(Style.STROKE);

            int numberOfColumns = tableRow.size();

            if (numberOfColumns == 0) {
                return;
            }

            int columnWidth = tableWidth / numberOfColumns;

            int offset;

            for (int i = 0; i < numberOfColumns; i++) {

                offset = i * columnWidth;

                if (paintBorder) {
                    // The rect is open at the bottom, so there's a single line
                    // between rows.
                    canvas.drawRect(offset, 0, offset + columnWidth, rowHeight, paint);
                }

                StaticLayout layout = new StaticLayout(tableRow.get(i),
                        getTextPaint(), (columnWidth - 2 * PADDING),
                        Alignment.ALIGN_NORMAL, 1.5f, 0.5f, true);

                canvas.translate(offset + PADDING, 0);
                layout.draw(canvas);
                canvas.translate(-1 * (offset + PADDING), 0);

            }
        }

        @Override
        public int getIntrinsicHeight() {
            return rowHeight;
        }

        @Override
        public int getIntrinsicWidth() {
            return tableWidth;
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter cf) {

        }
    }

    private class Table {

        private boolean drawBorder;
        private List<List<Spanned>> content = new ArrayList<>();

        private Table(boolean drawBorder) {
            this.drawBorder = drawBorder;
        }

        boolean isDrawBorder() {
            return drawBorder;
        }

        void addRow() {
            content.add(new ArrayList<>());
        }

        List<Spanned> getBottomRow() {
            return content.get(content.size() - 1);
        }

        List<List<Spanned>> getRows() {
            return content;
        }

        void addCell(Spanned text) {
            if (content.isEmpty()) {
                throw new IllegalStateException("No rows added yet");
            }

            getBottomRow().add(text);
        }
    }

}