package com.fastaccess.github.platform.markwon.hashtag.internal

import com.fastaccess.github.platform.markwon.hashtag.HashTag
import org.commonmark.node.Node
import org.commonmark.renderer.NodeRenderer
import org.commonmark.renderer.html.HtmlNodeRendererContext
import org.commonmark.renderer.html.HtmlWriter

class HashTagNodeRenderer(private val context: HtmlNodeRendererContext) : NodeRenderer {
    private val html: HtmlWriter = context.writer

    override fun getNodeTypes(): Set<Class<out Node>> {
        return setOf<Class<out Node>>(HashTag::class.java)
    }

    override fun render(node: Node) {
        val attributes = context.extendAttributes(node, "hashtag", emptyMap())
        html.tag("hashtag", attributes)
        renderChildren(node)
        html.tag("/hashtag")
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
