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

    @IntDef({
            LIGHT,
            DARK,
    })
    @Retention(RetentionPolicy.SOURCE) @interface ThemeType {}

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
    private static final String POPUP_ANIMATION = "popupAnimation";
    private static final String WRAP_CODE = "wrap_code";
    private static final String OTP_CODE = "otp_code";

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
        String s = PrefHelper.getString("notificationTime");
        if (!InputHelper.isEmpty(s)) {
            if (s.equalsIgnoreCase(context.getString(R.string.thirty_minutes))) {
                return TimeUnit.MINUTES.toMillis(30);
            } else if (s.equalsIgnoreCase(context.getString(R.string.twenty_minutes))) {
                return TimeUnit.MINUTES.toMillis(20);
            } else if (s.equalsIgnoreCase(context.getString(R.string.ten_minutes))) {
                return TimeUnit.MINUTES.toMillis(10);
            } else if (s.equalsIgnoreCase(context.getString(R.string.five_minutes))) {
                return TimeUnit.MINUTES.toMillis(5);
            } else if (s.equalsIgnoreCase(context.getString(R.string.one_minute))) {
                return TimeUnit.MINUTES.toMillis(1);
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

    public static boolean isPopupAnimationEnabled() {
//        return PrefHelper.getBoolean(POPUP_ANIMATION);
        return true;
    }

    public static boolean isWrapCode() {
        return PrefHelper.getBoolean(WRAP_CODE);
    }

    @ThemeType public static int getThemeType(@NonNull Context context) {
        return getThemeType(context.getResources());
    }

    @ThemeType public static int getThemeType(@NonNull Resources resources) {
        String appTheme = PrefHelper.getString("appTheme");
        if (!InputHelper.isEmpty(appTheme)) {
            if (appTheme.equalsIgnoreCase(resources.getString(R.string.auto_theme_mode))) {
                return LIGHT;
            } else if (appTheme.equalsIgnoreCase(resources.getString(R.string.dark_theme_mode))) {
                return DARK;
            } else if (appTheme.equalsIgnoreCase(resources.getString(R.string.light_theme_mode))) {
                return LIGHT;
            } /* add future themes here */
        }
        return LIGHT;
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
