package ru.noties.markwon.renderer;

import android.support.annotation.NonNull;

import org.commonmark.node.Node;

import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.SpannableConfiguration;

public class SpannableRenderer {

    @NonNull
    public CharSequence render(@NonNull SpannableConfiguration configuration, @NonNull Node node) {
        final SpannableBuilder builder = new SpannableBuilder();
        node.accept(new SpannableMarkdownVisitor(configuration, builder));
        return builder.text();
    }
}
