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
                    "</head>\n" +
                    "<body onload=\"prettyPrint()\">\n" +
                    "<pre class=\"prettyprint linenums\">%s</pre>\n" +
                    "<script src=\"./js/prettify.js\"></script>\n" +
                    "</body>\n" +
                    "</html>";


    @NonNull public static String generateContent(@NonNull String source, boolean isDark) {
        return String.format(HTML_CONTENT, getStyle(isDark), getFormattedSource(source));
    }

    @NonNull private static String getFormattedSource(@NonNull String source) {
        return source.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    @NonNull private static String getStyle(boolean isDark) {
        return !isDark ? "prettify.css" : "prettify_darkG.css";
    }

}
