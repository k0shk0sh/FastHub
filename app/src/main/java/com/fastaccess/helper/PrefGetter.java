package com.fastaccess.helper;

import android.support.annotation.NonNull;

/**
 * Created by Kosh on 10 Nov 2016, 3:43 PM
 */

public class PrefGetter {
    private static final String ADS = "enable_ads";
    private static final String TOKEN = "token";
    private static final String USER_ICON_GUIDE = "user_icon_guide";
    private static final String RELEASE_GUIDE = "release_guide";
    private static final String FILE_OPTION_GUIDE = "file_option_guide";
    private static final String COMMENTS_GUIDE = "comments_guide";
    private static final String REPO_GUIDE = "repo_guide";
    private static final String MARKDOWNDOWN_GUIDE = "markdowndown_guide";

    public static void setToken(@NonNull String token) {
        PrefHelper.set(TOKEN, token);
    }

    public static String getToken() {
        return PrefHelper.getString(TOKEN);
    }

    public static boolean isAdsEnabled() {
        return PrefHelper.getBoolean(ADS);
    }

    public static void setAdsEnabled(boolean isEnabled) {
        PrefHelper.set(ADS, isEnabled);
    }

    public static void clear() {
        PrefHelper.clearPrefs();
    }

    public static boolean isUserIconGuideShowed() {
        boolean isShowed = PrefHelper.getBoolean(USER_ICON_GUIDE);
        PrefHelper.set(USER_ICON_GUIDE, true);
        return isShowed;
    }

    public static boolean isReleaseHintShow() {
        boolean isShowed = PrefHelper.getBoolean(RELEASE_GUIDE);
        PrefHelper.set(RELEASE_GUIDE, true);
        return isShowed;
    }

    public static boolean isFileOptionHintShow() {
        boolean isShowed = PrefHelper.getBoolean(FILE_OPTION_GUIDE);
        PrefHelper.set(FILE_OPTION_GUIDE, true);
        return isShowed;
    }

    public static boolean isCommentHintShowed() {
        boolean isShowed = PrefHelper.getBoolean(COMMENTS_GUIDE);
        PrefHelper.set(COMMENTS_GUIDE, true);
        return isShowed;
    }

    public static boolean isRepoGuideShowed() {
        boolean isShowed = PrefHelper.getBoolean(REPO_GUIDE);
        PrefHelper.set(REPO_GUIDE, true);
        return isShowed;
    }

    public static boolean isEditorHintShowed() {
        boolean isShowed = PrefHelper.getBoolean(MARKDOWNDOWN_GUIDE);
        PrefHelper.set(MARKDOWNDOWN_GUIDE, true);
        return isShowed;
    }
}
