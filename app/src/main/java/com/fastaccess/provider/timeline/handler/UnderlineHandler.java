package com.fastaccess.provider.timeline.handler;

import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;

import net.nightwhistler.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

public class UnderlineHandler extends TagNodeHandler {

    @Override public void handleTagNode(TagNode tagNode, SpannableStringBuilder spannableStringBuilder, int start, int end) {
        spannableStringBuilder.setSpan(new UnderlineSpan(), start, end, 33);
    }
}