package com.fastaccess.provider.scheme;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ObjectsCompat;
import com.fastaccess.helper.PrefGetter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Kosh on 11 Apr 2017, 10:02 PM
 */

public class LinkParserHelper {
    public static final String HOST_DEFAULT = "github.com";
    static final String HOST_GISTS = "gist.github.com";
    static final String HOST_GISTS_RAW = "gist.githubusercontent.com";
    static final String RAW_AUTHORITY = "raw.githubusercontent.com";
    static final String API_AUTHORITY = "api.github.com";
    static final String PROTOCOL_HTTPS = "https";
    static final List<String> IGNORED_LIST = Arrays.asList("notifications", "settings", "blog",
            "explore", "dashboard", "repositories", "logout", "sessions", "site", "security",
            "contact", "about", "logos", "login", "pricing", "");

    @SafeVarargs static <T> Optional<T> returnNonNull(@NonNull T... t) {
        return Stream.of(t).filter(ObjectsCompat::nonNull).findFirst();
    }

    @NonNull static Uri getBlobBuilder(@NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        Uri.Builder urlBuilder = null;
        if (uri.getAuthority().equalsIgnoreCase(HOST_DEFAULT)) {
            String owner = segments.get(0);
            String repo = segments.get(1);
            String branch = segments.get(3);
            urlBuilder = new Uri.Builder();
            urlBuilder.scheme("https")
                    .authority(API_AUTHORITY)
                    .appendPath("repos")
                    .appendPath(owner)
                    .appendPath(repo)
                    .appendPath("contents");
            for (int i = 4; i < segments.size(); i++) {
                urlBuilder.appendPath(segments.get(i));
            }
            if (uri.getQueryParameterNames() != null) {
                for (String query : uri.getQueryParameterNames()) {
                    urlBuilder.appendQueryParameter(query, uri.getQueryParameter(query));
                }
            }
            if (uri.getEncodedFragment() != null) {
                urlBuilder.encodedFragment(uri.getEncodedFragment());
            }
            urlBuilder.appendQueryParameter("ref", branch);
        }
        return urlBuilder != null ? urlBuilder.build() : uri;
    }

    public static boolean isEnterprise(@Nullable String url) {
        if (InputHelper.isEmpty(url) || !PrefGetter.isEnterprise()) return false;
        String enterpriseUrl = PrefGetter.getEnterpriseUrl().toLowerCase();
        url = url.toLowerCase();
        return url.equalsIgnoreCase(enterpriseUrl) || url.startsWith(enterpriseUrl) || url.startsWith(getEndpoint(enterpriseUrl))
                || url.contains(enterpriseUrl) || enterpriseUrl.contains(url);
    }

    public static String stripScheme(@NonNull String url) {
        try {
            Uri uri = Uri.parse(url);
            return !InputHelper.isEmpty(uri.getAuthority()) ? uri.getAuthority() : url;
        } catch (Exception ignored) {}
        return url;
    }

    @NonNull public static String getEndpoint(@NonNull String url) {
        if (url.startsWith("http://")) {
            url = url.replace("http://", "https://");
        }
        if (!url.startsWith("https://")) {
            url = "https://" + url;
        }
        return getEnterpriseUrl(url);
    }

    @NonNull private static String getEnterpriseUrl(@NonNull String url) {
        if (url.endsWith("/api/v3/")) {
            return url;
        } else if (url.endsWith("/api/")) {
            return url + "v3/";
        } else if (url.endsWith("/api")) {
            return url + "/v3/";
        } else if (url.endsWith("/api/v3")) {
            return url + "/";
        } else if (!url.endsWith("/")) {
            return url + "/api/v3/";
        } else if (url.endsWith("/")) {
            return url + "api/v3/";
        }
        return url;
    }

    public static String getEnterpriseGistUrl(@NonNull String url, boolean isEnterprise) {
        if (isEnterprise) {
            Uri uri = Uri.parse(url);
            boolean isGist = uri == null || uri.getPathSegments() == null ? url.contains("gist/") : uri.getPathSegments().get(0).equals("gist");
            if (isGist) {
                String enterpriseUrl = PrefGetter.getEnterpriseUrl();
                if (!url.contains(enterpriseUrl + "/raw/")) {
                    url = url.replace(enterpriseUrl, enterpriseUrl + "/raw");
                }
            }
        }
        return url;
    }
}
