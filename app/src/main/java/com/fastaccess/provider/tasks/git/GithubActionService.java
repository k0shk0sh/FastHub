package com.fastaccess.provider.tasks.git;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.fastaccess.R;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.RestProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.reactivex.schedulers.Schedulers;


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
    private NotificationCompat.Builder notification;
    private NotificationManager notificationManager;

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

    public static void startForRepo(@NonNull Context context, @NonNull String login, @NonNull String repo,
                                    @GitActionType int type, boolean isEnterprise) {
        Intent intent = new Intent(context.getApplicationContext(), GithubActionService.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ID, repo)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TYPE, type)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .end());
        context.startService(intent);
    }

    public static void startForGist(@NonNull Context context, @NonNull String id, @GitActionType int type, boolean isEnterprise) {
        Intent intent = new Intent(context.getApplicationContext(), GithubActionService.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.ID, id)
                .put(BundleConstant.EXTRA_TYPE, type)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
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
            boolean isEnterprise = bundle.getBoolean(BundleConstant.IS_ENTERPRISE);
            switch (type) {
                case FORK_GIST:
                    forkGist(id, isEnterprise);
                    break;
                case FORK_REPO:
                    forkRepo(id, login, isEnterprise);
                    break;
                case STAR_GIST:
                    starGist(id, isEnterprise);
                    break;
                case STAR_REPO:
                    starRepo(id, login, isEnterprise);
                    break;
                case UNSTAR_GIST:
                    unStarGist(id, isEnterprise);
                    break;
                case UNSTAR_REPO:
                    unStarRepo(id, login, isEnterprise);
                    break;
                case UNWATCH_REPO:
                    unWatchRepo(id, login, isEnterprise);
                    break;
                case WATCH_REPO:
                    watchRepo(id, login, isEnterprise);
                    break;
            }
        }
    }

    private void forkGist(@Nullable String id, boolean isEnterprise) {
        if (id != null) {
            String msg = getString(R.string.forking, getString(R.string.gist));
            RestProvider.getGistService(isEnterprise)
                    .forkGist(id)
                    .doOnSubscribe(disposable -> showNotification(msg))
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, throwable -> hideNotification(msg), () -> hideNotification(msg));
        }
    }

    private void forkRepo(@Nullable String id, @Nullable String login, boolean isEnterprise) {
        if (id != null && login != null) {
            String msg = getString(R.string.forking, id);
            RestProvider.getRepoService(isEnterprise)
                    .forkRepo(login, id)
                    .doOnSubscribe(disposable -> showNotification(msg))
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, throwable -> hideNotification(msg), () -> hideNotification(msg));
        }
    }

    private void starGist(@Nullable String id, boolean isEnterprise) {
        if (id != null) {
            String msg = getString(R.string.starring, getString(R.string.gist));
            RestProvider.getGistService(isEnterprise)
                    .starGist(id)
                    .doOnSubscribe(disposable -> showNotification(msg))
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, throwable -> hideNotification(msg), () -> hideNotification(msg));
        }
    }

    private void starRepo(@Nullable String id, @Nullable String login, boolean isEnterprise) {
        if (id != null && login != null) {
            String msg = getString(R.string.starring, id);
            RestProvider.getRepoService(isEnterprise)
                    .starRepo(login, id)
                    .doOnSubscribe(disposable -> showNotification(msg))
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, throwable -> hideNotification(msg), () -> hideNotification(msg));
        }
    }

    private void unStarGist(@Nullable String id, boolean isEnterprise) {
        if (id != null) {
            String msg = getString(R.string.un_starring, getString(R.string.gist));
            RestProvider.getGistService(isEnterprise)
                    .unStarGist(id)
                    .doOnSubscribe(disposable -> showNotification(msg))
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, throwable -> hideNotification(msg), () -> hideNotification(msg));
        }
    }

    private void unStarRepo(@Nullable String id, @Nullable String login, boolean isEnterprise) {
        if (id != null && login != null) {
            String msg = getString(R.string.un_starring, id);
            RestProvider.getRepoService(isEnterprise)
                    .unstarRepo(login, id)
                    .doOnSubscribe(disposable -> showNotification(msg))
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, throwable -> hideNotification(msg), () -> hideNotification(msg));
        }
    }

    private void unWatchRepo(@Nullable String id, @Nullable String login, boolean isEnterprise) {
        if (id != null && login != null) {
            String msg = getString(R.string.un_watching, id);
            RestProvider.getRepoService(isEnterprise)
                    .unwatchRepo(login, id)
                    .doOnSubscribe(disposable -> showNotification(msg))
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, throwable -> hideNotification(msg), () -> hideNotification(msg));
        }
    }

    private void watchRepo(@Nullable String id, @Nullable String login, boolean isEnterprise) {
        if (id != null && login != null) {
            String msg = getString(R.string.watching, id);
            RestProvider.getRepoService(isEnterprise)
                    .watchRepo(login, id)
                    .doOnSubscribe(disposable -> showNotification(msg))
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                    }, throwable -> hideNotification(msg), () -> hideNotification(msg));
        }
    }

    private NotificationCompat.Builder getNotification(@NonNull String title) {
        if (notification == null) {
            notification = new NotificationCompat.Builder(this, title)
                    .setSmallIcon(R.drawable.ic_sync)
                    .setProgress(0, 100, true);
        }
        notification.setContentTitle(title);
        return notification;
    }

    private NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    private void showNotification(@NonNull String msg) {
        getNotificationManager().notify(msg.hashCode(), getNotification(msg).build());
    }

    private void hideNotification(@NonNull String msg) {
        getNotificationManager().cancel(msg.hashCode());
    }
}
