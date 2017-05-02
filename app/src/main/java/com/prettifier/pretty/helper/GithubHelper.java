package com.prettifier.pretty.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;

import com.fastaccess.data.dao.NameParser;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kosh on 25 Dec 2016, 9:12 PM
 */

public class GithubHelper {
    private static Pattern LINK_TAG_MATCHER = Pattern.compile("href=\"(.*?)\"");
    private static Pattern IMAGE_TAG_MATCHER = Pattern.compile("src=\"(.*?)\"");

    @NonNull public static String generateContent(@NonNull String source, @Nullable String baseUrl, boolean wrap, boolean dark) {
        Logger.e(baseUrl);
        if (baseUrl == null) {
            return mergeContent(source, wrap, dark);
        } else {
            return mergeContent(validateImageBaseUrl(source, baseUrl), wrap, dark);
        }
    }

    @NonNull private static String validateImageBaseUrl(@NonNull String source, @NonNull String baseUrl) {
        NameParser nameParser = new NameParser(baseUrl);
        String owner = nameParser.getUsername();
        String repoName = nameParser.getName();
        Matcher matcher = IMAGE_TAG_MATCHER.matcher(source);
        while (matcher.find()) {
            String src = matcher.group(1).trim();
            if (src.startsWith("http://") || src.startsWith("https://")) {
                continue;
            }
            String finalSrc = "https://raw.githubusercontent.com/" + owner + "/" + repoName + "/master/" + src;
            source = source.replace("src=\"" + src + "\"", "src=\"" + finalSrc + "\"");
        }
        return validateLinks(source, baseUrl);
    }

    private static String validateLinks(@NonNull String source, @NonNull String baseUrl) {
        NameParser nameParser = new NameParser(baseUrl);
        String owner = nameParser.getUsername();
        String repoName = nameParser.getName();
        Matcher matcher = LINK_TAG_MATCHER.matcher(source);
        while (matcher.find()) {
            String href = matcher.group(1).trim();
            if (href.startsWith("#") || href.startsWith("http://") || href.startsWith("https://") || href.startsWith("mailto:")) {
                continue;
            }
            String link;
            if (!InputHelper.isEmpty(MimeTypeMap.getFileExtensionFromUrl(href))) {
                link = "https://raw.githubusercontent.com/" + owner + "/" + repoName + "/master/" + href; //assuming always master is bad :'(
            } else {
                String formattedLink = href.replaceFirst("./", "/");
                link = "https://api.github.com/repos/" + owner + "/" + repoName +
                        (formattedLink.startsWith("/") ? formattedLink : ("/" + formattedLink));
            }
            source = source.replace("href=\"" + href + "\"", "href=\"" + link + "\"");
        }
        return source;
    }

    private static String mergeContent(@NonNull String source, boolean wrap, boolean dark) {
        return "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;\"/>" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"" + getStyle(dark, wrap) + "\">\n" +
                "    <script src=\"./intercept-hash.js\"></script>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                source +
                (!wrap ? "\n<script src=\"./intercept-touch.js\"></script>\n" : "\n") +
                "</body>\n" +
                "\n" +
                "</html>\n";
    }

    private static String getStyle(boolean dark, boolean isWrap) {
        return isWrap ? dark ? "./github_wrap_dark.css" : "./github_wrap.css" : dark ? "./github_dark.css" : "./github.css";
    }

}
