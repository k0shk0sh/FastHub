package com.fastaccess.provider.timeline.handler;

import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;

import net.nightwhistler.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

public class SubScriptHandler extends TagNodeHandler {

    @Override public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end) {
        builder.setSpan(new SubscriptSpan(), start, end, 33);
        builder.setSpan(new RelativeSizeSpan(0.8f), start, end, 33);
    }
}