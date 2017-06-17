package com.fastaccess.provider.tasks.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.modules.notification.NotificationActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.TaskParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.google.android.gms.gcm.GcmNetworkManager.RESULT_SUCCESS;

/**
 * Created by Kosh on 19 Feb 2017, 6:32 PM
 */

public class NotificationJobService extends GcmTaskService {
    private final static String JOB_ID = "fasthub_notification";

    private final static int THIRTY_MINUTES = 30 * 60;
    private static final String NOTIFICATION_GROUP_ID = "FastHub";

    public static void scheduleJob(@NonNull Context context) {
        int duration = PrefGetter.getNotificationTaskDuration();
        scheduleJob(context, duration, false);
    }

    public static void scheduleJob(@NonNull Context context, int duration, boolean cancel) {
        Single.<Boolean>create(singleEmitter -> {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
                GcmNetworkManager gcmNetworkManager = GcmNetworkManager.getInstance(context);
                if (cancel) gcmNetworkManager.cancelAllTasks(NotificationJobService.class);
                if (duration == -1) {
                    gcmNetworkManager.cancelAllTasks(NotificationJobService.class);
                    return;
                }
                final long finalDuration = duration <= 0 ? THIRTY_MINUTES : duration;
                PeriodicTask task = new PeriodicTask.Builder()
                        .setTag(JOB_ID)
                        .setUpdateCurrent(true)
                        .setPersisted(true)
                        .setRequiresCharging(false)
                        .setRequiredNetwork(PeriodicTask.NETWORK_STATE_ANY)
                        .setPeriod(finalDuration)
                        .setFlex(finalDuration)
                        .setService(NotificationJobService.class)
                        .build();
                gcmNetworkManager.schedule(task);
                singleEmitter.onSuccess(true);
            }
            singleEmitter.onSuccess(false);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (!o) {
                        Toast.makeText(context, "No Google API Service", Toast.LENGTH_SHORT).show();
                    }
                }, Throwable::printStackTrace);
    }

    private void onSave(@Nullable List<Notification> notificationThreadModels, TaskParams job) {
        if (notificationThreadModels != null) {
            RxHelper.safeObservable(Notification.save(notificationThreadModels)).subscribe(notification -> {/**/}, Throwable::printStackTrace);
            onNotifyUser(notificationThreadModels, job);
        }
    }

    private void onNotifyUser(@NonNull List<Notification> notificationThreadModels, TaskParams job) {
        long count = Stream.of(notificationThreadModels)
                .filter(Notification::isUnread)
                .count();
        if (count == 0) {
            AppHelper.cancelAllNotifications(getApplicationContext());
            finishJob(job);
            return;
        }
        Context context = getApplicationContext();
        int accentColor = ContextCompat.getColor(this, R.color.material_blue_700);
        Notification first = notificationThreadModels.get(0);
        Observable.fromIterable(notificationThreadModels)
                .subscribeOn(Schedulers.io())
                .filter(notification -> notification.isUnread() && first.getId() != notification.getId())
                .take(10)
                .flatMap(notification -> {
                    Logger.e(notification.getSubject().getTitle());
                    if (notification.getSubject() != null && notification.getSubject().getLatestCommentUrl() != null) {
                        return RestProvider.getNotificationService()
                                .getComment(notification.getSubject().getLatestCommentUrl())
                                .subscribeOn(Schedulers.io());
                    } else {
                        return Observable.empty();
                    }
                }, (thread, comment) -> {
                    CustomNotificationModel customNotificationModel = new CustomNotificationModel();
                    String url;
                    if (comment != null && comment.getUser() != null) {
                        url = comment.getUser().getAvatarUrl();
                        if (!InputHelper.isEmpty(thread.getSubject().getLatestCommentUrl())) {
                            customNotificationModel.comment = comment;
                            customNotificationModel.url = url;
                        }
                    }
                    customNotificationModel.notification = thread;
                    return customNotificationModel;
                })
                .subscribeOn(Schedulers.io())
                .subscribe(custom -> {
                    if (custom.comment != null) {
                        getNotificationWithComment(context, accentColor, custom.notification, custom.comment, custom.url);
                    } else {
                        showNotificationWithoutComment(context, accentColor, custom.notification, custom.url);
                    }

                }, throwable -> finishJob(job), () -> {
                    android.app.Notification grouped = getSummaryGroupNotification(first, accentColor, notificationThreadModels.size() > 1);
                    showNotification(first.getId(), grouped);
                    finishJob(job);
                });
    }

    private void finishJob(TaskParams job) {}

    private void showNotificationWithoutComment(Context context, int accentColor, Notification thread, String iconUrl) {
        if (!InputHelper.isEmpty(iconUrl)) {
            withoutComments(null, thread, context, accentColor);
        } else {
            ImageLoader.getInstance().loadImage(iconUrl, new ImageSize(50, 50), new ImageLoadingListener() {
                @Override public void onLoadingStarted(String s, View view) {}

                @Override public void onLoadingFailed(String s, View view, FailReason failReason) {
                    withoutComments(null, thread, context, accentColor);
                }

                @Override public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    withoutComments(bitmap, thread, context, accentColor);
                }

                @Override public void onLoadingCancelled(String s, View view) {
                    withoutComments(null, thread, context, accentColor);

                }
            });
        }
    }

    private void withoutComments(Bitmap bitmap, Notification thread, Context context, int accentColor) {
        android.app.Notification toAdd = getNotification(thread.getSubject().getTitle(), thread.getRepository().getFullName())
                .setLargeIcon(bitmap == null ? BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher) : bitmap)
                .setContentIntent(getPendingIntent(thread.getId(), thread.getSubject().getUrl()))
                .addAction(R.drawable.ic_github, context.getString(R.string.open), getPendingIntent(thread.getId(), thread
                        .getSubject().getUrl()))
                .addAction(R.drawable.ic_eye_off, context.getString(R.string.mark_as_read), getReadOnlyPendingIntent(thread.getId(), thread
                        .getSubject().getUrl()))
                .setWhen(thread.getUpdatedAt() != null ? thread.getUpdatedAt().getTime() : System.currentTimeMillis())
                .setShowWhen(true)
                .setColor(accentColor)
                .setGroup(NOTIFICATION_GROUP_ID)
                .build();
        showNotification(thread.getId(), toAdd);
    }

    private void getNotificationWithComment(Context context, int accentColor, Notification thread, Comment comment, String url) {
        if (!InputHelper.isEmpty(url)) {
            ImageLoader.getInstance().loadImage(url, new ImageSize(50, 50), new ImageLoadingListener() {
                @Override public void onLoadingStarted(String s, View view) {}

                @Override public void onLoadingFailed(String s, View view, FailReason failReason) {
                    withComments(null, comment, context, thread, accentColor);
                }

                @Override public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    withComments(bitmap, comment, context, thread, accentColor);
                }

                @Override public void onLoadingCancelled(String s, View view) {
                    withComments(null, comment, context, thread, accentColor);
                }
            });
        } else {
            withComments(null, comment, context, thread, accentColor);
        }
    }

    private void withComments(Bitmap bitmap, Comment comment, Context context, Notification thread, int accentColor) {
        android.app.Notification toAdd = getNotification(comment.getUser() != null ? comment.getUser().getLogin() : "", comment.getBody())
                .setLargeIcon(bitmap == null ? BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher) : bitmap)
                .setSmallIcon(R.drawable.ic_notification)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(comment.getUser() != null ? comment.getUser().getLogin() : "")
                        .bigText(comment.getBody()))
                .setWhen(comment.getCreatedAt().getTime())
                .setShowWhen(true)
                .addAction(R.drawable.ic_github, context.getString(R.string.open), getPendingIntent(thread.getId(),
                        thread.getSubject().getUrl()))
                .addAction(R.drawable.ic_eye_off, context.getString(R.string.mark_as_read), getReadOnlyPendingIntent(thread.getId(),
                        thread.getSubject().getUrl()))
                .setContentIntent(getPendingIntent(thread.getId(), thread.getSubject().getUrl()))
                .setColor(accentColor)
                .setGroup(NOTIFICATION_GROUP_ID)
                .build();
        showNotification(thread.getId(), toAdd);
    }

    private android.app.Notification getSummaryGroupNotification(@NonNull Notification thread, int accentColor, boolean toNotificationActivity) {
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), NotificationActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        return getNotification(thread.getSubject().getTitle(), thread.getRepository().getFullName())
                .setDefaults(PrefGetter.isNotificationSoundEnabled() ? NotificationCompat.DEFAULT_ALL : 0)
                .setContentIntent(toNotificationActivity ? pendingIntent : getPendingIntent(thread.getId(), thread.getSubject().getUrl()))
                .addAction(R.drawable.ic_github, getString(R.string.open), getPendingIntent(thread.getId(), thread
                        .getSubject().getUrl()))
                .addAction(R.drawable.ic_eye_off, getString(R.string.mark_as_read), getReadOnlyPendingIntent(thread.getId(), thread
                        .getSubject().getUrl()))
                .setWhen(thread.getUpdatedAt() != null ? thread.getUpdatedAt().getTime() : System.currentTimeMillis())
                .setShowWhen(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(accentColor)
                .setGroup(NOTIFICATION_GROUP_ID)
                .setGroupSummary(true)
                .build();
    }

    private NotificationCompat.Builder getNotification(@NonNull String title, @NonNull String message) {
        return new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);
    }

    private void showNotification(long id, android.app.Notification notification) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(InputHelper.getSafeIntId(id), notification);
    }

    private PendingIntent getReadOnlyPendingIntent(long id, @NonNull String url) {
        Intent intent = ReadNotificationService.start(getApplicationContext(), id, url, true);
        return PendingIntent.getService(getApplicationContext(), InputHelper.getSafeIntId(id) / 2, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getPendingIntent(long id, @NonNull String url) {
        Intent intent = ReadNotificationService.start(getApplicationContext(), id, url);
        return PendingIntent.getService(getApplicationContext(), InputHelper.getSafeIntId(id), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override public int onRunTask(TaskParams job) {
        if (PrefGetter.getNotificationTaskDuration() == -1) {
            scheduleJob(this, -1, false);
            finishJob(job);
            return RESULT_SUCCESS;
        }
        Login login = null;
        try {
            login = Login.getUser();
        } catch (Exception ignored) {}
        if (login != null) {
            RestProvider.getNotificationService()
                    .getNotifications(ParseDateFormat.getLastWeekDate())
                    .subscribeOn(Schedulers.io())
                    .subscribe(item -> {
                        AppHelper.cancelAllNotifications(getApplicationContext());
                        if (item != null) {
                            onSave(item.getItems(), job);
                        } else {
                            finishJob(job);
                        }
                    }, Throwable::printStackTrace);
        } else {
            finishJob(job);
        }
        return RESULT_SUCCESS;
    }

    @Override public void onInitializeTasks() {
        super.onInitializeTasks();
        scheduleJob(this);
    }

    private static class CustomNotificationModel {
        public String url;
        public Notification notification;
        public Comment comment;
    }
}