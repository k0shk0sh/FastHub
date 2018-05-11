package com.fastaccess.provider.timeline.handler;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.LeadingMarginSpan;

import net.nightwhistler.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

/**
 * Created by Kosh on 29 Apr 2017, 11:59 PM
 */

public class MarginHandler extends TagNodeHandler {

    public void beforeChildren(TagNode node, SpannableStringBuilder builder) {
        if (builder.length() > 0 && builder.charAt(builder.length() - 1) != 10) { //'10 = \n'
            this.appendNewLine(builder);
        }
    }

    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end) {
        builder.setSpan(new LeadingMarginSpan.Standard(30), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.appendNewLine(builder);
    }
}
