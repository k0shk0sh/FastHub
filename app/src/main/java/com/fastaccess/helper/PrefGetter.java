package com.fastaccess.helper;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.fastaccess.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Kosh on 10 Nov 2016, 3:43 PM
 */

public class PrefGetter {

    public static final int AUTO = 0;
    public static final int LIGHT = 1;
    public static final int DARK = 2;

    @IntDef({
            AUTO,
            LIGHT,
            DARK,
    })
    @Retention(RetentionPolicy.SOURCE) @interface ThemeType {}


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

    public static boolean isRVAnimationEnabled() {
        return PrefHelper.getBoolean("recylerViewAnimation");
    }

    public static int getNotificationTaskDuration(@NonNull Context context) {
        String s = PrefHelper.getString("notificationTime");
        if (!InputHelper.isEmpty(s)) {
            if (s.equalsIgnoreCase(context.getString(R.string.thirty_minutes))) {
                return 30 * 60;
            } else if (s.equalsIgnoreCase(context.getString(R.string.twenty_minutes))) {
                return 20 * 60;
            } else if (s.equalsIgnoreCase(context.getString(R.string.ten_minutes))) {
                return 10 * 60;
            } else if (s.equalsIgnoreCase(context.getString(R.string.five_minutes))) {
                return 5 * 60;
            } else if (s.equalsIgnoreCase(context.getString(R.string.one_minute))) {
                return 60;
            } else if (s.equalsIgnoreCase(context.getString(R.string.turn_off))) {
                return -1;
            }
        }
        return 0;
    }

    public static boolean isTwiceBackButtonDisabled() {
        return PrefHelper.getBoolean("back_button");
    }

    public static boolean isRectAvatar() {
        return PrefHelper.getBoolean("rect_avatar");
    }

    public static boolean isMarkAsReadEnabled() {
        return PrefHelper.getBoolean("markNotificationAsRead");
    }

    @ThemeType public static int getThemeType(@NonNull Context context) {
        return getThemeType(context.getResources());
    }

    @ThemeType public static int getThemeType(@NonNull Resources resources) {
        String appTheme = PrefHelper.getString("appTheme");
        if (!InputHelper.isEmpty(appTheme)) {
            if (appTheme.equalsIgnoreCase(resources.getString(R.string.auto_theme_mode))) {
                return AUTO;
            } else if (appTheme.equalsIgnoreCase(resources.getString(R.string.dark_theme_mode))) {
                return DARK;
            } else if (appTheme.equalsIgnoreCase(resources.getString(R.string.light_theme_mode))) {
                return LIGHT;
            } /* add future themes here */
        }
        return LIGHT;
    }
}
