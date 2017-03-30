package com.fastaccess.provider.comments;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.fastaccess.R;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.provider.tasks.git.ReactionService;

/**
 * Created by Kosh on 30 Mar 2017, 6:44 PM
 */

public class CommentsHandler {


    public static void handleReactions(@NonNull Context context, @NonNull String login, @NonNull String repoId,
                                       @IdRes int id, long commentId, boolean isCommit) {
        ReactionTypes type = null;
        switch (id) {
            case R.id.heart:
                type = ReactionTypes.HEART;
                break;
            case R.id.sad:
                type = ReactionTypes.CONFUSED;
                break;
            case R.id.thumbsDown:
                type = ReactionTypes.MINUS_ONE;
                break;
            case R.id.thumbsUp:
                type = ReactionTypes.PLUS_ONE;
                break;
            case R.id.laugh:
                type = ReactionTypes.LAUGH;
                break;
            case R.id.hurray:
                type = ReactionTypes.HOORAY;
                break;
        }
        if (type != null) {
            ReactionService.start(context, login, repoId, commentId, type, isCommit);
        }
    }

}
