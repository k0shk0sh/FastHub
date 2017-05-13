package com.fastaccess.helper;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kosh on 10 Nov 2016, 3:43 PM
 */

public class PrefGetter {

    public static final int LIGHT = 1;
    public static final int DARK = 2;

    public static final int RED = 1;
    public static final int PINK = 2;
    public static final int PURPLE = 3;
    public static final int DEEP_PURPLE = 4;
    public static final int INDIGO = 5;
    public static final int BLUE = 6;
    public static final int LIGHT_BLUE = 7;
    public static final int CYAN = 8;
    public static final int TEAL = 9;
    public static final int GREEN = 10;
    public static final int LIGHT_GREEN = 11;
    public static final int LIME = 12;
    public static final int YELLOW = 13;
    public static final int AMBER = 14;
    public static final int ORANGE = 15;
    public static final int DEEP_ORANGE = 16;

    @IntDef({
            LIGHT,
            DARK,
    })
    @Retention(RetentionPolicy.SOURCE) @interface ThemeType {}

    @IntDef({
            RED,
            PINK,
            PURPLE,
            DEEP_PURPLE,
            INDIGO,
            BLUE,
            LIGHT_BLUE,
            CYAN,
            TEAL,
            GREEN,
            LIGHT_GREEN,
            LIME,
            YELLOW,
            AMBER,
            ORANGE,
            DEEP_ORANGE
    })
    @Retention(RetentionPolicy.SOURCE) @interface ThemeColor {}


    private static final String WHATS_NEW_VERSION = "whats_new";
    private static final String ADS = "enable_ads";
    private static final String TOKEN = "token";
    private static final String USER_ICON_GUIDE = "user_icon_guide";
    private static final String RELEASE_GUIDE = "release_guide";
    private static final String FILE_OPTION_GUIDE = "file_option_guide";
    private static final String COMMENTS_GUIDE = "comments_guide";
    private static final String REPO_GUIDE = "repo_guide";
    private static final String MARKDOWNDOWN_GUIDE = "markdowndown_guide";
    private static final String HOME_BUTTON_GUIDE = "home_button_guide";
    private static final String NAV_DRAWER_GUIDE = "nav_drawer_guide";
    private static final String FAB_LONG_PRESS_REPO_GUIDE = "fab_long_press_repo_guide";
    private static final String WRAP_CODE = "wrap_code";
    private static final String OTP_CODE = "otp_code";
    private static final String APP_LANGUAGE = "app_language";
    private static final String SENT_VIA = "sent_via";
    private static final String SENT_VIA_BOX = "sent_via_enabled";

    public static void setToken(@NonNull String token) {
        PrefHelper.set(TOKEN, token);
    }

    public static String getToken() {
        return PrefHelper.getString(TOKEN);
    }

    public static String getOtpCode() {
        return PrefHelper.getString(OTP_CODE);
    }

