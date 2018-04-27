package com.fastaccess.provider.timeline;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.HapticFeedbackConstants;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.fastaccess.R;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.provider.timeline.handler.BetterLinkMovementExtended;
import com.fastaccess.provider.timeline.handler.DrawableHandler;
import com.fastaccess.provider.timeline.handler.EmojiHandler;
import com.fastaccess.provider.timeline.handler.HeaderHandler;
import com.fastaccess.provider.timeline.handler.HrHandler;
import com.fastaccess.provider.timeline.handler.ItalicHandler;
import com.fastaccess.provider.timeline.handler.LinkHandler;
import com.fastaccess.provider.timeline.handler.ListsHandler;
import com.fastaccess.provider.timeline.handler.MarginHandler;
import com.fastaccess.provider.timeline.handler.PreTagHandler;
import com.fastaccess.provider.timeline.handler.QuoteHandler;
import com.fastaccess.provider.timeline.handler.StrikethroughHandler;
import com.fastaccess.provider.timeline.handler.SubScriptHandler;
import com.fastaccess.provider.timeline.handler.SuperScriptHandler;
import com.fastaccess.provider.timeline.handler.TableHandler;
import com.fastaccess.provider.timeline.handler.UnderlineHandler;

import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.handlers.BoldHandler;


/**
 * Created by Kosh on 21 Apr 2017, 11:24 PM
 */

public class HtmlHelper {

    public static void htmlIntoTextView(@NonNull TextView textView, @NonNull String html, int width) {
        registerClickEvent(textView);
        textView.setText(initHtml(textView, width).fromHtml(format(html).toString()));
    }

