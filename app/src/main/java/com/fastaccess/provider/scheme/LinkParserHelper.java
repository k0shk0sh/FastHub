package com.fastaccess.provider.scheme;

import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import java.util.ArrayList;

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
            "dashboard", "repositories", "site", "security", "contact", "about", "")
            .collect(Collectors.toCollection(ArrayList::new));


    @SafeVarargs static <T> Optional<T> returnNonNull(@NonNull T... t) {
        return Stream.of(t).filter(value -> value != null).findFirst();
    }
}
