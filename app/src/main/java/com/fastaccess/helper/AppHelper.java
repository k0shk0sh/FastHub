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
        Toasty.success(context, context.getString(R.string.success_copied)).show();
    }

    public static boolean isNightMode(@NonNull Resources resources) {
        return PrefGetter.getThemeType(resources) == PrefGetter.DARK;
    }

    @SuppressWarnings("StringBufferReplaceableByString") public static String getFastHubIssueTemplate() {
        return new StringBuilder()
                .append("**App Version: ")
                .append(BuildConfig.VERSION_NAME)
                .append("**")
                .append("\n\n")
                .append("**OS Version: ")
                .append(String.valueOf(Build.VERSION.SDK_INT))
                .append("**")
                .append("\n\n")
                .append("**Model: ")
                .append(Build.MANUFACTURER)
                .append("-")
                .append(Build.MODEL)
                .append("**")
                .append("\n\n")
                .toString();
    }

    public static void updateAppLanguage(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, PrefGetter.getAppLanguage());
        }
        updateResourcesLegacy(context, PrefGetter.getAppLanguage());
    }

    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static void updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return InputHelper.capitalizeFirstLetter(model);
        } else {
            return InputHelper.capitalizeFirstLetter(manufacturer) + " " + model;
        }
    }

}
