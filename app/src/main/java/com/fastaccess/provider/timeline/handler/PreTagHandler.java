package com.fastaccess.provider.timeline.handler;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;

import com.fastaccess.helper.PrefGetter;

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
    @PrefGetter.ThemeType private int theme;

    private void getPlainText(StringBuffer buffer, Object node) {
        if (node instanceof ContentNode) {
            ContentNode contentNode = (ContentNode) node;
            String text = contentNode.getContent().toString();
            buffer.append(text);
        } else if (node instanceof TagNode) {
            TagNode tagNode = (TagNode) node;
            for (Object child : tagNode.getChildren()) {
                this.getPlainText(buffer, child);
            }
        }
    }

    private String replace(String text) {
        return text.replaceAll("&nbsp;", "\u00A0")
                .replaceAll("&amp;", "&")
                .replaceAll("&quot;", "\"")
                .replaceAll("&cent;", "¢")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&sect;", "§")
                .replaceAll("&ldquo;", "“")
                .replaceAll("&rdquo;", "”")
                .replaceAll("&lsquo;", "‘")
                .replaceAll("&rsquo;", "’")
                .replaceAll("&ndash;", "\u2013")
                .replaceAll("&mdash;", "\u2014")
                .replaceAll("&horbar;", "\u2015");
    }

    @Override public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end) {
        if (isPre) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("\n");//fake padding top + make sure, pre is always by itself
            getPlainText(buffer, node);
            buffer.append("\n");//fake padding bottom + make sure, pre is always by itself
            builder.append(replace(buffer.toString()));
            builder.append("\n");
            builder.setSpan(new CodeBackgroundRoundedSpan(color), start, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append("\n");
            this.appendNewLine(builder);
            this.appendNewLine(builder);
        } else {
            StringBuffer text = node.getText();
            builder.append(" ");
            builder.append(replace(text.toString()));
            builder.append(" ");
            final int stringStart = start + 1;
            final int stringEnd = builder.length() - 1;
            builder.setSpan(new BackgroundColorSpan(color), stringStart, stringEnd, SPAN_EXCLUSIVE_EXCLUSIVE);
            if (theme == PrefGetter.LIGHT) {
                builder.setSpan(new ForegroundColorSpan(Color.RED), stringStart, stringEnd, SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            builder.setSpan(new TypefaceSpan("monospace"), stringStart, stringEnd, SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
