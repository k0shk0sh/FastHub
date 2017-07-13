package com.fastaccess.provider.timeline.handler;

import android.content.Context;
import android.graphics.RectF;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Kosh on 23 Apr 2017, 12:09 PM
 * <p>
 * credit to to https://github.com/Saketme/Better-Link-Movement-Method
 */

public class BetterLinkMovementExtended extends LinkMovementMethod {
    private static final Class SPAN_CLASS = ClickableSpan.class;
    private static final int LINKIFY_NONE = -2;
    private BetterLinkMovementExtended.OnLinkClickListener onLinkClickListener;
    private BetterLinkMovementExtended.OnLinkLongClickListener onLinkLongClickListener;
    private final RectF touchedLineBounds = new RectF();
    private boolean isUrlHighlighted;
    private boolean touchStartedOverLink;
    private int activeTextViewHashcode;

    private final GestureDetector gestureDetector;
    private final LinkClickGestureListener clickGestureListener = new LinkClickGestureListener();

    private BetterLinkMovementExtended(Context context) {
        gestureDetector = new GestureDetector(context, clickGestureListener);
    }

    private final class LinkClickGestureListener extends GestureDetector.SimpleOnGestureListener {
        private GestureDetector.SimpleOnGestureListener listener = null;

        @Override public boolean onDown(MotionEvent e) {
            if(listener != null) listener.onDown(e);
            return true;
        }

        @Override public boolean onSingleTapUp(MotionEvent e) {
            return listener == null || listener.onSingleTapUp(e);
        }

        @Override public void onLongPress(MotionEvent e) {
            if(listener != null) listener.onLongPress(e);
        }
    }

    private static BetterLinkMovementExtended linkify(int linkifyMask, TextView textView) {
        BetterLinkMovementExtended movementMethod = new BetterLinkMovementExtended(textView.getContext());
        addLinks(linkifyMask, movementMethod, textView);
        return movementMethod;
    }

    public static BetterLinkMovementExtended linkifyHtml(TextView textView) {
        return linkify(LINKIFY_NONE, textView);
    }

    private static BetterLinkMovementExtended linkify(int linkifyMask, ViewGroup viewGroup) {
        BetterLinkMovementExtended movementMethod = new BetterLinkMovementExtended(viewGroup.getContext());
        rAddLinks(linkifyMask, viewGroup, movementMethod);
        return movementMethod;
    }

    public static BetterLinkMovementExtended linkifyHtml(ViewGroup viewGroup) {
        return linkify(LINKIFY_NONE, viewGroup);
    }

    public void setOnLinkClickListener(OnLinkClickListener onLinkClickListener) {
        this.onLinkClickListener = onLinkClickListener;
    }

    public void setOnLinkLongClickListener(OnLinkLongClickListener onLinkLongClickListener) {
        this.onLinkLongClickListener = onLinkLongClickListener;
    }

