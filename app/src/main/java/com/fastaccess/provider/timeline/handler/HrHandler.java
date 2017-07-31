package com.fastaccess.provider.timeline.handler;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import net.nightwhistler.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

import lombok.AllArgsConstructor;

/**
 * Created by kosh on 30/07/2017.
 */

@AllArgsConstructor public class HrHandler extends TagNodeHandler {

    private final int color;
    private final int width;


    @Override public void handleTagNode(TagNode tagNode, SpannableStringBuilder spannableStringBuilder, int i, int i1) {
        spannableStringBuilder.append(" ");
        spannableStringBuilder.setSpan(new UnderLineSpan(color, width), i, i1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append("\n");
    }

}
