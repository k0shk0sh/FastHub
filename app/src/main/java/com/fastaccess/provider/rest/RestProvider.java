package com.fastaccess.provider.rest;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.GitHubErrorResponse;
import com.fastaccess.data.dao.GitHubStatusModel;
import com.fastaccess.data.service.ContentService;
import com.fastaccess.data.service.GistService;
import com.fastaccess.data.service.IssueService;
import com.fastaccess.data.service.NotificationService;
import com.fastaccess.data.service.OrganizationService;
import com.fastaccess.data.service.ProjectsService;
import com.fastaccess.data.service.PullRequestService;
import com.fastaccess.data.service.ReactionsService;
import com.fastaccess.data.service.RepoService;
import com.fastaccess.data.service.ReviewService;
import com.fastaccess.data.service.SearchService;
import com.fastaccess.data.service.UserRestService;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.rest.converters.GithubResponseConverter;
import com.fastaccess.provider.rest.interceptors.AuthenticationInterceptor;
import com.fastaccess.provider.rest.interceptors.ContentTypeInterceptor;
import com.fastaccess.provider.rest.interceptors.PaginationInterceptor;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.lang.reflect.Modifier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import tech.linjiang.pandora.Pandora;

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
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static OkHttpClient provideOkHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                client.addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY));
            }
            client.addInterceptor(new AuthenticationInterceptor());
            client.addInterceptor(new PaginationInterceptor());
            client.addInterceptor(new ContentTypeInterceptor());
            client.addInterceptor(Pandora.get().getInterceptor());
            okHttpClient = client.build();
        }
        return okHttpClient;
    }

    private static Retrofit provideRetrofit(boolean enterprise) {
        return new Retrofit.Builder()
                .baseUrl(enterprise && PrefGetter.isEnterprise() ? LinkParserHelper.getEndpoint(PrefGetter.getEnterpriseUrl()) : BuildConfig.REST_URL)
                .client(provideOkHttpClient())
                .addConverterFactory(new GithubResponseConverter(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static void downloadFile(@NonNull Context context, @NonNull String url) {
        downloadFile(context, url, null);
    }

    public static void downloadFile(@NonNull Context context, @NonNull String url, @Nullable String extension) {
        try {
            if (InputHelper.isEmpty(url)) return;
            boolean isEnterprise = LinkParserHelper.isEnterprise(url);
            if (url.endsWith(".apk")) {
                Activity activity = ActivityHelper.getActivity(context);
                if (activity != null) {
                    ActivityHelper.startCustomTab(activity, url);
                    return;
                }
            }
            Uri uri = Uri.parse(url);
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            String authToken = isEnterprise ? PrefGetter.getEnterpriseToken() : PrefGetter.getToken();
            if (!TextUtils.isEmpty(authToken)) {
                request.addRequestHeader("Authorization", authToken.startsWith("Basic") ? authToken : "token " + authToken);
            }
            String fileName = new File(url).getName();
            if (!InputHelper.isEmpty(extension)) {
                fileName += extension;
            }
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.setTitle(fileName);
            request.setDescription(context.getString(R.string.downloading_file));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            if (downloadManager != null) {
                downloadManager.enqueue(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public static int getErrorCode(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return ((HttpException) throwable).code();
        }
        return -1;
    }

    @NonNull public static UserRestService getUserService(boolean enterprise) {
        return provideRetrofit(enterprise).create(UserRestService.class);
    }

    @NonNull public static GistService getGistService(boolean enterprise) {
        return provideRetrofit(enterprise).create(GistService.class);
    }

    @NonNull public static RepoService getRepoService(boolean enterprise) {
        return provideRetrofit(enterprise).create(RepoService.class);
    }

    @NonNull public static IssueService getIssueService(boolean enterprise) {
        return provideRetrofit(enterprise).create(IssueService.class);
    }

    @NonNull public static PullRequestService getPullRequestService(boolean enterprise) {
        return provideRetrofit(enterprise).create(PullRequestService.class);
    }

    @NonNull public static NotificationService getNotificationService(boolean enterprise) {
        return provideRetrofit(enterprise).create(NotificationService.class);
    }

    @NonNull public static ReactionsService getReactionsService(boolean enterprise) {
        return provideRetrofit(enterprise).create(ReactionsService.class);
    }

    @NonNull public static OrganizationService getOrgService(boolean enterprise) {
        return provideRetrofit(enterprise).create(OrganizationService.class);
    }

    @NonNull public static ReviewService getReviewService(boolean enterprise) {
        return provideRetrofit(enterprise).create(ReviewService.class);
    }

    @NonNull public static UserRestService getContribution() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.REST_URL)
                .addConverterFactory(new GithubResponseConverter(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(UserRestService.class);
    }

    @NonNull public static SearchService getSearchService(boolean enterprise) {
        return provideRetrofit(enterprise).create(SearchService.class);
    }

    @NonNull public static ContentService getContentService(boolean enterprise) {
        return provideRetrofit(enterprise).create(ContentService.class);
    }

    @NonNull public static ProjectsService getProjectsService(boolean enterprise) {
        return provideRetrofit(enterprise).create(ProjectsService.class);
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

    @NonNull public static Observable<GitHubStatusModel> gitHubStatus() {
        return new Retrofit.Builder()
                .baseUrl("https://kctbh9vrtdwd.statuspage.io/")
                .client(provideOkHttpClient())
                .addConverterFactory(new GithubResponseConverter(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ContentService.class)
                .checkStatus();
    }

    public static void clearHttpClient() {
        okHttpClient = null;
    }

}
