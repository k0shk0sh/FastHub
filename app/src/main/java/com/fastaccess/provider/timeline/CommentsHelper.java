package com.fastaccess.provider.timeline;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.provider.tasks.git.ReactionService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 30 Mar 2017, 6:44 PM
 */

public class CommentsHelper {

    private static final int LAUGH = 0x1F601;
    private static final int SAD = 0x1F615;
    private static final int THUMBS_UP = 0x1f44d;
    private static final int THUMBS_DOWN = 0x1f44e;
    private static final int HOORAY = 0x1f389;
    private static final int HEART = 0x2764;


    public static boolean isOwner(@NonNull String currentLogin, @NonNull String repoOwner, @NonNull String commentUser) {
        return currentLogin.equalsIgnoreCase(repoOwner) || currentLogin.equalsIgnoreCase(commentUser);
    }

    public static void handleReactions(@NonNull Context context, @NonNull String login, @NonNull String repoId,
                                       @IdRes int id, long commentId, boolean commit, boolean isDelete) {
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
            ReactionService.start(context, login, repoId, commentId, type, commit, isDelete);
        }
    }

    public static String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public static String getEmoji(@NonNull ReactionTypes reactionTypes) {
        switch (reactionTypes) {
            case HEART:
                return getHeart();
            case HOORAY:
                return getHooray();
            case PLUS_ONE:
                return getThumbsUp();
            case MINUS_ONE:
                return getThumbsDown();
            case CONFUSED:
                return getSad();
            case LAUGH:
                return getLaugh();
            default:
                return getThumbsUp();
        }
    }

    public static String getLaugh() {
        return getEmojiByUnicode(LAUGH);
    }

    public static String getSad() {
        return getEmojiByUnicode(SAD);
    }

    public static String getThumbsUp() {
        return getEmojiByUnicode(THUMBS_UP);
    }

    public static String getThumbsDown() {
        return getEmojiByUnicode(THUMBS_DOWN);
    }

    public static String getHooray() {
        return getEmojiByUnicode(HOORAY);
    }

    public static String getHeart() {
        return getEmojiByUnicode(HEART);
    }

    @NonNull public static ArrayList<String> getUsers(@NonNull List<Comment> comments) {
        return Stream.of(comments)
                .map(comment -> comment.getUser().getLogin())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @NonNull public static ArrayList<String> getUsersByTimeline(@NonNull List<TimelineModel> comments) {
        return Stream.of(comments)
                .filter(timelineModel -> timelineModel.getComment() != null && timelineModel.getComment().getUser() != null)
                .map(comment -> comment.getComment().getUser().getLogin())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
