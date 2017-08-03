package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fastaccess.R;
import com.fastaccess.data.dao.ReactionsModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.provider.timeline.HtmlHelper;
import com.fastaccess.provider.timeline.handler.drawable.DrawableGetter;
import com.fastaccess.ui.adapter.IssuesTimelineAdapter;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.adapter.callback.ReactionsCallback;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class TimelineCommentsViewHolder extends BaseViewHolder<TimelineModel> {


    @BindView(R.id.avatarView) AvatarLayout avatar;
    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.toggle) ForegroundImageView toggle;
    @BindView(R.id.commentMenu) ForegroundImageView commentMenu;
    @BindView(R.id.toggleHolder) LinearLayout toggleHolder;
    @BindView(R.id.thumbsUp) FontTextView thumbsUp;
    @BindView(R.id.thumbsDown) FontTextView thumbsDown;
    @BindView(R.id.laugh) FontTextView laugh;
    @BindView(R.id.hurray) FontTextView hurray;
    @BindView(R.id.sad) FontTextView sad;
    @BindView(R.id.heart) FontTextView heart;
    @BindView(R.id.emojiesList) HorizontalScrollView emojiesList;
    @BindView(R.id.commentOptions) RelativeLayout commentOptions;
    @BindView(R.id.comment) FontTextView comment;
    @BindView(R.id.reactionsText) FontTextView reactionsText;
    @BindView(R.id.owner) FontTextView owner;
    private OnToggleView onToggleView;
    private boolean showEmojies;
    private ReactionsCallback reactionsCallback;
    private ViewGroup viewGroup;
    private String repoOwner;
    private String poster;

    @Override public void onClick(View v) {
        if (v.getId() == R.id.toggle || v.getId() == R.id.toggleHolder) {
            if (onToggleView != null) {
                int position = getAdapterPosition();
                onToggleView.onToggle(position, !onToggleView.isCollapsed(position));
                onToggle(onToggleView.isCollapsed(position), true);
            }
        } else {
            addReactionCount(v);
            super.onClick(v);
        }
    }

    private TimelineCommentsViewHolder(@NonNull View itemView, @NonNull ViewGroup viewGroup, @Nullable IssuesTimelineAdapter adapter,
                                       @NonNull OnToggleView onToggleView, boolean showEmojies, @NonNull ReactionsCallback reactionsCallback,
                                       String repoOwner, String poster) {
        super(itemView, adapter);
        this.viewGroup = viewGroup;
        this.onToggleView = onToggleView;
        this.showEmojies = showEmojies;
        this.reactionsCallback = reactionsCallback;
        this.repoOwner = repoOwner;
        this.poster = poster;
        itemView.setOnClickListener(null);
        itemView.setOnLongClickListener(null);
        commentMenu.setOnClickListener(this);
        commentMenu.setOnLongClickListener(this);
        toggleHolder.setOnClickListener(this);
        toggle.setOnClickListener(this);
        laugh.setOnClickListener(this);
        sad.setOnClickListener(this);
        thumbsDown.setOnClickListener(this);
        thumbsUp.setOnClickListener(this);
        hurray.setOnClickListener(this);
        laugh.setOnLongClickListener(this);
        sad.setOnLongClickListener(this);
        thumbsDown.setOnLongClickListener(this);
        thumbsUp.setOnLongClickListener(this);
        hurray.setOnLongClickListener(this);
        heart.setOnLongClickListener(this);
        heart.setOnClickListener(this);
    }

    public static TimelineCommentsViewHolder newInstance(@NonNull ViewGroup viewGroup, @Nullable IssuesTimelineAdapter adapter,
                                                         @NonNull OnToggleView onToggleView, boolean showEmojies,
                                                         @NonNull ReactionsCallback reactionsCallback, String repoOwner, String poster) {
        return new TimelineCommentsViewHolder(getView(viewGroup, R.layout.comments_row_item), viewGroup, adapter,
                onToggleView, showEmojies, reactionsCallback, repoOwner, poster);
    }

    @Override public void bind(@NonNull TimelineModel timelineModel) {
        Comment commentsModel = timelineModel.getComment();
        if (commentsModel.getUser() != null) {
            avatar.setUrl(commentsModel.getUser().getAvatarUrl(), commentsModel.getUser().getLogin(),
                    false, LinkParserHelper.isEnterprise(commentsModel.getHtmlUrl()));
            name.setText(commentsModel.getUser() != null ? commentsModel.getUser().getLogin() : "Anonymous");
            boolean isRepoOwner = TextUtils.equals(commentsModel.getUser().getLogin(), repoOwner);
            if (isRepoOwner) {
                owner.setVisibility(View.VISIBLE);
                owner.setText(R.string.owner);
            } else {
                boolean isPoster = TextUtils.equals(commentsModel.getUser().getLogin(), poster);
                if (isPoster) {
                    owner.setVisibility(View.VISIBLE);
                    owner.setText(R.string.original_poster);
                } else {
                    owner.setText(null);
                    owner.setVisibility(View.GONE);
                }
            }
        } else {
            avatar.setUrl(null, null, false, false);
            name.setText(null);
        }
        if (!InputHelper.isEmpty(commentsModel.getBodyHtml())) {
            String body = commentsModel.getBodyHtml();
            if (!InputHelper.isEmpty(commentsModel.getPath()) && commentsModel.getPosition() > 0) {
                body = "<small color='grey'><i>Commented at <b>line(" +
                        (commentsModel.getLine() > 0 ? commentsModel.getLine() : commentsModel.getPosition()) + ")</b> in "
                        + commentsModel.getPath() + "</i></small><br/>" + body;
            }
            HtmlHelper.htmlIntoTextView(comment, body, viewGroup.getWidth());
        } else {
            comment.setText("");
        }
        if (commentsModel.getCreatedAt().before(commentsModel.getUpdatedAt())) {
            date.setText(String.format("%s %s", ParseDateFormat.getTimeAgo(commentsModel.getCreatedAt()), itemView
                    .getResources().getString(R.string.edited)));
        } else {
            date.setText(ParseDateFormat.getTimeAgo(commentsModel.getCreatedAt()));
        }
        if (showEmojies) {
            if (commentsModel.getReactions() != null) {
                ReactionsModel reaction = commentsModel.getReactions();
                appendEmojies(reaction);
            }
        }
        emojiesList.setVisibility(showEmojies ? View.VISIBLE : View.GONE);
        if (onToggleView != null) onToggle(onToggleView.isCollapsed(getAdapterPosition()), false);
    }

    private void addReactionCount(View v) {
        if (adapter != null) {
            TimelineModel timelineModel = (TimelineModel) adapter.getItem(getAdapterPosition());
            if (timelineModel == null) return;
            Comment comment = timelineModel.getComment();
            if (comment != null) {
                boolean isReacted = reactionsCallback == null || reactionsCallback.isPreviouslyReacted(comment.getId(), v.getId());
                boolean isCallingApi = reactionsCallback != null && reactionsCallback.isCallingApi(comment.getId(), v.getId());
//                if (isCallingApi) return;
                ReactionsModel reactionsModel = comment.getReactions() != null ? comment.getReactions() : new ReactionsModel();
                switch (v.getId()) {
                    case R.id.heart:
                        reactionsModel.setHeart(!isReacted ? reactionsModel.getHeart() + 1 : reactionsModel.getHeart() - 1);
                        break;
                    case R.id.sad:
                        reactionsModel.setConfused(!isReacted ? reactionsModel.getConfused() + 1 : reactionsModel.getConfused() - 1);
                        break;
                    case R.id.thumbsDown:
                        reactionsModel.setMinusOne(!isReacted ? reactionsModel.getMinusOne() + 1 : reactionsModel.getMinusOne() - 1);
                        break;
                    case R.id.thumbsUp:
                        reactionsModel.setPlusOne(!isReacted ? reactionsModel.getPlusOne() + 1 : reactionsModel.getPlusOne() - 1);
                        break;
                    case R.id.laugh:
                        reactionsModel.setLaugh(!isReacted ? reactionsModel.getLaugh() + 1 : reactionsModel.getLaugh() - 1);
                        break;
                    case R.id.hurray:
                        reactionsModel.setHooray(!isReacted ? reactionsModel.getHooray() + 1 : reactionsModel.getHooray() - 1);
                        break;
                }
                comment.setReactions(reactionsModel);
                appendEmojies(reactionsModel);
                timelineModel.setComment(comment);
            }
        }
    }

    private void appendEmojies(ReactionsModel reaction) {
        SpannableBuilder spannableBuilder = SpannableBuilder.builder();
        reactionsText.setText("");
        thumbsUp.setText(SpannableBuilder.builder()
                .append(CommentsHelper.getThumbsUp()).append(" ")
                .append(String.valueOf(reaction.getPlusOne()))
                .append("   "));
        thumbsDown.setText(SpannableBuilder.builder()
                .append(CommentsHelper.getThumbsDown()).append(" ")
                .append(String.valueOf(reaction.getMinusOne()))
                .append("   "));
        hurray.setText(SpannableBuilder.builder()
                .append(CommentsHelper.getHooray()).append(" ")
                .append(String.valueOf(reaction.getHooray()))
                .append("   "));
        sad.setText(SpannableBuilder.builder()
                .append(CommentsHelper.getSad()).append(" ")
                .append(String.valueOf(reaction.getConfused()))
                .append("   "));
        laugh.setText(SpannableBuilder.builder()
                .append(CommentsHelper.getLaugh()).append(" ")
                .append(String.valueOf(reaction.getLaugh()))
                .append("   "));
        heart.setText(SpannableBuilder.builder()
                .append(CommentsHelper.getHeart()).append(" ")
                .append(String.valueOf(reaction.getHeart())));
        if (reaction.getPlusOne() > 0) {
            spannableBuilder.append(CommentsHelper.getThumbsUp())
                    .append(" ")
                    .append(String.valueOf(reaction.getPlusOne()))
                    .append("   ");
        }
        if (reaction.getMinusOne() > 0) {
            spannableBuilder.append(CommentsHelper.getThumbsDown())
                    .append(" ")
                    .append(String.valueOf(reaction.getMinusOne()))
                    .append("   ");
        }
        if (reaction.getLaugh() > 0) {
            spannableBuilder.append(CommentsHelper.getLaugh())
                    .append(" ")
                    .append(String.valueOf(reaction.getLaugh()))
                    .append("   ");
        }
        if (reaction.getHooray() > 0) {
            spannableBuilder.append(CommentsHelper.getHooray())
                    .append(" ")
                    .append(String.valueOf(reaction.getHooray()))
                    .append("   ");
        }
        if (reaction.getConfused() > 0) {
            spannableBuilder.append(CommentsHelper.getSad())
                    .append(" ")
                    .append(String.valueOf(reaction.getConfused()))
                    .append("   ");
        }
        if (reaction.getHeart() > 0) {
            spannableBuilder.append(CommentsHelper.getHeart())
                    .append(" ")
                    .append(String.valueOf(reaction.getHeart()));
        }
        if (spannableBuilder.length() > 0) {
            reactionsText.setText(spannableBuilder);
            if (!onToggleView.isCollapsed(getAdapterPosition())) {
                reactionsText.setVisibility(View.VISIBLE);
            }
        } else {
            reactionsText.setVisibility(View.GONE);
        }
    }

    private void onToggle(boolean expanded, boolean animate) {
        if (animate) {
            TransitionManager.beginDelayedTransition(viewGroup, new ChangeBounds());
        }
        toggle.setRotation(!expanded ? 0.0F : 180F);
        commentOptions.setVisibility(!expanded ? View.GONE : View.VISIBLE);
        if (!InputHelper.isEmpty(reactionsText)) {
            reactionsText.setVisibility(!expanded ? View.VISIBLE : View.GONE);
        }
    }

    @Override protected void onViewIsDetaching() {
        DrawableGetter drawableGetter = (DrawableGetter) comment.getTag(R.id.drawable_callback);
        if (drawableGetter != null) {
            drawableGetter.clear(drawableGetter);
        }
    }
}
