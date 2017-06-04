package com.fastaccess.provider.rest.jsoup;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.BuildConfig;
import com.fastaccess.data.service.TrendingService;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.rest.interceptors.AuthenticationInterceptor;
import com.github.florent37.retrojsoup.RetroJsoup;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Kosh on 02 Jun 2017, 12:47 PM
 */

public class RetroJsoupProvider {

    private static OkHttpClient okHttpClient;

    private static OkHttpClient provideOkHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                client.addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY));
            }
            client.addInterceptor(new AuthenticationInterceptor(PrefGetter.getToken(), PrefGetter.getOtpCode(), true));
            okHttpClient = client.build();
        }
        return okHttpClient;
    }

    public static TrendingService getTrendingService(@NonNull String since, @Nullable String lang) {
        return new RetroJsoup.Builder()
                .url("https://github.com/trending/" + (!InputHelper.isEmpty(lang) ? lang.replaceAll(" ", "-") : "").toLowerCase()
                        + "?since=" + since)
                .client(provideOkHttpClient())
                .build()
                .create(TrendingService.class);
    }
}