    public static void setOtpCode(@NonNull String otp) {
        PrefHelper.set(OTP_CODE, otp);
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted") public static boolean isUserIconGuideShowed() {
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

    public static boolean isHomeButoonHintShowed() {
        boolean isShowed = PrefHelper.getBoolean(HOME_BUTTON_GUIDE);
        PrefHelper.set(HOME_BUTTON_GUIDE, true);
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

    public static boolean isNavDrawerHintShowed() {
        boolean isShowed = PrefHelper.getBoolean(NAV_DRAWER_GUIDE);
        PrefHelper.set(NAV_DRAWER_GUIDE, true);
        return isShowed;
    }

    public static boolean isRepoFabHintShowed() {
        boolean isShowed = PrefHelper.getBoolean(FAB_LONG_PRESS_REPO_GUIDE);
        PrefHelper.set(FAB_LONG_PRESS_REPO_GUIDE, true);
        return isShowed;
    }

    public static boolean isRVAnimationEnabled() {
        return PrefHelper.getBoolean("recylerViewAnimation");
    }

    public static long getNotificationTaskDuration(@NonNull Context context) {
        String prefValue = PrefHelper.getString("notificationTime");
        if (prefValue != null) {
            return notificationDurationMillis(context, prefValue);
        }
        return -1;
    }

    public static long notificationDurationMillis(@NonNull Context context, @NonNull String prefValue) {
        if (!InputHelper.isEmpty(prefValue)) {
            if (prefValue.equalsIgnoreCase(context.getString(R.string.thirty_minutes))) {
                return TimeUnit.MINUTES.toMillis(30);
            } else if (prefValue.equalsIgnoreCase(context.getString(R.string.twenty_minutes))) {
                return TimeUnit.MINUTES.toMillis(20);
            } else if (prefValue.equalsIgnoreCase(context.getString(R.string.ten_minutes))) {
                return TimeUnit.MINUTES.toMillis(10);
            } else if (prefValue.equalsIgnoreCase(context.getString(R.string.five_minutes))) {
                return TimeUnit.MINUTES.toMillis(5);
            } else if (prefValue.equalsIgnoreCase(context.getString(R.string.one_minute))) {
                return TimeUnit.MINUTES.toMillis(1);
            } else if (prefValue.equalsIgnoreCase(context.getString(R.string.turn_off))) {
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted") public static boolean isMarkAsReadEnabled() {
        return PrefHelper.getBoolean("markNotificationAsRead");
    }

    public static boolean isWrapCode() {
        return PrefHelper.getBoolean(WRAP_CODE);
    }

    public static boolean isSentViaEnabled() {
        return PrefHelper.getBoolean(SENT_VIA);
    }

    public static boolean isSentViaBoxEnabled() {
        return PrefHelper.getBoolean(SENT_VIA_BOX);
    }

    @ThemeType public static int getThemeType(@NonNull Context context) {
        return getThemeType(context.getResources());
    }

    @ThemeColor public static int getThemeColor(@NonNull Context context) {
        return getThemeColor(context.getResources());
    }

    @ThemeType static int getThemeType(@NonNull Resources resources) {
        String appTheme = PrefHelper.getString("appTheme");
        if (!InputHelper.isEmpty(appTheme)) {
            if (appTheme.equalsIgnoreCase(resources.getString(R.string.dark_theme_mode))) {
                return DARK;
            } else if (appTheme.equalsIgnoreCase(resources.getString(R.string.light_theme_mode))) {
                return LIGHT;
            }
        }
        return LIGHT;
    }

    @ThemeColor private static int getThemeColor(@NonNull Resources resources) {
        String appColor = PrefHelper.getString("appColor");
        if (!InputHelper.isEmpty(appColor)) {
            if (appColor.equalsIgnoreCase(resources.getString(R.string.red_theme_mode)))
                return RED;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.pink_theme_mode)))
                return PINK;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.purple_theme_mode)))
                return PURPLE;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.deep_purple_theme_mode)))
                return DEEP_PURPLE;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.indigo_theme_mode)))
                return INDIGO;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.blue_theme_mode)))
                return BLUE;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.light_blue_theme_mode)))
                return LIGHT_BLUE;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.cyan_theme_mode)))
                return CYAN;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.teal_theme_mode)))
                return TEAL;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.green_theme_mode)))
                return GREEN;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.light_green_theme_mode)))
                return LIGHT_GREEN;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.lime_theme_mode)))
                return LIME;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.yellow_theme_mode)))
                return YELLOW;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.amber_theme_mode)))
                return AMBER;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.orange_theme_mode)))
                return ORANGE;
            if (appColor.equalsIgnoreCase(resources.getString(R.string.deep_orange_theme_mode)))
                return DEEP_ORANGE;
        }
        return BLUE;
    }

    @NonNull static String getAppLanguage() {
        String appLanguage = PrefHelper.getString(APP_LANGUAGE);
        return appLanguage == null ? "en" : appLanguage;
    }

    public static void setWhatsNewVersion() {
        PrefHelper.set(WHATS_NEW_VERSION, BuildConfig.VERSION_CODE);
    }

    public static boolean showWhatsNew() {
        return PrefHelper.getInt(WHATS_NEW_VERSION) != BuildConfig.VERSION_CODE;
    }

    public static boolean isNotificationSoundEnabled() {
        return PrefHelper.getBoolean("notificationSound");
    }
}
