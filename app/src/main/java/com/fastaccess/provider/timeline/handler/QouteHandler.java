package com.fastaccess.provider.timeline.handler;

import android.support.annotation.ColorInt;
import android.text.SpannableStringBuilder;

import com.zzhoujay.markdown.style.MarkDownQuoteSpan;

import net.nightwhistler.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

import lombok.AllArgsConstructor;

/**
 * Created by Kosh on 23 Apr 2017, 11:30 AM
 */

@AllArgsConstructor public class QouteHandler extends TagNodeHandler {

    @ColorInt private int color;

    public void beforeChildren(TagNode node, SpannableStringBuilder builder) {
        if (builder.length() > 0 && builder.charAt(builder.length() - 1) != 10) {
            this.appendNewLine(builder);
        }
    }

    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end) {
        builder.setSpan(new MarkDownQuoteSpan(color), start, builder.length(), 33);
        this.appendNewLine(builder);
        this.appendNewLine(builder);
    }

}
