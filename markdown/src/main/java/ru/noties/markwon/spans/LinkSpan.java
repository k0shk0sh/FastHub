package ru.noties.markwon.spans;

import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class LinkSpan extends ClickableSpan {

    public interface Resolver {
        void resolve(View view, @NonNull String link);
    }

    private final SpannableTheme theme;
    private final String link;
    private final Resolver resolver;

    public LinkSpan(@NonNull SpannableTheme theme, @NonNull String link, @NonNull Resolver resolver) {
        this.theme = theme;
        this.link = link;
        this.resolver = resolver;
    }

    @Override
    public void onClick(View widget) {
        resolver.resolve(widget, link);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        theme.applyLinkStyle(ds);
    }
}
