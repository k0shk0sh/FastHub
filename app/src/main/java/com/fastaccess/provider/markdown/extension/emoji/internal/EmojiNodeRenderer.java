package com.fastaccess.provider.markdown.extension.emoji.internal;

import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class EmojiNodeRenderer implements NodeRenderer {

    private final HtmlNodeRendererContext context;
    private final HtmlWriter html;

    public EmojiNodeRenderer(HtmlNodeRendererContext context) {
        this.context = context;
        this.html = context.getWriter();
    }

    @Override public Set<Class<? extends Node>> getNodeTypes() {
        return Collections.singleton(com.fastaccess.provider.markdown.extension.emoji.Emoji.class);
    }

    @Override public void render(Node node) {
        Map<String, String> attributes = context.extendAttributes(node, "emoji", Collections.emptyMap());
        html.tag("emoji", attributes);
        renderChildren(node);
        html.tag("/emoji");
    }

    private void renderChildren(Node parent) {
        Node node = parent.getFirstChild();
        while (node != null) {
            Node next = node.getNext();
            context.render(node);
            node = next;
        }
    }
}
