package com.fastaccess.helper;

import android.support.annotation.NonNull;

/**
 * Created by Kosh on 10 Nov 2016, 3:43 PM
 */

public class PrefGetter {
    private static final String TOKEN = "token";

    public static void setToken(@NonNull String token) {
        PrefHelper.set(TOKEN, token);
    }

    public static String getToken() {
        return PrefHelper.getString(TOKEN);
    }
}
