package com.fastaccess.helper;

import com.fastaccess.BuildConfig;

/**
 * Created by thermatk on 12.04.17.
 */

public class GithubConfigHelper {
    private static final String REDIRECT_URL = "fasthub://login";

    private static final String GITHUB_FDROID_CLIENT_ID = "290c67ea4022804763e0";
    private static final String GITHUB_FDROID_SECRET = "02c6a47c2cc25a95f0d58eba90ea1078f74e5740";

    public static String getRedirectUrl() {
        return REDIRECT_URL;
    }

    public static String getClientId() {
        if (BuildConfig.FDROID) {
            return GITHUB_FDROID_CLIENT_ID;
        } else {
            return BuildConfig.GITHUB_CLIENT_ID;
        }
    }

    public static String getSecret() {
        if (BuildConfig.FDROID) {
            return GITHUB_FDROID_SECRET;
        } else {
            return BuildConfig.GITHUB_SECRET;
        }
    }
}