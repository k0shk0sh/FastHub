package com.fastaccess.provider.timeline.handler;

import android.text.SpannableStringBuilder;

import com.fastaccess.provider.emoji.EmojiManager;

import net.nightwhistler.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

/**
 * Created by Kosh on 27 May 2017, 4:54 PM
 */

public class EmojiHandler extends TagNodeHandler {

    @Override public void handleTagNode(TagNode tagNode, SpannableStringBuilder spannableStringBuilder, int i, int i1) {

    }

    @Override public void beforeChildren(TagNode node, SpannableStringBuilder builder) {
        super.beforeChildren(node, builder);
        builder.append(EmojiManager.getForAlias(node.getAttributeByName("alias")).getUnicode());
    }
}
