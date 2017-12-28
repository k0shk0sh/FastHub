package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

/**
 * @since 1.0.1
 */
public class TaskListSpan implements LeadingMarginSpan {

    private static final int[] STATE_CHECKED = new int[]{android.R.attr.state_checked};

    private static final int[] STATE_NONE = new int[0];

    private final SpannableTheme theme;
    private final int blockIndent;
    private final boolean isDone;

    public TaskListSpan(@NonNull SpannableTheme theme, int blockIndent, boolean isDone) {
        this.theme = theme;
        this.blockIndent = blockIndent;
        this.isDone = isDone;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return theme.getBlockMargin() * blockIndent;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        if (!first
                || !LeadingMarginUtils.selfStart(start, text, this)) {
            return;
        }

        final Drawable drawable = theme.getTaskListDrawable();
        if (drawable == null) {
            return;
        }

        final int save = c.save();
        try {

            final int width = theme.getBlockMargin();
            final int height = bottom - top;

            final int w = (int) (width * .75F + .5F);
            final int h = (int) (height * .75F + .5F);

            drawable.setBounds(0, 0, w, h);

            if (drawable.isStateful()) {
                final int[] state;
                if (isDone) {
                    state = STATE_CHECKED;
                } else {
                    state = STATE_NONE;
                }
                drawable.setState(state);
            }

            final int l;
            if (dir > 0) {
                l = x + (width * (blockIndent - 1)) + ((width - w) / 2);
            } else {
                l = x - (width * blockIndent) + ((width - w) / 2);
            }

            final int t = top + ((height - h) / 2);

            c.translate(l, t);
            drawable.draw(c);

        } finally {
            c.restoreToCount(save);
        }
    }
}
