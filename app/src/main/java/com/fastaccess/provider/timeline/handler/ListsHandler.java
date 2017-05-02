package com.fastaccess.provider.timeline.handler;

import android.text.SpannableStringBuilder;

import com.fastaccess.helper.Logger;

import net.nightwhistler.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

public class ListsHandler extends TagNodeHandler {

    private int getMyIndex(TagNode node) {
        if (node.getParent() == null) {
            return -1;
        } else {
            int i = 1;
            for (Object child : node.getParent().getChildren()) {
                if (child == node) {
                    return i;
                }
                if (child instanceof TagNode) {
                    TagNode childNode = (TagNode) child;
                    if ("li".equals(childNode.getName())) {
                        ++i;
                    }
                }
            }
            return -1;
        }
    }

    private String getParentName(TagNode node) {
        return node.getParent() == null ? null : node.getParent().getName();
    }

    @Override public void beforeChildren(TagNode node, SpannableStringBuilder builder) {
        TodoItems todoItems = null;
        if (node.getChildTags() != null && node.getChildTags().length > 0) {
            TagNode tagNode = node.getChildTags()[0];
            if (tagNode.getName() != null && "input".equalsIgnoreCase(tagNode.getName())) {
                todoItems = new TodoItems();
                todoItems.isChecked = tagNode.getAttributeByName("checked") != null;
            }
            Logger.e(tagNode.getName(), tagNode.getAttributeByName("checked"));
        }
        if ("ol".equals(getParentName(node))) {
            builder.append("").append(String.valueOf(getMyIndex(node))).append(". ");
        } else if ("ul".equals(getParentName(node))) {
            if (todoItems != null) {
                builder.append(todoItems.isChecked ? "☑ " : "☐ ");
            } else {
                builder.append("\u2022  ");
            }
        }
    }

    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end) {
        if (builder.length() > 0 && builder.charAt(builder.length() - 1) != '\n') {
            builder.append("\n");
        }
    }

    static class TodoItems {
        boolean isChecked;
    }
}
