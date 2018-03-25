package com.prettifier.pretty.helper;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.NameParser;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.ViewHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Kosh on 25 Dec 2016, 9:12 PM
 */

public class GithubHelper {

    @NonNull public static String generateContent(@NonNull Context context, @NonNull String source,
                                                  @Nullable String baseUrl, boolean dark,
                                                  boolean isWiki, boolean replace) {
        if (baseUrl == null) {
            return mergeContent(context, Jsoup.parse(source).html(), dark);
        } else {
            return mergeContent(context, parseReadme(source, baseUrl, isWiki), dark);
        }
    }

    @NonNull private static String parseReadme(@NonNull String source, @NonNull String baseUrl, boolean isWiki) {
        NameParser nameParser = new NameParser(baseUrl);
        String owner = nameParser.getUsername();
        String repoName = nameParser.getName();
        Uri uri = Uri.parse(baseUrl);
        ArrayList<String> paths = new ArrayList<>(uri.getPathSegments());
        StringBuilder builder = new StringBuilder();
        builder.append(owner).append("/").append(repoName).append("/");
        boolean containsMaster = paths.size() > 3;
        if (!containsMaster) {
            builder.append("master/");
        } else {
            paths.remove("blob");
        }
        paths.remove(owner);
        paths.remove(repoName);
        for (String path : paths) {
            if (!path.equalsIgnoreCase(uri.getLastPathSegment())) {
                builder.append(path).append("/");
            }
        }
        String baseLinkUrl = !isWiki ? getLinkBaseUrl(baseUrl) : baseUrl;
        return getParsedHtml(source, owner, repoName, !isWiki ? builder.toString() : baseUrl, baseLinkUrl, isWiki);
    }

    @NonNull private static String getParsedHtml(@NonNull String source, String owner, String repoName,
                                                 String builder, String baseLinkUrl, boolean isWiki) {
        Document document = Jsoup.parse(source, "");
        Elements imageElements = document.getElementsByTag("img");
        if (imageElements != null && !imageElements.isEmpty()) {
            for (Element element : imageElements) {
                String src = element.attr("src");
                if (src != null && !(src.startsWith("http://") || src.startsWith("https://"))) {
                    String finalSrc;
                    if (src.startsWith("/" + owner + "/" + repoName)) {
                        finalSrc = "https://raw.githubusercontent.com/" + src;
                    } else {
                        finalSrc = "https://raw.githubusercontent.com/" + builder + src;
                    }
                    element.attr("src", finalSrc);
                }
            }
        }
        Elements linkElements = document.getElementsByTag("a");
        if (linkElements != null && !linkElements.isEmpty()) {
            for (Element element : linkElements) {
                String href = element.attr("href");
                if (href.startsWith("#") || href.startsWith("http://") || href.startsWith("https://") || href.startsWith("mailto:")) {
                    continue;
                }
                element.attr("href", baseLinkUrl + (isWiki && href.startsWith("wiki")
                                                    ? href.replaceFirst("wiki", "") : href));
            }
        }
        return document.html();
    }

    @NonNull private static String getLinkBaseUrl(@NonNull String baseUrl) {
        NameParser nameParser = new NameParser(baseUrl);
        String owner = nameParser.getUsername();
        String repoName = nameParser.getName();
        Uri uri = Uri.parse(baseUrl);
        ArrayList<String> paths = new ArrayList<>(uri.getPathSegments());
        StringBuilder builder = new StringBuilder();
        builder.append("https://").append(uri.getAuthority()).append("/").append(owner).append("/").append(repoName).append("/");
        boolean containsMaster = paths.size() > 3 && paths.get(2).equalsIgnoreCase("blob");
        if (!containsMaster) {
            builder.append("blob/master/");
        }
        paths.remove(owner);
        paths.remove(repoName);
        for (String path : paths) {
            if (!path.equalsIgnoreCase(uri.getLastPathSegment())) {
                builder.append(path).append("/");
            }
        }
        return builder.toString();
    }

    @NonNull private static String mergeContent(@NonNull Context context, @NonNull String source, boolean dark) {
        return "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;\"/>" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"" + getStyle(dark) + "\">\n" +
                "\n" + getCodeStyle(context, dark) + "\n" +
                "    <script src=\"./intercept-hash.js\"></script>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" + source +
                "\n<script src=\"./intercept-touch.js\"></script>\n" +
                "</body>\n" +
                "\n" +
                "</html>\n";
    }

    @NonNull private static String getStyle(boolean dark) {
        return dark ? "./github_dark.css" : "./github.css";
    }

    @NonNull private static String getCodeStyle(@NonNull Context context, boolean isDark) {
        if (!isDark) return "";
        String primaryColor = getCodeBackgroundColor(context);
        String accentColor = "#" + Integer.toHexString(ViewHelper.getAccentColor(context)).substring(2).toUpperCase();
        return "<style>\n" +
                "body .highlight pre, body pre {\n" +
                "background-color: " + primaryColor + " !important;\n" +
                (PrefGetter.getThemeType(context) == PrefGetter.AMLOD ? "border: solid 1px " + accentColor + " !important;\n" : "") +
                "}\n" +
                "</style>";
    }

    @NonNull private static String getCodeBackgroundColor(@NonNull Context context) {
        @PrefGetter.ThemeType int themeType = PrefGetter.getThemeType();
        if (themeType == PrefGetter.BLUISH) {
            return "#" + Integer.toHexString(ViewHelper.getPrimaryDarkColor(context)).substring(2).toUpperCase();
        }
        return "#" + Integer.toHexString(ViewHelper.getPrimaryColor(context)).substring(2).toUpperCase();
    }

}