package com.fastaccess.provider.rest;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.GitHubErrorResponse;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.service.GistService;
import com.fastaccess.data.service.IssueService;
import com.fastaccess.data.service.NotificationService;
import com.fastaccess.data.service.OrganizationService;
import com.fastaccess.data.service.PullRequestService;
import com.fastaccess.data.service.ReactionsService;
import com.fastaccess.data.service.RepoService;
import com.fastaccess.data.service.ReviewService;
import com.fastaccess.data.service.SearchService;
import com.fastaccess.data.service.SlackService;
import com.fastaccess.data.service.UserRestService;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.rest.converters.GithubResponseConverter;
import com.fastaccess.provider.rest.interceptors.AuthenticationInterceptor;
import com.fastaccess.provider.rest.interceptors.PaginationInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URI;

import okhttp3.HttpUrl;
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

    public static final int PAGE_SIZE = 30;

    private static OkHttpClient okHttpClient;
    public final static Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setPrettyPrinting()
            .create();

    private static OkHttpClient provideOkHttpClient(boolean isRawString) {
        if (okHttpClient == null) {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                client.addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY));
            }
            client.addInterceptor(new AuthenticationInterceptor(PrefGetter.getToken(), PrefGetter.getOtpCode()));
            if (!isRawString) client.addInterceptor(new PaginationInterceptor());
            client.addInterceptor(chain -> {
                Request original = chain.request();
                if (original.url() != HttpUrl.get(URI.create(NotificationService.SUBSCRIPTION_URL))) {
                    Request.Builder requestBuilder = original.newBuilder();
                    requestBuilder.addHeader("Accept", "application/vnd.github.v3+json")
                            .addHeader("Content-type", "application/vnd.github.v3+json");
                    requestBuilder.method(original.method(), original.body());
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
                return chain.proceed(original);
            });
            okHttpClient = client.build();
        }
        return okHttpClient;
    }

    private static Retrofit provideRetrofit(boolean isRawString) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.REST_URL)
                .client(provideOkHttpClient(isRawString))
                .addConverterFactory(new GithubResponseConverter(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    private static Retrofit provideRetrofit() {
        return provideRetrofit(false);
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
        return getRepoService(false);
    }

    @NonNull public static RepoService getRepoService(boolean isRawString) {
        return provideRetrofit(isRawString).create(RepoService.class);
    }

    @NonNull public static IssueService getIssueService() {
        return provideRetrofit().create(IssueService.class);
    }

    @NonNull public static PullRequestService getPullRequestService() {
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

    @NonNull public static OrganizationService getOrgService() {
        return provideRetrofit().create(OrganizationService.class);
    }

    @NonNull public static ReviewService getReviewService() {
        return provideRetrofit().create(ReviewService.class);
    }

    @Nullable public static GitHubErrorResponse getErrorResponse(@NonNull Throwable throwable) {
        ResponseBody body = null;
        if (throwable instanceof HttpException) {
            body = ((HttpException) throwable).response().errorBody();
        }
        if (body != null) {
            try {
                return gson.fromJson(body.string(), GitHubErrorResponse.class);
            } catch (Exception ignored) {}
        }
        return null;
    }

    @NonNull public static SlackService getSlackService() {
        return new Retrofit.Builder()
                .baseUrl("https://ok13pknpj4.execute-api.eu-central-1.amazonaws.com/prod/")
                .addConverterFactory(new GithubResponseConverter(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(SlackService.class);
    }

    public static void clearHttpClient() {
        okHttpClient = null;
    }

}
