package com.fastaccess.provider.tasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.data.dao.NotificationThreadModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.modules.main.MainView;
import com.fastaccess.ui.modules.notification.NotificationActivityView;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.List;

import rx.schedulers.Schedulers;

/**
 * Created by Kosh on 19 Feb 2017, 6:32 PM
 */

public class NotificationJobTask extends JobService {
    private final static String EVERY_30_MINS = "every_30_mins";

    @Override public boolean onStartJob(JobParameters job) {
        if (LoginModel.getUser() != null) {
            RestProvider.getNotificationService()
                    .getNotifications(0)
                    .subscribeOn(Schedulers.io())
                    .subscribe(item -> {
                        if (item != null) onSave(item.getItems());
                    }, Throwable::printStackTrace);
        }
        return false;
    }

    @Override public boolean onStopJob(JobParameters job) {
        return false;
    }

    public static void scheduleJob(@NonNull Context context) {
        scheduleJob(context, PrefGetter.getNotificationTaskDuration(context) == 0 ? (30 * 60) : PrefGetter.getNotificationTaskDuration(context),
                false);
    }

    public static void scheduleJob(@NonNull Context context, int duration, boolean cancel) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        if (cancel) dispatcher.cancel(EVERY_30_MINS);
        Job.Builder builder = dispatcher
                .newJobBuilder()
                .setTag(EVERY_30_MINS)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setTrigger(Trigger.executionWindow(10, duration))
                .setService(NotificationJobTask.class);
        dispatcher.mustSchedule(builder.build());
    }

    private void onSave(@Nullable List<NotificationThreadModel> notificationThreadModels) {
        if (notificationThreadModels != null) {
            NotificationThreadModel.save(notificationThreadModels)
                    .subscribe(() -> onNotifyUser(notificationThreadModels));
        }
    }

    private void onNotifyUser(@NonNull List<NotificationThreadModel> notificationThreadModels) {
        long count = Stream.of(notificationThreadModels)
                .filter(NotificationThreadModel::isUnread)
                .count();
        if (count > 0) {
            Context context = getApplicationContext();
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addParentStack(MainView.class);
            Intent intent = new Intent(this, NotificationActivityView.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            taskStackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_announcement)
                    .setContentTitle(context.getString(R.string.notifictions))
                    .setContentText(context.getString(R.string.unread_notification) + " (" + count + ")")
                    .setContentIntent(pendingIntent)
                    .setNumber((int) count)
                    .addAction(R.drawable.ic_github, context.getString(R.string.open), pendingIntent)
                    .build();
            ((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE)).notify(BundleConstant.REQUEST_CODE, notification);
        }
    }
}
