package com.fastaccess.provider.timeline.handler;

import android.support.annotation.ColorInt;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;

import net.nightwhistler.htmlspanner.TextUtil;
import net.nightwhistler.htmlspanner.handlers.PreHandler;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;

import lombok.AllArgsConstructor;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/**
 * Created by Kosh on 22 Apr 2017, 1:07 PM
 */

@AllArgsConstructor public class PreTagHandler extends PreHandler {

    @ColorInt private final int color;
    private final boolean isPre;

    private void getPlainText(StringBuffer buffer, Object node) {
        if (node instanceof ContentNode) {
            ContentNode contentNode = (ContentNode) node;
            String text = TextUtil.replaceHtmlEntities(contentNode.getContent().toString(), true);
            buffer.append(text);
        } else if (node instanceof TagNode) {
            TagNode tagNode = (TagNode) node;
            for (Object child : tagNode.getChildren()) {
                this.getPlainText(buffer, child);
            }
        }
    }

    @Override public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end) {
        if (isPre) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("\n");//fake padding top + make sure, pre is always by itself
            getPlainText(buffer, node);
            buffer.append("\n");//fake padding bottom + make sure, pre is always by itself
            builder.append(buffer);
            builder.append("\n");
            builder.setSpan(new CodeBackgroundRoundedSpan(color), start, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("\n");
            this.appendNewLine(builder);
            this.appendNewLine(builder);
        } else {
            StringBuffer text = node.getText();
            builder.append("  ");
            builder.append(text);
            builder.append("  ");
            builder.setSpan(new BackgroundColorSpan(color), start + 1, builder.length() - 1, SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
