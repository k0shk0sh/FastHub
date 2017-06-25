package com.prettifier.pretty.helper;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.NameParser;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.ViewHelper;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kosh on 25 Dec 2016, 9:12 PM
 */

public class GithubHelper {
    private static Pattern LINK_TAG_MATCHER = Pattern.compile("href=\"(.*?)\"");
    private static Pattern IMAGE_TAG_MATCHER = Pattern.compile("src=\"(.*?)\"");

    @NonNull public static String generateContent(@NonNull Context context, @NonNull String source, @Nullable String baseUrl, boolean dark) {
        Logger.e(baseUrl);
        if (baseUrl == null) {
            return mergeContent(context, source, dark);
        } else {
            return mergeContent(context, validateImageBaseUrl(source, baseUrl), dark);
        }
    }

    @NonNull private static String validateImageBaseUrl(@NonNull String source, @NonNull String baseUrl) {
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
        Matcher matcher = IMAGE_TAG_MATCHER.matcher(source);
        while (matcher.find()) {
            String src = matcher.group(1).trim();
            if (src.startsWith("http://") || src.startsWith("https://")) {
                continue;
            }
            String finalSrc;
            Logger.e(src);
            if (src.startsWith("/" + owner + "/" + repoName)) {
                finalSrc = "https://raw.githubusercontent.com/" + src;
            } else {
                finalSrc = "https://raw.githubusercontent.com/" + builder.toString() + src;
            }
            Logger.e(finalSrc);
            source = source.replace("src=\"" + src + "\"", "src=\"" + finalSrc
                    .replace("raw/", "master/").replaceAll("//", "/") + "\"");
        }
        return validateLinks(source, baseUrl);
    }

    @NonNull private static String validateLinks(@NonNull String source, @NonNull String baseUrl) {
        NameParser nameParser = new NameParser(baseUrl);
        String owner = nameParser.getUsername();
        String repoName = nameParser.getName();
        Matcher matcher = LINK_TAG_MATCHER.matcher(source);
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
        while (matcher.find()) {
            String href = matcher.group(1).trim();
            if (href.startsWith("#") || href.startsWith("http://") || href.startsWith("https://") || href.startsWith("mailto:")) {
                continue;
            }
            String link = builder.toString() + "" + href;
            source = source.replace("href=\"" + href + "\"", "href=\"" + link + "\"");
        }
        return source;
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
                "<body>\n" +
                source +
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
