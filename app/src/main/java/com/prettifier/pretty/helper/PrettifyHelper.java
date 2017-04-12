package com.prettifier.pretty.helper;

import android.support.annotation.NonNull;

/**
 * Created by Kosh on 25 Dec 2016, 9:12 PM
 */

public class PrettifyHelper {

    @NonNull private final static String HTML_CONTENT =
            "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <link rel=\"stylesheet\" href=\"./styles/%s\">\n" +
                    "    %s\n" +
                    "</head>\n" +
                    "<body onload=\"prettyPrint()\">\n" +
                    "<pre class=\"prettyprint linenums\">%s</pre>\n" +
                    "<script src=\"./js/prettify.js\"></script>\n" +
                    "</body>\n" +
                    "</html>";

    @NonNull private static final String WRAPPED_STYLE = "<style>\n " +
            "pre {\n" +
            "    word-wrap: break-all !important;\n" +
            "    white-space: pre-wrap !important;\n" +
            "    white-space: -moz-pre-wrap !important;\n" +
            "    white-space: -pre-wrap !important;\n" +
            "    white-space: -o-pre-wrap !important;\n" +
            "    word-wrap: break-word !important;\n" +
            "    margin: 4px 0px 4px 0px !important;\n" +
            "}\n" +
            "pre code {\n" +
            "    word-wrap: break-all !important;\n" +
            "    white-space: pre-wrap !important;\n" +
            "    white-space: -moz-pre-wrap !important;\n" +
            "    white-space: -pre-wrap !important;\n" +
            "    white-space: -o-pre-wrap !important;\n" +
            "    word-wrap: break-word !important;\n" +
            "}\n" +
            "img {\n" +
            "    max-width: 100% !important;\n" +
            "}\n" +
            "table {\n" +
            "    word-wrap: break-all !important;\n" +
            "    white-space: pre-wrap !important;\n" +
            "    white-space: -moz-pre-wrap !important;\n" +
            "    white-space: -pre-wrap !important;\n" +
            "    white-space: -o-pre-wrap !important;\n" +
            "    word-wrap: break-word !important;\n" +
            "    margin: 4px 0px 4px 0px !important;\n" +
            "}\n" +
            "</style>";


    @NonNull public static String generateContent(@NonNull String source, boolean isDark, boolean wrap) {
        return String.format(HTML_CONTENT, getStyle(isDark), wrap ? WRAPPED_STYLE : "", getFormattedSource(source));
    }

    @NonNull private static String getFormattedSource(@NonNull String source) {
        return source.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }

    @NonNull private static String getStyle(boolean isDark) {
        return !isDark ? "prettify.css" : "prettify_dark.css";
    }

}
