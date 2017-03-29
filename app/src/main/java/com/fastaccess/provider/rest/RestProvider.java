package com.fastaccess.provider.rest;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.App;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.GitHubErrorResponse;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.service.GistService;
import com.fastaccess.data.service.IssueService;
import com.fastaccess.data.service.NotificationService;
import com.fastaccess.data.service.PullRequestService;
import com.fastaccess.data.service.ReactionsService;
import com.fastaccess.data.service.RepoService;
import com.fastaccess.data.service.SearchService;
import com.fastaccess.data.service.UserRestService;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.rest.converters.GithubResponseConverter;
import com.fastaccess.provider.rest.interceptors.PaginationInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.lang.reflect.Modifier;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by Kosh on 08 Feb 2017, 8:37 PM
 */

public class RestProvider {

    private static Cache cache;
    public static final int PAGE_SIZE = 30;

    public final static Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setPrettyPrinting()
            .create();

    private static Cache provideCache() {
        if (cache == null) {
            int cacheSize = 20 * 1024 * 1024; //20MB
            cache = new Cache(App.getInstance().getCacheDir(), cacheSize);
        }
        return cache;
    }

    private static OkHttpClient provideOkHttpClient() {
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
                    requestBuilder.addHeader("Accept", "application/vnd.github.v3+json")
                            .addHeader("Content-type", "application/vnd.github.v3+json");
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
                .client(provideOkHttpClient())
                .addConverterFactory(new GithubResponseConverter(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public static void downloadFile(@NonNull Context context, @NonNull String url) {
        if (InputHelper.isEmpty(url)) return;
        Uri uri = Uri.parse(url);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        File direct = new File(Environment.getExternalStorageDirectory() + File.separator + context.getString(R.string.app_name));
        if (!direct.exists()) {
            direct.mkdirs();
        }
        String fileName = "";
        NameParser nameParser = new NameParser(url);
        if (nameParser.getUsername() != null) {
            fileName += nameParser.getUsername() + "_";
        }
        if (nameParser.getName() != null) {
            fileName += nameParser.getName() + "_";
        }
        fileName += new File(url).getName();
        request.setDestinationInExternalPublicDir(context.getString(R.string.app_name), fileName);
        request.setTitle(fileName);
        request.setDescription(context.getString(R.string.downloading_file));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
    }

    public static int getErrorCode(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return ((HttpException) throwable).code();

        }
        return -1;
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

    @NonNull public static ReactionsService getReactionsService() {
        return provideRetrofit().create(ReactionsService.class);
    }

    @Nullable public static GitHubErrorResponse getErrorResponse(@NonNull Throwable throwable) {
        ResponseBody body = null;
        if (throwable instanceof HttpException) {
            body = ((HttpException) throwable).response().errorBody();
        }
        if (body != null) {
            try {
                return new Gson().fromJson(body.toString(), GitHubErrorResponse.class);
            } catch (Exception ignored) {}
        }
        return null;
    }
}
