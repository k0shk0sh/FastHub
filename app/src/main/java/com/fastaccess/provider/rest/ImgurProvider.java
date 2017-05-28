package com.fastaccess.provider.rest;

import android.support.annotation.NonNull;

import com.fastaccess.BuildConfig;
import com.fastaccess.data.service.ImgurService;
import com.fastaccess.provider.rest.converters.GithubResponseConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by Kosh on 15 Apr 2017, 7:59 PM
 */

public class ImgurProvider {

    public final static Gson gson = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            .setPrettyPrinting()
            .create();

    private ImgurProvider() {}

    private static OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            client.addInterceptor(new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        client.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder();
            requestBuilder.header("Authorization", "Client-ID " + BuildConfig.IMGUR_CLIENT_ID);
            requestBuilder.method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        return client.build();
    }

    private static Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.IMGUR_URL)
                .client(provideOkHttpClient())
                .addConverterFactory(new GithubResponseConverter(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @NonNull public static ImgurService getImgurService() {
        return provideRetrofit().create(ImgurService.class);
    }

}
