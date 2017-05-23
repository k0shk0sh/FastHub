package com.fastaccess.helper;

import com.fastaccess.BuildConfig;

/**
 * Created by thermatk on 12.04.17.
 */

public class GithubConfigHelper {
    private static final String REDIRECT_URL = "fasthub://login";

    public static String getRedirectUrl() {
        return REDIRECT_URL;
    }

    public static String getClientId() {
        return BuildConfig.GITHUB_CLIENT_ID;
    }

    public static String getSecret() {
        return BuildConfig.GITHUB_SECRET;
    }
}