package com.fastaccess.provider.tasks.notification;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.annimon.stream.LongStream;
import com.fastaccess.R;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.RestProvider;

import rx.schedulers.Schedulers;

/**
 * Created by Kosh on 11 Mar 2017, 12:13 AM
 */

public class ReadNotificationService extends IntentService {

    public static final int READ_SINGLE = 1;
    public static final int READ_ALL = 2;
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

    @Override protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            int type = bundle.getInt(BundleConstant.EXTRA_TYPE);
            if (type == READ_SINGLE) {
                markSingleAsRead(bundle.getLong(BundleConstant.ID));
            } else if (type == READ_ALL) {
                markMultiAsRead(bundle.getLongArray(BundleConstant.ID));
            }
        }
    }

    private void markMultiAsRead(@Nullable long[] ids) {
        if (ids != null && ids.length > 0) {
            LongStream.of(ids).forEach(this::markSingleAsRead);
        }
    }

    private void markSingleAsRead(long id) {
        RestProvider.getNotificationService()
                .markAsRead(String.valueOf(id))
                .doOnSubscribe(() -> getNotificationManager().notify((int) id, getNotification().build()))
                .subscribeOn(Schedulers.io())
                .subscribe(booleanResponse -> {
                }, Throwable::printStackTrace, () -> getNotificationManager().cancel((int) id));
    }

    public NotificationCompat.Builder getNotification() {
        if (notification == null) {
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.marking_as_read))
                    .setSmallIcon(R.drawable.ic_sync)
                    .setProgress(0, 100, true);
        }
        return notification;
    }

    public NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
}
