package com.fastaccess.provider.tasks.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.annimon.stream.LongStream;
import com.fastaccess.R;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Kosh on 11 Mar 2017, 12:13 AM
 */

public class ReadNotificationService extends IntentService {

    public static final int READ_SINGLE = 1;
    public static final int READ_ALL = 2;
    public static final int OPEN_NOTIFICATION = 3;
    public static final int UN_SUBSCRIBE = 4;
    private NotificationCompat.Builder notification;
    private NotificationManager notificationManager;

    public static void start(@NonNull Context context, long id) {
        Intent intent = new Intent(context.getApplicationContext(), ReadNotificationService.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA_TYPE, READ_SINGLE)
                .put(BundleConstant.ID, id)
                .end());
        context.startService(intent);
    }

    public static Intent start(@NonNull Context context, long id, @NonNull String url) {
        return start(context, id, url, false);
    }

    public static Intent start(@NonNull Context context, long id, @NonNull String url, boolean onlyRead) {
        Intent intent = new Intent(context.getApplicationContext(), ReadNotificationService.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA_TYPE, OPEN_NOTIFICATION)
                .put(BundleConstant.EXTRA, url)
                .put(BundleConstant.ID, id)
                .put(BundleConstant.YES_NO_EXTRA, onlyRead)
                .end());
        return intent;
    }

    public static void unSubscribe(@NonNull Context context, long id) {
        Intent intent = new Intent(context.getApplicationContext(), ReadNotificationService.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA_TYPE, UN_SUBSCRIBE)
                .put(BundleConstant.ID, id)
                .end());
        context.startService(intent);
    }

    public static void start(@NonNull Context context, @NonNull long[] ids) {
        Intent intent = new Intent(context.getApplicationContext(), ReadNotificationService.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA_TYPE, READ_ALL)
                .put(BundleConstant.ID, ids)
                .end());
        context.startService(intent);
    }

    public ReadNotificationService() {
        super(ReadNotificationService.class.getSimpleName());
    }

    @Override public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            int type = bundle.getInt(BundleConstant.EXTRA_TYPE);
            if (type == READ_SINGLE) {
                markSingleAsRead(bundle.getLong(BundleConstant.ID));
            } else if (type == READ_ALL) {
                markMultiAsRead(bundle.getLongArray(BundleConstant.ID));
            } else if (type == OPEN_NOTIFICATION) {
                openNotification(bundle.getLong(BundleConstant.ID), bundle.getString(BundleConstant.EXTRA),
                        bundle.getBoolean(BundleConstant.YES_NO_EXTRA));
            } else if (type == UN_SUBSCRIBE) {
                unSubscribeFromThread(bundle.getLong(BundleConstant.ID));
            }
        }
    }

    private void unSubscribeFromThread(long id) {
        RestProvider.getNotificationService(PrefGetter.isEnterprise())
                .unSubscribe(id)
                .doOnSubscribe(disposable -> notify(id, getNotification().build()))
                .subscribeOn(Schedulers.io())
                .flatMap(notification1 -> Observable.create(subscriber -> markSingleAsRead(id)))
                .subscribe(booleanResponse -> cancel(id), throwable -> cancel(id));
    }

    private void openNotification(long id, @Nullable String url, boolean readOnly) {
        if (id > 0 && url != null) {
            AppHelper.cancelNotification(this, InputHelper.getSafeIntId(id));
            if (readOnly) {
                markSingleAsRead(id);
            } else if (!PrefGetter.isMarkAsReadEnabled()) {
                markSingleAsRead(id);
            }
            if (!readOnly) {
                SchemeParser.launchUri(getApplicationContext(), Uri.parse(url), true, true);
            }
        }
    }

    private void markMultiAsRead(@Nullable long[] ids) {
        if (ids != null && ids.length > 0) {
            LongStream.of(ids).forEach(this::markSingleAsRead);
        }
    }

    private void markSingleAsRead(long id) {
        com.fastaccess.data.dao.model.Notification.markAsRead(id);
        RestProvider.getNotificationService(PrefGetter.isEnterprise())
                .markAsRead(String.valueOf(id))
                .doOnSubscribe(disposable -> notify(id, getNotification().build()))
                .subscribeOn(Schedulers.io())
                .subscribe(booleanResponse -> cancel(id), throwable -> cancel(id));
    }

    private NotificationCompat.Builder getNotification() {
        if (notification == null) {
            notification = new NotificationCompat.Builder(this, "read-notification")
                    .setContentTitle(getString(R.string.marking_as_read))
                    .setSmallIcon(R.drawable.ic_sync)
                    .setProgress(0, 100, true);
        }
        return notification;
    }

    private void notify(long id, Notification notification) {
        notificationManager.notify(InputHelper.getSafeIntId(id), notification);
    }

    private void cancel(long id) {
        notificationManager.cancel(InputHelper.getSafeIntId(id));
    }
}
