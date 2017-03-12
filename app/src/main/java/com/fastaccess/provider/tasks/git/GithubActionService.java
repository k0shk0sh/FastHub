package com.fastaccess.provider.tasks.git;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.RestProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import rx.schedulers.Schedulers;

/**
 * Created by Kosh on 12 Mar 2017, 2:25 PM
 */

public class GithubActionService extends IntentService {

    public static final int STAR_REPO = 1;
    public static final int UNSTAR_REPO = 2;
    public static final int FORK_REPO = 3;
    public static final int WATCH_REPO = 4;
    public static final int UNWATCH_REPO = 5;
    public static final int STAR_GIST = 6;
    public static final int UNSTAR_GIST = 7;
    public static final int FORK_GIST = 8;

    @IntDef({
            STAR_REPO,
            UNSTAR_REPO,
            FORK_REPO,
            WATCH_REPO,
            UNWATCH_REPO,
            STAR_GIST,
            UNSTAR_GIST,
            FORK_GIST,
    })
    @Retention(RetentionPolicy.SOURCE) @interface GitActionType {}


    public static void startForRepo(@NonNull Context context, @NonNull String login, @NonNull String repo, @GitActionType int type) {
        Intent intent = new Intent(context.getApplicationContext(), GithubActionService.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ID, repo)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TYPE, type)
                .end());
        context.startService(intent);
    }

    public static void startForGist(@NonNull Context context, @NonNull String id, @GitActionType int type) {
        Intent intent = new Intent(context.getApplicationContext(), GithubActionService.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ID, id)
                .put(BundleConstant.EXTRA_TYPE, type)
                .end());
        context.startService(intent);
    }

    public GithubActionService() {
        super(GithubActionService.class.getName());
    }

    @Override protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            @GitActionType int type = bundle.getInt(BundleConstant.EXTRA_TYPE);
            String id = bundle.getString(BundleConstant.ID);
            String login = bundle.getString(BundleConstant.EXTRA);
            switch (type) {
                case FORK_GIST:
                    forkGist(id);
                    break;
                case FORK_REPO:
                    forkRepo(id, login);
                    break;
                case STAR_GIST:
                    starGist(id);
                    break;
                case STAR_REPO:
                    starRepo(id, login);
                    break;
                case UNSTAR_GIST:
                    unStarGist(id);
                    break;
                case UNSTAR_REPO:
                    unStarRepo(id, login);
                    break;
                case UNWATCH_REPO:
                    unWatchRepo(id, login);
                    break;
                case WATCH_REPO:
                    watchRepo(id, login);
                    break;
            }
        }
    }

    private void forkGist(@Nullable String id) {
        if (id != null) {
            RestProvider.getGistService()
                    .forkGist(id)
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, Throwable::printStackTrace);
        }
    }

    private void forkRepo(@Nullable String id, @Nullable String login) {
        if (id != null && login != null) {
            RestProvider.getRepoService()
                    .forkRepo(login, id)
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, Throwable::printStackTrace);
        }
    }

    private void starGist(@Nullable String id) {
        if (id != null) {
            RestProvider.getGistService()
                    .starGist(id)
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, Throwable::printStackTrace);
        }
    }

    private void starRepo(@Nullable String id, @Nullable String login) {
        if (id != null && login != null) {
            RestProvider.getRepoService()
                    .starRepo(login, id)
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, Throwable::printStackTrace);
        }
    }

    private void unStarGist(@Nullable String id) {
        if (id != null) {
            RestProvider.getGistService()
                    .unStarGist(id)
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, Throwable::printStackTrace);
        }
    }

    private void unStarRepo(@Nullable String id, @Nullable String login) {
        if (id != null && login != null) {
            RestProvider.getRepoService()
                    .unstarRepo(login, id)
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, Throwable::printStackTrace);
        }
    }

    private void unWatchRepo(@Nullable String id, @Nullable String login) {
        if (id != null && login != null) {
            RestProvider.getRepoService()
                    .unwatchRepo(login, id)
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, Throwable::printStackTrace);
        }
    }

    private void watchRepo(@Nullable String id, @Nullable String login) {
        if (id != null && login != null) {
            RestProvider.getRepoService()
                    .watchRepo(login, id)
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, Throwable::printStackTrace);
        }
    }
}
