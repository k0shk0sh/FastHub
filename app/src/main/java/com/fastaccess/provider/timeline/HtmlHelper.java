package com.fastaccess.provider.timeline;


import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.HapticFeedbackConstants;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.fastaccess.R;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.provider.scheme.SchemeParser;


/**
 * Created by Kosh on 21 Apr 2017, 11:24 PM
 */

public class HtmlHelper {

    public static void htmlIntoTextView(@NonNull TextView textView, @NonNull String html, int width) {
        MarkDownProvider.setMdText(textView, html);
        registerClickEvent(textView);

    }

    private static void registerClickEvent(@NonNull TextView textView) {
        BetterLinkMovementExtended betterLinkMovementMethod = BetterLinkMovementExtended.linkifyHtml(textView);
        betterLinkMovementMethod.setOnLinkClickListener((view, url) -> {
            if (url.startsWith("@")) {
                url = LinkParserHelper.PROTOCOL_HTTPS + "://" + LinkParserHelper.HOST_DEFAULT + "/" + url.replace("@", "");
            } else if (url.startsWith("#")) {

            }
            Logger.e(url);
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
