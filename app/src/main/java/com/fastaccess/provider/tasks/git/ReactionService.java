package com.fastaccess.provider.tasks.git;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.fastaccess.R;
import com.fastaccess.data.dao.PostReactionModel;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;

/**
 * Created by Kosh on 29 Mar 2017, 9:59 PM
 */

public class ReactionService extends IntentService {

    private NotificationCompat.Builder notification;
    private NotificationManager notificationManager;

    public static void start(@NonNull Context context, @NonNull String login, @NonNull String repo,
                             long commentId, ReactionTypes reactionType, boolean isCommit, boolean isDelete,
                             boolean isEnterprise) {
        Intent intent = new Intent(context, ReactionService.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, isCommit)
                .put(BundleConstant.EXTRA_TWO, login)
                .put(BundleConstant.EXTRA_THREE, repo)
                .put(BundleConstant.EXTRA_FOUR, isDelete)
                .put(BundleConstant.ID, commentId)
                .put(BundleConstant.EXTRA_TYPE, reactionType)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .end());
        context.startService(intent);
    }

    public ReactionService() {
        super(ReactionService.class.getSimpleName());
    }

    @Override protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            ReactionTypes reactionType = (ReactionTypes) bundle.getSerializable(BundleConstant.EXTRA_TYPE);
            boolean isCommit = bundle.getBoolean(BundleConstant.EXTRA);
            String login = bundle.getString(BundleConstant.EXTRA_TWO);
            String repo = bundle.getString(BundleConstant.EXTRA_THREE);
            long commentId = bundle.getLong(BundleConstant.ID);
            boolean isEnterprise = bundle.getBoolean(BundleConstant.IS_ENTERPRISE);
            if (InputHelper.isEmpty(login) || InputHelper.isEmpty(repo) || reactionType == null) {
                stopSelf();
                return;
            }
            if (isCommit) {
                postCommit(reactionType, login, repo, commentId, isEnterprise);
            } else {
                post(reactionType, login, repo, commentId, isEnterprise);
            }
        }
    }

    private void post(@NonNull ReactionTypes reactionType, @NonNull String login, @NonNull String repo, long commentId, boolean isEnterprise) {
        RxHelper.safeObservable(RestProvider.getReactionsService(isEnterprise)
                .postIssueCommentReaction(new PostReactionModel(reactionType.getContent()), login, repo, commentId))
                .doOnSubscribe(disposable -> showNotification(getNotification(reactionType), (int) commentId))
                .subscribe(response -> hideNotification((int) commentId), throwable -> hideNotification((int) commentId));
    }

    private void postCommit(@NonNull ReactionTypes reactionType, @NonNull String login, @NonNull String repo, long commentId, boolean isEnterprise) {
        RxHelper.safeObservable(RestProvider.getReactionsService(isEnterprise)
                .postCommitReaction(new PostReactionModel(reactionType.getContent()), login, repo, commentId))
                .doOnSubscribe(disposable -> showNotification(getNotification(reactionType), (int) commentId))
                .subscribe(response -> hideNotification((int) commentId), throwable -> hideNotification((int) commentId));
    }

    public NotificationCompat.Builder getNotification(@NonNull ReactionTypes reactionTypes) {
        if (notification == null) {
            notification = new NotificationCompat.Builder(this, "reaction")
                    .setSmallIcon(R.drawable.ic_sync)
                    .setProgress(0, 100, true);
        }
        notification.setContentTitle(getString(R.string.posting_reaction, reactionTypes.getContent()));
        return notification;
    }

    public NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    private void showNotification(@NonNull NotificationCompat.Builder builder, int id) {
        getNotificationManager().notify(id, builder.build());
    }

    private void hideNotification(int id) {
        getNotificationManager().cancel(id);
    }
}
