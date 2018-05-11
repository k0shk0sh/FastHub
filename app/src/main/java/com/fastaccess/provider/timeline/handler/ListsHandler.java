package com.fastaccess.provider.timeline.handler;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;

import com.fastaccess.helper.Logger;
import com.fastaccess.ui.widgets.SpannableBuilder;

import net.nightwhistler.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor public class ListsHandler extends TagNodeHandler {

    @Nullable private Drawable checked;
    @Nullable private Drawable unchecked;

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
        TodoItems todoItem = null;
        if (node.getChildTags() != null && node.getChildTags().length > 0) {
            for (TagNode tagNode : node.getChildTags()) {
                Logger.e(tagNode.getName(), tagNode.getText());
                if (tagNode.getName() != null && tagNode.getName().equals("input")) {
                    todoItem = new TodoItems();
                    todoItem.isChecked = tagNode.getAttributeByName("checked") != null;
                    break;
                }
            }
        }
        if ("ol".equals(getParentName(node))) {
            builder.append(String.valueOf(getMyIndex(node))).append(". ");
        } else if ("ul".equals(getParentName(node))) {
            if (todoItem != null) {
                if (checked == null || unchecked == null) {
                    builder.append(todoItem.isChecked ? "☑" : "☐");
                } else {
                    builder.append(SpannableBuilder.builder()
                            .append(todoItem.isChecked ? checked : unchecked))
                            .append(" ");
                }
            } else {
                builder.append("\u2022 ");
            }
        }
    }

    @Override public void handleTagNode(TagNode tagNode, SpannableStringBuilder spannableStringBuilder, int i, int i1) {
        appendNewLine(spannableStringBuilder);
    }

    static class TodoItems {
        boolean isChecked;
    }
}
