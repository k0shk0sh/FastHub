package com.fastaccess.provider.rest.jsoup;

import com.fastaccess.BuildConfig;
import com.fastaccess.data.service.TrendingService;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.rest.converters.GithubResponseConverter;
import com.fastaccess.provider.rest.interceptors.AuthenticationInterceptor;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by Kosh on 02 Jun 2017, 12:47 PM
 */

public class JsoupProvider {

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

    public static TrendingService getTrendingService() {
        return new Retrofit.Builder()
                .baseUrl("https://github.com/trending/")
                .client(provideOkHttpClient())
                .addConverterFactory(new GithubResponseConverter(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(TrendingService.class);
    }
}