    private static void rAddLinks(int linkifyMask, ViewGroup viewGroup, BetterLinkMovementExtended movementMethod) {
        for (int i = 0; i < viewGroup.getChildCount(); ++i) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                rAddLinks(linkifyMask, (ViewGroup) child, movementMethod);
            } else if (child instanceof TextView) {
                TextView textView = (TextView) child;
                addLinks(linkifyMask, movementMethod, textView);
            }
        }

    }

    private static void addLinks(int linkifyMask, BetterLinkMovementExtended movementMethod, TextView textView) {
        textView.setMovementMethod(movementMethod);
        if (linkifyMask != LINKIFY_NONE) {
            Linkify.addLinks(textView, linkifyMask);
        }

    }

    public boolean onTouchEvent(TextView view, Spannable text, MotionEvent event) {
        if (this.activeTextViewHashcode != view.hashCode()) {
            this.activeTextViewHashcode = view.hashCode();
            view.setAutoLinkMask(0);
        }

        BetterLinkMovementExtended.ClickableSpanWithText touchedClickableSpan = this.findClickableSpanUnderTouch(view, text, event);
        if (touchedClickableSpan != null) {
            this.highlightUrl(view, touchedClickableSpan, text);
        } else {
            this.removeUrlHighlightColor(view);
        }

        clickGestureListener.listener = new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onDown(MotionEvent e) {
                touchStartedOverLink = touchedClickableSpan != null;
                return true;
            }

            @Override public boolean onSingleTapUp(MotionEvent e) {
                if (touchedClickableSpan != null && touchStartedOverLink) {
                    dispatchUrlClick(view, touchedClickableSpan);
                    removeUrlHighlightColor(view);
                }

                touchStartedOverLink = false;
                return true;
            }

            @Override public void onLongPress(MotionEvent e) {
                if (touchedClickableSpan != null && touchStartedOverLink) {
                    dispatchUrlLongClick(view, touchedClickableSpan);
                    removeUrlHighlightColor(view);
                }

                touchStartedOverLink = false;
            }
        };

        boolean ret = gestureDetector.onTouchEvent(event);

        if(!ret && event.getAction() == MotionEvent.ACTION_UP) {
            clickGestureListener.listener = null;
            removeUrlHighlightColor(view);
            this.touchStartedOverLink = false;
            ret = true;
        }

        return ret;
    }

    private BetterLinkMovementExtended.ClickableSpanWithText findClickableSpanUnderTouch(TextView textView, Spannable text, MotionEvent event) {
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();
        touchX -= textView.getTotalPaddingLeft();
        touchY -= textView.getTotalPaddingTop();
        touchX += textView.getScrollX();
        touchY += textView.getScrollY();
        Layout layout = textView.getLayout();
        int touchedLine = layout.getLineForVertical(touchY);
        int touchOffset = layout.getOffsetForHorizontal(touchedLine, (float) touchX);
        this.touchedLineBounds.left = layout.getLineLeft(touchedLine);
        this.touchedLineBounds.top = (float) layout.getLineTop(touchedLine);
        this.touchedLineBounds.right = layout.getLineWidth(touchedLine) + this.touchedLineBounds.left;
        this.touchedLineBounds.bottom = (float) layout.getLineBottom(touchedLine);
        if (this.touchedLineBounds.contains((float) touchX, (float) touchY)) {
            Object[] spans = text.getSpans(touchOffset, touchOffset, SPAN_CLASS);
            for (Object span : spans) {
                if (span instanceof ClickableSpan) {
                    return ClickableSpanWithText.ofSpan(textView, (ClickableSpan) span);
                }
            }
            return null;
        } else {
            return null;
        }
    }

    private void highlightUrl(TextView textView, BetterLinkMovementExtended.ClickableSpanWithText spanWithText, Spannable text) {
        if (!this.isUrlHighlighted) {
            this.isUrlHighlighted = true;
            int spanStart = text.getSpanStart(spanWithText.span());
            int spanEnd = text.getSpanEnd(spanWithText.span());
            Selection.removeSelection(text);
            text.setSpan(new BackgroundColorSpan(textView.getHighlightColor()), spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            textView.setText(text);
            Selection.setSelection(text, spanStart, spanEnd);
        }
    }

    private void removeUrlHighlightColor(TextView textView) {
        if (this.isUrlHighlighted) {
            this.isUrlHighlighted = false;
            Spannable text = (Spannable) textView.getText();
            BackgroundColorSpan[] highlightSpans = text.getSpans(0, text.length(), BackgroundColorSpan.class);
            for (BackgroundColorSpan highlightSpan : highlightSpans) {
                text.removeSpan(highlightSpan);
            }
            try {
                textView.setText(text);
                Selection.removeSelection(text);
            } catch (Exception ignored) {}
        }
    }

    private void dispatchUrlClick(TextView textView, BetterLinkMovementExtended.ClickableSpanWithText spanWithText) {
        String spanUrl = spanWithText.text();
        boolean handled = this.onLinkClickListener != null && this.onLinkClickListener.onClick(textView, spanUrl);
        if (!handled) {
            spanWithText.span().onClick(textView);
        }

    }

    private void dispatchUrlLongClick(TextView textView, BetterLinkMovementExtended.ClickableSpanWithText spanWithText) {
        String spanUrl = spanWithText.text();
        if(onLinkLongClickListener != null) onLinkLongClickListener.onLongClick(textView, spanUrl);
    }

    static class ClickableSpanWithText {
        private ClickableSpan span;
        private String text;

        static BetterLinkMovementExtended.ClickableSpanWithText ofSpan(TextView textView, ClickableSpan span) {
            Spanned s = (Spanned) textView.getText();
            String text;
            if (span instanceof URLSpan) {
                text = ((URLSpan) span).getURL();
            } else {
                int start = s.getSpanStart(span);
                int end = s.getSpanEnd(span);
                text = s.subSequence(start, end).toString();
            }
            return new BetterLinkMovementExtended.ClickableSpanWithText(span, text);
        }

        private ClickableSpanWithText(ClickableSpan span, String text) {
            this.span = span;
            this.text = text;
        }

        ClickableSpan span() {
            return this.span;
        }

        String text() {
            return this.text;
        }
    }

    public interface OnLinkClickListener {
        boolean onClick(TextView view, String link);
    }

    public interface OnLinkLongClickListener {
        boolean onLongClick(TextView view, String link);
    }
}
