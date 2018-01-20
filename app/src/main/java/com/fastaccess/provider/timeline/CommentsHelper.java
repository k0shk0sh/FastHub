package com.fastaccess.provider.timeline;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.ReactionsModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.provider.tasks.git.ReactionService;
import com.fastaccess.ui.widgets.SpannableBuilder;

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
                                       @IdRes int id, long commentId, boolean commit, boolean isDelete,
                                       boolean isEnterprise) {
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
            ReactionService.start(context, login, repoId, commentId, type, commit, isDelete, isEnterprise);
        }
    }

    private static String getEmojiByUnicode(int unicode) {
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

    public static void appendEmojies(@NonNull ReactionsModel reaction, @NonNull TextView thumbsUp,
                                     @NonNull TextView thumbsUpReaction, @NonNull TextView thumbsDown,
                                     @NonNull TextView thumbsDownReaction, @NonNull TextView hurray,
                                     @NonNull TextView hurrayReaction, @NonNull TextView sad,
                                     @NonNull TextView sadReaction, @NonNull TextView laugh,
                                     @NonNull TextView laughReaction, @NonNull TextView heart,
                                     @NonNull TextView heartReaction, @NonNull View reactionsList) {
        SpannableBuilder spannableBuilder = SpannableBuilder.builder()
                .append(CommentsHelper.getThumbsUp()).append(" ")
                .append(String.valueOf(reaction.getPlusOne()))
                .append("   ");
        thumbsUp.setText(spannableBuilder);
        thumbsUpReaction.setText(spannableBuilder);
        thumbsUpReaction.setVisibility(reaction.getPlusOne() > 0 ? View.VISIBLE : View.GONE);
        spannableBuilder = SpannableBuilder.builder()
                .append(CommentsHelper.getThumbsDown()).append(" ")
                .append(String.valueOf(reaction.getMinusOne()))
                .append("   ");
        thumbsDown.setText(spannableBuilder);
        thumbsDownReaction.setText(spannableBuilder);
        thumbsDownReaction.setVisibility(reaction.getMinusOne() > 0 ? View.VISIBLE : View.GONE);
        spannableBuilder = SpannableBuilder.builder()
                .append(CommentsHelper.getHooray()).append(" ")
                .append(String.valueOf(reaction.getHooray()))
                .append("   ");
        hurray.setText(spannableBuilder);
        hurrayReaction.setText(spannableBuilder);
        hurrayReaction.setVisibility(reaction.getHooray() > 0 ? View.VISIBLE : View.GONE);
        spannableBuilder = SpannableBuilder.builder()
                .append(CommentsHelper.getSad()).append(" ")
                .append(String.valueOf(reaction.getConfused()))
                .append("   ");
        sad.setText(spannableBuilder);
        sadReaction.setText(spannableBuilder);
        sadReaction.setVisibility(reaction.getConfused() > 0 ? View.VISIBLE : View.GONE);
        spannableBuilder = SpannableBuilder.builder()
                .append(CommentsHelper.getLaugh()).append(" ")
                .append(String.valueOf(reaction.getLaugh()))
                .append("   ");
        laugh.setText(spannableBuilder);
        laughReaction.setText(spannableBuilder);
        laughReaction.setVisibility(reaction.getLaugh() > 0 ? View.VISIBLE : View.GONE);
        spannableBuilder = SpannableBuilder.builder()
                .append(CommentsHelper.getHeart()).append(" ")
                .append(String.valueOf(reaction.getHeart()));
        heart.setText(spannableBuilder);
        heartReaction.setText(spannableBuilder);
        heartReaction.setVisibility(reaction.getHeart() > 0 ? View.VISIBLE : View.GONE);
        if (reaction.getPlusOne() > 0 || reaction.getMinusOne() > 0
                || reaction.getLaugh() > 0 || reaction.getHooray() > 0
                || reaction.getConfused() > 0 || reaction.getHeart() > 0) {
            reactionsList.setVisibility(View.VISIBLE);
            reactionsList.setTag(true);
        } else {
            reactionsList.setVisibility(View.GONE);
            reactionsList.setTag(false);
        }
    }


}
