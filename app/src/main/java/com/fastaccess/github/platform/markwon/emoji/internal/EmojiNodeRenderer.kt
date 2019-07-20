package com.fastaccess.github.platform.markwon.emoji.internal

import com.fastaccess.github.platform.markwon.emoji.Emoji
import org.commonmark.node.Node
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.HtmlNodeRendererContext
import org.commonmark.renderer.html.HtmlWriter

class EmojiNodeRenderer(private val context: HtmlNodeRendererContext) : NodeRenderer {
    private val html: HtmlWriter = context.writer

    override fun getNodeTypes(): Set<Class<out Node>> = setOf<Class<out Node>>(Emoji::class.java)

    override fun render(node: Node) {
        val attributes = context.extendAttributes(node, "emoji", emptyMap())
        html.tag("emoji", attributes)
        renderChildren(node)
        html.tag("/emoji")
    }

    private fun renderChildren(parent: Node) {
        var node: Node? = parent.firstChild
        while (node != null) {
            val next = node.next
            context.render(node)
            node = next
        }
    }
}
