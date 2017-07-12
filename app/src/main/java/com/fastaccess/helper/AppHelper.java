package com.fastaccess.helper;

import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.fastaccess.App;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;

import java.util.Locale;

import es.dmoral.toasty.Toasty;

/**
 * Created by kosh20111 on 18 Oct 2016, 9:29 PM
 */

public class AppHelper {

    public static void hideKeyboard(@NonNull View view) {
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Nullable public static Fragment getFragmentByTag(@NonNull FragmentManager fragmentManager, @NonNull String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }

    public static void cancelNotification(@NonNull Context context) {
        cancelNotification(context, BundleConstant.REQUEST_CODE);
    }

    public static void cancelNotification(@NonNull Context context, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    public static void cancelAllNotifications(@NonNull Context context) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }

    public static void copyToClipboard(@NonNull Context context, @NonNull String uri) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getString(R.string.app_name), uri);
        clipboard.setPrimaryClip(clip);
        Toasty.success(App.getInstance(), context.getString(R.string.success_copied)).show();
    }

    public static boolean isNightMode(@NonNull Resources resources) {
        @PrefGetter.ThemeType int themeType = PrefGetter.getThemeType(resources);
        return themeType == PrefGetter.DARK || themeType == PrefGetter.AMLOD || themeType == PrefGetter.BLUISH;
    }

    @SuppressWarnings("StringBufferReplaceableByString") public static String getFastHubIssueTemplate() {
        String brand = (!isEmulator()) ? Build.BRAND : "Android Emulator";
        String model = (!isEmulator()) ? Build.MODEL : "Android Emulator";
        return new StringBuilder()
                .append("**FastHub Version: ")
                .append(BuildConfig.VERSION_NAME)
                .append("**")
                .append("  \n")
                .append("**Android Version: ")
                .append(String.valueOf(Build.VERSION.RELEASE))
                .append(" (SDK: ")
                .append(String.valueOf(Build.VERSION.SDK_INT))
                .append(")**")
                .append("  \n")
                .append("**Device Information:**")
                .append("  \n")
                .append("- ")
                .append(Build.MANUFACTURER)
                .append("  \n")
                .append("- ")
                .append(brand)
                .append("  \n")
                .append("- ")
                .append(model)
                .append("\n\n")
                .append("---")
                .append("\n\n")
                .toString();
    }

    public static void updateAppLanguage(@NonNull Context context) {
        String lang = PrefGetter.getAppLanguage();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, lang);
        }
        updateResourcesLegacy(context, lang);
    }

    private static void updateResources(Context context, String language) {
        Locale locale;
        String[] split = language.split("-");
        if (split.length > 1) {
            locale = new Locale(split[0], split[1]);
        } else {
            locale = new Locale(language);
        }
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static void updateResourcesLegacy(Context context, String language) {
        Locale locale;
        String[] split = language.split("-");
        if (split.length > 1) {
            locale = new Locale(split[0], split[1]);
        } else {
            locale = new Locale(language);
        }
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public static String getDeviceName() {
        String brand = Build.BRAND;
        String model = Build.MODEL;
        if (model.startsWith(brand)) {
            return InputHelper.capitalizeFirstLetter(model);
        } else if (isEmulator()){
            return "Android Emulator";
        }
        return InputHelper.capitalizeFirstLetter(brand) + " " + model;
    }

    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
}