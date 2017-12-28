package ru.noties.markwon.renderer.html;

import android.support.annotation.NonNull;
import android.text.style.UnderlineSpan;

class UnderlineProvider implements SpannableHtmlParser.SpanProvider {

    @Override
    public Object provide(@NonNull SpannableHtmlParser.Tag tag) {
        return new UnderlineSpan();
    }
}
