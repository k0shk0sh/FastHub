package com.fastaccess.provider.timeline.handler;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.spans.CenterSpan;

import org.htmlcleaner.TagNode;

import lombok.AllArgsConstructor;

/**
 * Created by kosh on 30/07/2017.
 */

@AllArgsConstructor public class HrHandler extends TagNodeHandler {

    private final int color;
    private final int width;
    private final boolean isHeader;

    @Override public void handleTagNode(TagNode tagNode, SpannableStringBuilder spannableStringBuilder, int i, int i1) {
        spannableStringBuilder.append("\n");
        SpannableStringBuilder builder = new SpannableStringBuilder("$");
        HrSpan hrSpan = new HrSpan(color, width);
        builder.setSpan(hrSpan, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new CenterSpan(), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append("\n");
        spannableStringBuilder.append(builder);
    }

}
