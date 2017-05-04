package com.zzhoujay.markdown.style;

import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * Created by zhou on 16-7-2.
 * 链接Span
 */
public class LinkSpan extends URLSpan {

    private int color;

    public LinkSpan(String url, int color) {
        super(url);
        this.color = color;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(color);
        ds.setUnderlineText(false);
    }

}
