package ru.noties.markwon.renderer.html;

import android.support.annotation.NonNull;

import ru.noties.markwon.spans.SpannableTheme;
import ru.noties.markwon.spans.SuperScriptSpan;

class SuperScriptProvider implements SpannableHtmlParser.SpanProvider {

    private final SpannableTheme theme;

    SuperScriptProvider(SpannableTheme theme) {
        this.theme = theme;
    }

    @Override
    public Object provide(@NonNull SpannableHtmlParser.Tag tag) {
        return new SuperScriptSpan(theme);
    }
}
