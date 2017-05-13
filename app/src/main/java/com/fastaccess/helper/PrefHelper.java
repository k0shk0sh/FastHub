package com.fastaccess.helper;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.App;

import java.util.Map;

/**
 * Created by kosh20111 on 19 Feb 2017, 2:01 AM
 */
public class PrefHelper {

    /**
     * @param key
     *         ( the Key to used to retrieve this data later  )
     * @param value
     *         ( any kind of primitive values  )
     *         <p/>
     *         non can be null!!!
     */
    @SuppressLint("ApplySharedPref") public static void set(@NonNull String key, @NonNull Object value) {
        if (InputHelper.isEmpty(key)) {
            throw new NullPointerException("Key must not be null! (key = " + key + "), (value = " + value + ")");
        }
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(App.getInstance()).edit();
        if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (int) value);
        } else if (value instanceof Long) {
            edit.putLong(key, (long) value);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (boolean) value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (float) value);
        }
        edit.commit();//apply on UI
    }

    @Nullable public static String getString(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getString(key, null);
    }

    public static boolean getBoolean(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getBoolean(key, false);
    }

    public static int getInt(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getInt(key, 0);
    }

    public static long getLong(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getLong(key, 0);
    }

    public static float getFloat(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getFloat(key, 0);
    }

    public static void clearKey(@NonNull String key) {
        PreferenceManager.getDefaultSharedPreferences(App.getInstance()).edit().remove(key).apply();
    }

    public static boolean isExist(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance()).contains(key);
    }

    public static void clearPrefs() {
        PreferenceManager.getDefaultSharedPreferences(App.getInstance()).edit().clear().apply();
    }

    public static Map<String, ?> getAll() {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getAll();
    }
}
