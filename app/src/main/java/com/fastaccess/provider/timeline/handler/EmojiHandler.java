package com.fastaccess.provider.timeline.handler;

import android.text.SpannableStringBuilder;

import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.emoji.Emoji;
import com.fastaccess.provider.emoji.EmojiManager;

import net.nightwhistler.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

/**
 * Created by Kosh on 27 May 2017, 4:54 PM
 */

public class EmojiHandler extends TagNodeHandler {

    @Override public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end) {
        String emoji = node.getAttributeByName("alias");
        if (!InputHelper.isEmpty(emoji)) {
            Emoji unicode = EmojiManager.getForAlias(emoji);
            if (unicode != null && unicode.getUnicode() != null) {
                builder.replace(start, end, unicode.getUnicode());
            }
        }
    }

    @Override public void beforeChildren(TagNode node, SpannableStringBuilder builder) {
        super.beforeChildren(node, builder);
    }
}
