package com.fastaccess.github.platform.markwon.mention.internal

import com.fastaccess.github.platform.markwon.mention.Mention
import org.commonmark.node.Node
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.HtmlNodeRendererContext
import org.commonmark.renderer.html.HtmlWriter

class MentionNodeRenderer(private val context: HtmlNodeRendererContext) : NodeRenderer {
    private val html: HtmlWriter = context.writer

    override fun getNodeTypes(): Set<Class<out Node>> {
        return setOf<Class<out Node>>(Mention::class.java)
    }

    override fun render(node: Node) {
        val attributes = context.extendAttributes(node, "mention", emptyMap())
        html.tag("mention", attributes)
        renderChildren(node)
        html.tag("/mention")
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
