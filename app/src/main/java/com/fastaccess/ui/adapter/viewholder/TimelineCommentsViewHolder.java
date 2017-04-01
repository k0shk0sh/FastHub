package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.ReactionsModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;
import com.prettifier.pretty.PrettifyWebView;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class TimelineCommentsViewHolder extends BaseViewHolder<TimelineModel> {

    @BindView(R.id.avatarView) AvatarLayout avatar;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.comment) PrettifyWebView comment;
    @BindView(R.id.thumbsUp) FontTextView thumbsUp;
    @BindView(R.id.thumbsDown) FontTextView thumbsDown;
    @BindView(R.id.laugh) FontTextView laugh;
    @BindView(R.id.sad) FontTextView sad;
    @BindView(R.id.hurray) FontTextView hooray;
    @BindView(R.id.heart) FontTextView heart;
    @BindView(R.id.toggle) View toggle;
    @BindView(R.id.delete) AppCompatImageView delete;
    @BindView(R.id.reply) AppCompatImageView reply;
    @BindView(R.id.edit) AppCompatImageView edit;
    @BindView(R.id.commentOptions) View commentOptions;
    @BindView(R.id.toggleHolder) View toggleHolder;
    @BindView(R.id.emojiesList) View emojiesList;
    private String login;
    private OnToggleView onToggleView;
    private boolean showEmojies;

    @Override public void onClick(View v) {
        if (v.getId() == R.id.toggle || v.getId() == R.id.toggleHolder) {
            if (onToggleView != null) {
                int position = getAdapterPosition();
                onToggleView.onToggle(position, !onToggleView.isCollapsed(position));
                onToggle(onToggleView.isCollapsed(position));
            }
        } else {
            addReactionCount(v);
            super.onClick(v);
        }
    }

    private void addReactionCount(View v) {
        if (adapter != null) {
            TimelineModel timelineModel = (TimelineModel) adapter.getItem(getAdapterPosition());
            if (timelineModel == null) return;
            Comment comment = timelineModel.getComment();
            if (comment != null) {
                ReactionsModel reactionsModel = comment.getReactions() != null ? comment.getReactions() : new ReactionsModel();
                switch (v.getId()) {
                    case R.id.heart:
                        reactionsModel.setHeart(reactionsModel.getHeart() + 1);
                        heart.setText(String.format("%s", reactionsModel.getHeart()));
                        break;
                    case R.id.sad:
                        reactionsModel.setConfused(reactionsModel.getConfused() + 1);
                        sad.setText(String.format("%s", reactionsModel.getConfused()));
                        break;
                    case R.id.thumbsDown:
                        reactionsModel.setMinusOne(reactionsModel.getMinusOne() + 1);
                        thumbsDown.setText(String.format("%s", reactionsModel.getMinusOne()));
                        break;
                    case R.id.thumbsUp:
                        reactionsModel.setPlusOne(reactionsModel.getPlusOne() + 1);
                        thumbsUp.setText(String.format("%s", reactionsModel.getPlusOne()));
                        break;
                    case R.id.laugh:
                        reactionsModel.setLaugh(reactionsModel.getLaugh() + 1);
                        laugh.setText(String.format("%s", reactionsModel.getLaugh()));
                        break;
                    case R.id.hurray:
                        reactionsModel.setHooray(reactionsModel.getHooray() + 1);
                        hooray.setText(String.format("%s", reactionsModel.getHooray()));
                        break;
                }
                comment.setReactions(reactionsModel);
                timelineModel.setComment(comment);
            }
        }
    }

    private TimelineCommentsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter,
                                       @NonNull String login, @NonNull OnToggleView onToggleView, boolean showEmojies) {
        super(itemView, adapter);
        this.login = login;
        this.onToggleView = onToggleView;
        this.showEmojies = showEmojies;
        itemView.setOnClickListener(null);
        itemView.setOnLongClickListener(null);
        reply.setOnClickListener(this);
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);
        toggleHolder.setOnClickListener(this);
        laugh.setOnClickListener(this);
        sad.setOnClickListener(this);
        thumbsDown.setOnClickListener(this);
        thumbsUp.setOnClickListener(this);
        hooray.setOnClickListener(this);
        heart.setOnClickListener(this);
    }

    public static TimelineCommentsViewHolder newInstance(@NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter,
                                                         @NonNull String login, @NonNull OnToggleView onToggleView, boolean showEmojies) {
        return new TimelineCommentsViewHolder(getView(viewGroup, R.layout.comments_row_item), adapter, login, onToggleView, showEmojies);
    }

    @Override public void bind(@NonNull TimelineModel timelineModel) {
        Comment commentsModel = timelineModel.getComment();
        if (commentsModel.getUser() != null) {
            avatar.setUrl(commentsModel.getUser().getAvatarUrl(), commentsModel.getUser().getLogin());
            delete.setVisibility(TextUtils.equals(commentsModel.getUser().getLogin(), login) ? View.VISIBLE : View.GONE);
            edit.setVisibility(TextUtils.equals(commentsModel.getUser().getLogin(), login) ? View.VISIBLE : View.GONE);
        } else {
            avatar.setUrl(null, null);
        }
        if (!InputHelper.isEmpty(commentsModel.getBodyHtml())) {
            comment.setNestedScrollingEnabled(false);
            comment.setGithubContent(commentsModel.getBodyHtml(), null, true);
        }
        name.setText(commentsModel.getUser() != null ? commentsModel.getUser().getLogin() : "Anonymous");
        date.setText(ParseDateFormat.getTimeAgo(commentsModel.getCreatedAt()));
        if (showEmojies) {
            if (commentsModel.getReactions() != null) {
                ReactionsModel reaction = commentsModel.getReactions();
                thumbsUp.setText(String.valueOf(reaction.getPlusOne()));
                thumbsDown.setText(String.valueOf(reaction.getMinusOne()));
                sad.setText(String.valueOf(reaction.getConfused()));
                laugh.setText(String.valueOf(reaction.getLaugh()));
                hooray.setText(String.valueOf(reaction.getHooray()));
                heart.setText(String.valueOf(reaction.getHeart()));
            }
        }
        emojiesList.setVisibility(showEmojies ? View.VISIBLE : View.GONE);
        if (onToggleView != null) onToggle(onToggleView.isCollapsed(getAdapterPosition()));
    }

    private void onToggle(boolean expanded) {
        toggle.setRotation(!expanded ? 0.0F : 180F);
        commentOptions.setVisibility(!expanded ? View.GONE : View.VISIBLE);
    }

}
