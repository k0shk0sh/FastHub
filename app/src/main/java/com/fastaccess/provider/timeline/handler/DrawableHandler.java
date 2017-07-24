package com.fastaccess.provider.timeline.handler;

import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.provider.timeline.handler.drawable.DrawableGetter;

import net.nightwhistler.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

import lombok.AllArgsConstructor;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/**
 * Created by Kosh on 22 Apr 2017, 1:09 PM
 */

@AllArgsConstructor public class DrawableHandler extends TagNodeHandler {

    private TextView textView;

    @SuppressWarnings("ConstantConditions") private boolean isNull() {
        return textView == null;
    }

    @Override public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end) {
        String src = node.getAttributeByName("src");
        boolean isGif = "gif".equalsIgnoreCase(node.getAttributeByName("alt"));
        Logger.e(isGif);
        if (!InputHelper.isEmpty(src)) {
            builder.append("￼");
            if (isNull()) return;
            DrawableGetter imageGetter = new DrawableGetter(textView);
            builder.setSpan(new ImageSpan(imageGetter.getDrawable(src)), start, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            appendNewLine(builder);
        }
    }

}
