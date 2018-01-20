package com.fastaccess.provider.timeline.handler;

import android.graphics.Color;
import android.text.SpannableStringBuilder;

import com.zzhoujay.markdown.style.LinkSpan;

import net.nightwhistler.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

/**
 * Created by Kosh on 10 May 2017, 8:46 PM
 */

public class LinkHandler extends TagNodeHandler {
    private final static int linkColor = Color.parseColor("#4078C0");

    @Override public void handleTagNode(TagNode node, SpannableStringBuilder spannableStringBuilder, int start, int end) {
        String href = node.getAttributeByName("href");
        if (href != null) {
            spannableStringBuilder.setSpan(new LinkSpan(href, linkColor), start, end, 33);
        } else if (node.getText() != null) {
            spannableStringBuilder.setSpan(new LinkSpan("https://github.com/" + node.getText().toString(), linkColor), start, end, 33);
        }
    }
}
