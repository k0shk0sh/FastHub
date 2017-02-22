package com.fastaccess.provider.rest;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.App;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.GitHubErrorResponse;
import com.fastaccess.data.service.GistService;
import com.fastaccess.data.service.IssueService;
import com.fastaccess.data.service.NotificationService;
import com.fastaccess.data.service.PullRequestService;
import com.fastaccess.data.service.RepoService;
import com.fastaccess.data.service.SearchService;
import com.fastaccess.data.service.UserRestService;
import com.fastaccess.helper.FileHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.rest.converters.GithubResponseConverter;
import com.fastaccess.provider.rest.interceptors.PaginationInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Kosh on 08 Feb 2017, 8:37 PM
 */

public class RestProvider {

    private static Cache cache;
    public static final int PAGE_SIZE = 30;

    private final static Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .setPrettyPrinting()
            .create();

    private static Cache provideCache() {
        if (cache == null) {
            int cacheSize = 20 * 1024 * 1024;
            cache = new Cache(App.getInstance().getCacheDir(), cacheSize);
        }
        return cache;
    }

    private static OkHttpClient provideOkHttpClient(boolean forLogin) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            client.addInterceptor(new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        client.addInterceptor(new PaginationInterceptor())
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder();
                    if (!InputHelper.isEmpty(PrefGetter.getToken())) {
                        requestBuilder.header("Authorization", "token " + PrefGetter.getToken());
                    }
                    if (!forLogin) {
                        requestBuilder.addHeader("Accept", "application/vnd.github.v3+json")
                                .addHeader("Content-type", "application/vnd.github.v3+json");
                    } else {
                        requestBuilder.addHeader("Accept", "application/json")
                                .addHeader("Content-type", "application/json");
                    }
                    requestBuilder.method(original.method(), original.body());
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                });
//        client.cache(provideCache());//disable cache, since we are going offline.
        return client.build();
    }

    private static Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.REST_URL)
                .client(provideOkHttpClient(false))
                .addConverterFactory(new GithubResponseConverter(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @SuppressWarnings("WeakerAccess") public static long downloadFile(@NonNull Context context, @NonNull String url, @Nullable String fileName) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        Logger.e(FileHelper.getDownloadDirectory(), fileName);
        if (!InputHelper.isEmpty(fileName)) {
            request.setDestinationInExternalPublicDir(FileHelper.getDownloadDirectory(), fileName);
            request.setDescription(String.format("%s %s", context.getString(R.string.downloading), fileName));
        } else {
            request.setDescription(String.format("%s %s", context.getString(R.string.downloading), url));
        }
        request.setTitle(context.getString(R.string.downloading_file));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        return downloadManager.enqueue(request);
    }

    public static long downloadFile(@NonNull Context context, @NonNull String url) {
        return downloadFile(context, url, null);
    }

    public static int getErrorCode(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return ((HttpException) throwable).code();

        }
        return -1;
    }

    @NonNull public static UserRestService getLoginRestService() {
        return new Retrofit.Builder()
                .client(provideOkHttpClient(true))
                .baseUrl("https://github.com/login/oauth/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(UserRestService.class);
    }

    @NonNull public static UserRestService getUserService() {
        return provideRetrofit().create(UserRestService.class);
    }

    @NonNull public static GistService getGistService() {
        return provideRetrofit().create(GistService.class);
    }

    @NonNull public static RepoService getRepoService() {
        return provideRetrofit().create(RepoService.class);
    }

    @NonNull public static IssueService getIssueService() {
        return provideRetrofit().create(IssueService.class);
    }

    @NonNull public static PullRequestService getPullRequestSerice() {
        return provideRetrofit().create(PullRequestService.class);
    }

    @NonNull public static SearchService getSearchService() {
        return provideRetrofit().create(SearchService.class);
    }

    @NonNull public static NotificationService getNotificationService() {
        return provideRetrofit().create(NotificationService.class);
    }

    @Nullable public static GitHubErrorResponse getErrorResponse(@NonNull Throwable throwable) {
        if (throwable instanceof HttpException) {
            ResponseBody body = ((HttpException) throwable).response().errorBody();
            if (body != null) {
                try {
                    Logger.e(body.string());
                    return new Gson().fromJson(body.toString(), GitHubErrorResponse.class);
                } catch (Exception ignored) {}
            }
        }
        return null;
    }
}