    private static void registerClickEvent(@NonNull TextView textView) {
        BetterLinkMovementExtended betterLinkMovementMethod = BetterLinkMovementExtended.linkifyHtml(textView);
        betterLinkMovementMethod.setOnLinkClickListener((view, url) -> {
            SchemeParser.launchUri(view.getContext(), Uri.parse(url));
            return true;
        });
        betterLinkMovementMethod.setOnLinkLongClickListener((view, url) -> {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            PopupMenu menu = new PopupMenu(view.getContext(), view);
            menu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.copy:
                        AppHelper.copyToClipboard(view.getContext(), url);
                        return true;
                    case R.id.open:
                        SchemeParser.launchUri(view.getContext(), Uri.parse(url));
                        return true;
                    case R.id.open_new_window:
                        SchemeParser.launchUri(view.getContext(), Uri.parse(url), false, true);
                        return true;
                    default:
                        return false;
                }
            });
            menu.inflate(R.menu.link_popup_menu);
            menu.show();
            return true;
        });
    }

    private static HtmlSpanner initHtml(@NonNull TextView textView, int width) {
        @PrefGetter.ThemeType int theme = PrefGetter.getThemeType();
        @ColorInt int windowBackground = getWindowBackground(theme);
        Drawable checked = ContextCompat.getDrawable(textView.getContext(), R.drawable.ic_checkbox_small);
        Drawable unchecked = ContextCompat.getDrawable(textView.getContext(), R.drawable.ic_checkbox_empty_small);
        HtmlSpanner mySpanner = new HtmlSpanner();
        mySpanner.setStripExtraWhiteSpace(true);
        mySpanner.registerHandler("pre", new PreTagHandler(windowBackground, true, theme));
        mySpanner.registerHandler("code", new PreTagHandler(windowBackground, false, theme));
        mySpanner.registerHandler("img", new DrawableHandler(textView, width));
        mySpanner.registerHandler("g-emoji", new EmojiHandler());
        mySpanner.registerHandler("blockquote", new QuoteHandler(windowBackground));
        mySpanner.registerHandler("b", new BoldHandler());
        mySpanner.registerHandler("strong", new BoldHandler());
        mySpanner.registerHandler("i", new ItalicHandler());
        mySpanner.registerHandler("em", new ItalicHandler());
        mySpanner.registerHandler("ul", new MarginHandler());
        mySpanner.registerHandler("ol", new MarginHandler());
        mySpanner.registerHandler("li", new ListsHandler(checked, unchecked));
        mySpanner.registerHandler("u", new UnderlineHandler());
        mySpanner.registerHandler("strike", new StrikethroughHandler());
        mySpanner.registerHandler("ins", new UnderlineHandler());
        mySpanner.registerHandler("del", new StrikethroughHandler());
        mySpanner.registerHandler("sub", new SubScriptHandler());
        mySpanner.registerHandler("sup", new SuperScriptHandler());
        mySpanner.registerHandler("a", new LinkHandler());
        mySpanner.registerHandler("hr", new HrHandler(windowBackground, width, false));
        mySpanner.registerHandler("emoji", new EmojiHandler());
        mySpanner.registerHandler("mention", new LinkHandler());
        mySpanner.registerHandler("h1", new HeaderHandler(1.5F));
        mySpanner.registerHandler("h2", new HeaderHandler(1.4F));
        mySpanner.registerHandler("h3", new HeaderHandler(1.3F));
        mySpanner.registerHandler("h4", new HeaderHandler(1.2F));
        mySpanner.registerHandler("h5", new HeaderHandler(1.1F));
        mySpanner.registerHandler("h6", new HeaderHandler(1.0F));
        if (width > 0) {
            TableHandler tableHandler = new TableHandler();
            tableHandler.setTextColor(ViewHelper.generateTextColor(windowBackground));
            tableHandler.setTableWidth(width);
            mySpanner.registerHandler("table", tableHandler);
        }
        return mySpanner;
    }

    @ColorInt public static int getWindowBackground(@PrefGetter.ThemeType int theme) {
        if (theme == PrefGetter.AMLOD) {
            return Color.parseColor("#0B162A");
        } else if (theme == PrefGetter.BLUISH) {
            return Color.parseColor("#111C2C");
        } else if (theme == PrefGetter.DARK) {
            return Color.parseColor("#22252A");
        } else {
            return Color.parseColor("#EEEEEE");
        }
    }

    private static final String TOGGLE_START = "<span class=\"email-hidden-toggle\">";

    private static final String TOGGLE_END = "</span>";

    private static final String REPLY_START = "<div class=\"email-quoted-reply\">";

    private static final String REPLY_END = "</div>";

    private static final String SIGNATURE_START = "<div class=\"email-signature-reply\">";

    private static final String SIGNATURE_END = "</div>";

    private static final String HIDDEN_REPLY_START = "<div class=\"email-hidden-reply\" style=\" display:none\">";

    private static final String HIDDEN_REPLY_END = "</div>";

    private static final String BREAK = "<br>";

    private static final String PARAGRAPH_START = "<p>";

    private static final String PARAGRAPH_END = "</p>";

    //https://github.com/k0shk0sh/GitHubSdk/blob/master/library/src/main/java/com/meisolsson/githubsdk/core/HtmlUtils.java
    @NonNull public static CharSequence format(final String html) {
        if (html == null || html.length() == 0) return "";
        StringBuilder formatted = new StringBuilder(html);
        strip(formatted, TOGGLE_START, TOGGLE_END);
        strip(formatted, SIGNATURE_START, SIGNATURE_END);
        strip(formatted, REPLY_START, REPLY_END);
        strip(formatted, HIDDEN_REPLY_START, HIDDEN_REPLY_END);
        if (replace(formatted, PARAGRAPH_START, BREAK)) replace(formatted, PARAGRAPH_END, BREAK);
        trim(formatted);
        return formatted;
    }

    private static void strip(final StringBuilder input, final String prefix, final String suffix) {
        int start = input.indexOf(prefix);
        while (start != -1) {
            int end = input.indexOf(suffix, start + prefix.length());
            if (end == -1)
                end = input.length();
            input.delete(start, end + suffix.length());
            start = input.indexOf(prefix, start);
        }
    }

    private static boolean replace(final StringBuilder input, final String from, final String to) {
        int start = input.indexOf(from);
        if (start == -1) return false;
        final int fromLength = from.length();
        final int toLength = to.length();
        while (start != -1) {
            input.replace(start, start + fromLength, to);
            start = input.indexOf(from, start + toLength);
        }
        return true;
    }

    private static void trim(final StringBuilder input) {
        int length = input.length();
        int breakLength = BREAK.length();
        while (length > 0) {
            if (input.indexOf(BREAK) == 0) input.delete(0, breakLength);
            else if (length >= breakLength && input.lastIndexOf(BREAK) == length - breakLength) input.delete(length - breakLength, length);
            else if (Character.isWhitespace(input.charAt(0))) input.deleteCharAt(0);
            else if (Character.isWhitespace(input.charAt(length - 1))) input.deleteCharAt(length - 1);
            else break;
            length = input.length();
        }
    }
}
