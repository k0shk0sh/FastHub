package com.fastaccess.provider.scheme;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.fastaccess.helper.ObjectsCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 11 Apr 2017, 10:02 PM
 */

class LinkParserHelper {
    static final String HOST_DEFAULT = "github.com";
    static final String HOST_GISTS = "gist.github.com";
    static final String HOST_GISTS_RAW = "gist.githubusercontent.com";
    static final String RAW_AUTHORITY = "raw.githubusercontent.com";
    static final String API_AUTHORITY = "api.github.com";
    static final String PROTOCOL_HTTPS = "https";
    static final ArrayList<String> IGNORED_LIST = Stream.of("notifications", "settings", "blog", "explore",
            "dashboard", "repositories", "logout", "sessions", "site", "security", "contact", "about", "logos", "login", "")
            .collect(Collectors.toCollection(ArrayList::new));


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
}
