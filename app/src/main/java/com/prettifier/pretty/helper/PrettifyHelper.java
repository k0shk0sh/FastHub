package com.prettifier.pretty.helper;

import android.support.annotation.NonNull;

/**
 * Created by Kosh on 25 Dec 2016, 9:12 PM
 */

public class PrettifyHelper {

    @NonNull private static String getHtmlContent(@NonNull String css, @NonNull String text, @NonNull String wrapStyle, boolean isDark) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <link rel=\"stylesheet\" href=\"./styles/" + css + "\">\n" +
                "    " + wrapStyle + "\n" +
                "<script src=\"./js/prettify.js\"></script>\n" +
                "<script src=\"./js/prettify_line_number.js\"></script>\n" +
                "</head>\n" +
                "<body style=\"" + (isDark && textTooLarge(text) ? "color:white;" : "") + "\">\n" +
                "<pre><code>" + text + "</code></pre>\n" +
                "<script>" + (textTooLarge(text) ? "" : "hljs.initHighlightingOnLoad();\nhljs.initLineNumbersOnLoad();") + "</script>\n" +
                "<script src=\"./js/scrollto.js\"></script>\n" +
                "</body>\n" +
                "</html>";
    }

    @NonNull private static final String WRAPPED_STYLE =
            "<style>\n " +
                    "pre, pre code, table {\n" +
                    "    white-space: pre-wrap !important;\n" +
                    "    word-wrap: break-all !important;\n" +
                    "    word-wrap: break-word !important;\n" +
                    "}\n" +
                    "img {\n" +
                    "    max-width: 100% !important;\n" +
                    "}\n" +
                    "ol {\n" +
                    "    margin-left: 0 !important;\n" +
                    "    padding-left: 6px !important;\n" +
                    "}\n" +
                    "ol li {\n" +
                    "    margin-left: 0  !important;\n" +
                    "    padding-left: 0  !important;\n" +
                    "    text-indent: -12px !important;\n" +
                    "}" +
                    "</style>";


    @NonNull public static String generateContent(@NonNull String source, boolean isDark, boolean wrap) {
        return getHtmlContent(getStyle(isDark), getFormattedSource(source), wrap ? WRAPPED_STYLE : "", isDark);
    }

    @NonNull private static String getFormattedSource(@NonNull String source) {
        return source.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }

    @NonNull private static String getStyle(boolean isDark) {
        return !isDark ? "prettify.css" : "prettify_dark.css";
    }

    private static boolean textTooLarge(@NonNull String text) {
        return text.length() > 304800;//>roughly 300kb ? disable highlighting to avoid crash.
    }

}
