package com.fastaccess.markdown.spans

import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder

import com.fastaccess.markdown.widget.SpannableBuilder
import net.nightwhistler.htmlspanner.SpanStack

import net.nightwhistler.htmlspanner.TagNodeHandler

import org.htmlcleaner.TagNode

class ListsHandler(
        private val checked: Drawable? = null,
        private val unchecked: Drawable? = null

) : TagNodeHandler() {
    private fun getMyIndex(node: TagNode): Int {
        if (node.parent == null) {
            return -1
        } else {
            var i = 1
            for (child in node.parent.children) {
                if (child === node) {
                    return i
                }
                if (child is TagNode) {
                    if ("li" == child.name) {
                        ++i
                    }
                }
            }
            return -1
        }
    }

    private fun getParentName(node: TagNode): String? {
        return if (node.parent == null) null else node.parent.name
    }

    override fun beforeChildren(node: TagNode?, builder: SpannableStringBuilder?, spanStack: SpanStack?) {
        node?.let { n ->
            var todoItem: TodoItems? = null
            if (n.childTags?.isNotEmpty() == true) {
                for (tagNode in n.childTags) {
                    if (tagNode.name != null && tagNode.name == "input") {
                        todoItem = TodoItems()
                        todoItem.isChecked = tagNode.getAttributeByName("checked") != null
                        break
                    }
                }
            }
            when {
                "ol" == getParentName(n) -> builder?.append(getMyIndex(n).toString())?.append(". ")
                "ul" == getParentName(n) -> if (todoItem != null) {
                    if (checked == null || unchecked == null) {
                        builder?.append(if (todoItem.isChecked) "☑" else "☐")
                    } else {
                        builder?.append(SpannableBuilder.builder()
                                .append(if (todoItem.isChecked) checked else unchecked))
                                ?.append(" ")
                    }
                } else {
                    builder?.append("\u2022 ")
                }
                else -> null
            }

        }
    }

    override fun handleTagNode(node: TagNode?, builder: SpannableStringBuilder?, start: Int, end: Int, spanStack: SpanStack?) {
        builder?.let(this::appendNewLine)
    }

    internal class TodoItems {
        var isChecked: Boolean = false
    }
}
