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
    private final static String HASH_LINK_SCRIPT = "<script>\n" +
            "function scrollTo(hash) {\n" +
            "    var element = document.getElementById('user-content-'+hash);\n" +
            "    element.scrollIntoView();\n" +
            "}" +
            "</script>";

    @NonNull public static String generateContent(@NonNull String source, @Nullable String baseUrl) {
        return generateContent(source, baseUrl, false);
    }

    @NonNull public static String generateContent(@NonNull String source, @Nullable String baseUrl, boolean wrap) {
        if (baseUrl == null) {
            return mergeContent(source, wrap);
        } else {
            return mergeContent(validateImageBaseUrl(source, baseUrl), wrap);
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
            Logger.e(src);
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
                link = "https://raw.githubusercontent.com/" + owner + "/" + repoName + "/master/" + href;
            } else {
                String formattedLink = href.replaceFirst("./", "/");
                link = "https://api.github.com/repos/" + owner + "/" + repoName +
                        (formattedLink.startsWith("/") ? formattedLink : ("/" + formattedLink));
            }
            source = source.replace("href=\"" + href + "\"", "href=\"" + link + "\"");
        }
        return source;
    }

    private static String mergeContent(@NonNull String source, boolean wrap) {
        return "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;\"/>" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"" + (wrap ? "./github_wrap.css" : "./github.css") + "\">\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                source +
                "\n<script src=\"" + (!wrap ? "./intercept-touch.js" : "") + "\"></script>\n" +
                HASH_LINK_SCRIPT + "\n" +
                "</body>\n" +
                "\n" +
                "</html>\n";
    }

}
