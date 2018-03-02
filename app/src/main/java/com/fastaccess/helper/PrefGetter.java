package com.fastaccess.helper;

import android.content.Context;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.App;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Kosh on 10 Nov 2016, 3:43 PM
 */

public class PrefGetter {

    public static final int LIGHT = 1;
    public static final int DARK = 2;
    public static final int AMLOD = 3;
    public static final int BLUISH = 4;
    public static final int MID_NIGHT_BLUE = 5;

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
            AMLOD,
            MID_NIGHT_BLUE,
            BLUISH
    })
    @Retention(RetentionPolicy.SOURCE) public @interface ThemeType {}

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
    private static final String ENTERPRISE_TOKEN = "enterprise_token";
    private static final String USER_ICON_GUIDE = "user_icon_guide";
    private static final String RELEASE_GUIDE = "release_guide";
    private static final String FILE_OPTION_GUIDE = "file_option_guide";
    private static final String COMMENTS_GUIDE = "comments_guide";
    private static final String REPO_GUIDE = "repo_guide";
    private static final String MARKDOWNDOWN_GUIDE = "markdowndown_guide";
    private static final String HOME_BUTTON_GUIDE = "home_button_guide";
    private static final String NAV_DRAWER_GUIDE = "nav_drawer_guide";
    private static final String ACC_NAV_DRAWER_GUIDE = "acc_nav_drawer_guide";
    private static final String FAB_LONG_PRESS_REPO_GUIDE = "fab_long_press_repo_guide";
    private static final String WRAP_CODE = "wrap_code";
    private static final String OTP_CODE = "otp_code";
    private static final String ENTERPRISE_OTP_CODE = "enterprise_otp_code";
    private static final String APP_LANGUAGE = "app_language";
    private static final String SENT_VIA = "fasthub_signature";
    private static final String SENT_VIA_BOX = "sent_via_enabled";
    private static final String PROFILE_BACKGROUND_URL = "profile_background_url";
    private static final String AMLOD_THEME_ENABLED = "amlod_theme_enabled";
    private static final String MIDNIGHTBLUE_THEME_ENABLED = "midnightblue_theme_enabled";
    private static final String BLUISH_THEME_ENABLED = "bluish_theme_enabled";
    private static final String PRO_ITEMS = "fasth_pro_items";
    private static final String ENTERPRISE_ITEM = "enterprise_item";
    private static final String CODE_THEME = "code_theme";
    private static final String ENTERPRISE_URL = "enterprise_url";
    private static final String NOTIFICATION_SOUND_PATH = "notification_sound_path";
    private static final String DISABLE_AUTO_LOAD_IMAGE = "disable_auto_loading_image";
    private static final String PLAY_STORE_REVIEW_ACTIVITY = "play_store_review_activity";

    public static void setToken(@Nullable String token) {
        PrefHelper.set(TOKEN, token);
    }

    public static void setTokenEnterprise(@Nullable String token) {
        PrefHelper.set(ENTERPRISE_TOKEN, token);
    }

    public static String getToken() {
        return PrefHelper.getString(TOKEN);
    }

    public static String getEnterpriseToken() {
        return PrefHelper.getString(ENTERPRISE_TOKEN);
    }

    public static String getEnterpriseOtpCode() {
        return PrefHelper.getString(ENTERPRISE_OTP_CODE);
    }

    public static void setEnterpriseOtpCode(@Nullable String otp) {
        PrefHelper.set(ENTERPRISE_OTP_CODE, otp);
    }

    public static String getOtpCode() {
        return PrefHelper.getString(OTP_CODE);
    }

    public static void setOtpCode(@Nullable String otp) {
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

    public static boolean isAccountNavDrawerHintShowed() {
        boolean isShowed = PrefHelper.getBoolean(ACC_NAV_DRAWER_GUIDE);
        PrefHelper.set(ACC_NAV_DRAWER_GUIDE, true);
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

    public static int getNotificationTaskDuration() {
        if (PrefHelper.isExist("notificationEnabled") && PrefHelper.getBoolean("notificationEnabled")) {
            String prefValue = PrefHelper.getString("notificationTime");
            if (prefValue != null) {
                return notificationDurationMillis(prefValue);
            }
        }
        return -1;
    }

    public static int notificationDurationMillis(@NonNull String prefValue) {
        if (!InputHelper.isEmpty(prefValue)) {
            switch (prefValue) {
                case "1":
                    return 60;
                case "5":
                    return 5 * 60;
                case "10":
                    return 10 * 60;
                case "20":
                    return 20 * 60;
                case "30":
                    return 30 * 60;
                case "60":
                    return 60 * 60; // 1 hour
                case "120":
                    return (60 * 2) * 60; // 2 hours
                case "180":
                    return (60 * 3) * 60; // 3 hours
            }
        }
        return -1;
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

    @ThemeType public static int getThemeType() {
        return getThemeType(App.getInstance().getResources());
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
            } else if (appTheme.equalsIgnoreCase(resources.getString(R.string.amlod_theme_mode))) {
                return AMLOD;
            } else if (appTheme.equalsIgnoreCase(resources.getString(R.string.mid_night_blue_theme_mode))) {
                return MID_NIGHT_BLUE;
            } else if (appTheme.equalsIgnoreCase(resources.getString(R.string.bluish_theme))) {
                return BLUISH;
            }
        }
        return LIGHT;
    }

    @ThemeColor private static int getThemeColor(@NonNull Resources resources) {
        String appColor = PrefHelper.getString("appColor");
        return getThemeColor(resources, appColor);
    }

    // used for color picker to get the index of the color (enum) from the name of the color
    public static int getThemeColor(@NonNull Resources resources, String appColor) {
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

    @NonNull public static String getAppLanguage() {
        String appLanguage = PrefHelper.getString(APP_LANGUAGE);
        return appLanguage == null ? "en" : appLanguage;
    }

    public static void setAppLangauge(@Nullable String language) {
        PrefHelper.set(APP_LANGUAGE, language == null ? "en" : language);
    }

    public static void setProfileBackgroundUrl(@Nullable String url) {
        if (url == null) {
            PrefHelper.clearKey(PROFILE_BACKGROUND_URL);
        } else {
            PrefHelper.set(PROFILE_BACKGROUND_URL, url);
        }
    }

    @Nullable public static String getProfileBackgroundUrl() {
        return PrefHelper.getString(PROFILE_BACKGROUND_URL);
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

    public static void enableAmlodTheme() {
        PrefHelper.set(AMLOD_THEME_ENABLED, true);
    }

    public static boolean isAmlodEnabled() {
        return PrefHelper.getBoolean(AMLOD_THEME_ENABLED);
    }

    public static void enableMidNightBlueTheme() {
        PrefHelper.set(MIDNIGHTBLUE_THEME_ENABLED, true);
    }

    public static boolean isMidNightBlueThemeEnabled() {
        return PrefHelper.getBoolean(MIDNIGHTBLUE_THEME_ENABLED);
    }

    public static boolean isBluishEnabled() {
        return PrefHelper.getBoolean(BLUISH_THEME_ENABLED);
    }

    public static void enableBluishTheme() {
        PrefHelper.set(BLUISH_THEME_ENABLED, true);
    }

    public static void setProItems() {
        PrefHelper.set(PRO_ITEMS, true);
        enableAmlodTheme();
        enableBluishTheme();
        enableMidNightBlueTheme();
    }

    public static void setEnterpriseItem() {
        PrefHelper.set(ENTERPRISE_ITEM, true);
    }

    public static boolean isEnterpriseEnabled() {
        return PrefHelper.getBoolean(ENTERPRISE_ITEM);
    }

    public static boolean isAllFeaturesUnlocked() {
        return isProEnabled() && isEnterprise();
    }

    public static boolean isProEnabled() {
        return PrefHelper.getBoolean(PRO_ITEMS);
    }

    public static boolean hasSupported() {
        return isProEnabled() || isAmlodEnabled() || isBluishEnabled();
    }

    public static String getCodeTheme() {
        return PrefHelper.getString(CODE_THEME);
    }

    public static void setCodeTheme(@NonNull String theme) {
        PrefHelper.set(CODE_THEME, theme);
    }

    public static String getEnterpriseUrl() {
        return PrefHelper.getString(ENTERPRISE_URL);
    }

    public static void setEnterpriseUrl(@Nullable String value) {
        PrefHelper.set(ENTERPRISE_URL, value);
    }

    public static boolean isEnterprise() {
        return !InputHelper.isEmpty(getEnterpriseUrl());
    }

    public static boolean isNavBarTintingDisabled() {
        return PrefHelper.getBoolean("navigation_color");
    }

    public static void resetEnterprise() {
        PrefGetter.setTokenEnterprise(null);
        PrefGetter.setEnterpriseOtpCode(null);
        PrefGetter.setEnterpriseUrl(null);
    }

    @Nullable public static Uri getNotificationSound() {
        String nsp = PrefHelper.getString(NOTIFICATION_SOUND_PATH);
        return !InputHelper.isEmpty(nsp) ? Uri.parse(nsp) : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    public static void setNotificationSound(@NonNull Uri uri) {
        PrefHelper.set(NOTIFICATION_SOUND_PATH, uri.toString());
    }

    public static boolean isAutoImageDisabled() {
        return PrefHelper.getBoolean(DISABLE_AUTO_LOAD_IMAGE) && AppHelper.isDataPlan();
    }

    public static boolean isAppAnimationDisabled() {
        return PrefHelper.getBoolean("app_animation");
    }

    public static boolean isPlayStoreWarningShowed() {
        return PrefHelper.getBoolean(PLAY_STORE_REVIEW_ACTIVITY);
    }

    public static void setPlayStoreWarningShowed() {
        PrefHelper.set(PLAY_STORE_REVIEW_ACTIVITY, true);
    }

    public static void clearPurchases() {
        PrefHelper.set(PRO_ITEMS, false);
        PrefHelper.set(BLUISH_THEME_ENABLED, false);
        PrefHelper.set(AMLOD_THEME_ENABLED, false);
        setEnterpriseUrl(null);
    }

    public static boolean isFeedsHintShowed() {
        boolean isFeedsHitShowed = PrefHelper.getBoolean("feeds_hint");
        if (!isFeedsHitShowed) {
            PrefHelper.set("feeds_hint", true);
        }
        return isFeedsHitShowed;
    }

    public static boolean isIssuesLongPressHintShowed() {
        boolean isIssuesLongPressHintShowed = PrefHelper.getBoolean("issues_long_press_hint");
        if (!isIssuesLongPressHintShowed) {
            PrefHelper.set("issues_long_press_hint", true);
        }
        return isIssuesLongPressHintShowed;
    }

    public static boolean isPRLongPressHintShowed() {
        boolean isPRLongPressHintShowed = PrefHelper.getBoolean("pr_long_press_hint");
        if (!isPRLongPressHintShowed) {
            PrefHelper.set("pr_long_press_hint", true);
        }
        return isPRLongPressHintShowed;
    }

}
