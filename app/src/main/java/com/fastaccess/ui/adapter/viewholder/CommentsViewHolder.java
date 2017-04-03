package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.ReactionsModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.comments.CommentsHelper;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;
import com.prettifier.pretty.PrettifyWebView;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class CommentsViewHolder extends BaseViewHolder<Comment> {

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
    @BindView(R.id.reactionsText) TextView reactionsText;
    private String login;
    private OnToggleView onToggleView;
    private boolean showEmojies;

    @Override public void onClick(View v) {
        if (v.getId() == R.id.toggle || v.getId() == R.id.toggleHolder || v.getId() == R.id.reactionsText) {
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
            Comment comment = (Comment) adapter.getItem(getAdapterPosition());
            if (comment != null) {
                ReactionsModel reactionsModel = comment.getReactions() != null ? comment.getReactions() : new ReactionsModel();
                switch (v.getId()) {
                    case R.id.heart:
                        reactionsModel.setHeart(reactionsModel.getHeart() + 1);
                        break;
                    case R.id.sad:
                        reactionsModel.setConfused(reactionsModel.getConfused() + 1);
                        break;
                    case R.id.thumbsDown:
                        reactionsModel.setMinusOne(reactionsModel.getMinusOne() + 1);
                        break;
                    case R.id.thumbsUp:
                        reactionsModel.setPlusOne(reactionsModel.getPlusOne() + 1);
                        break;
                    case R.id.laugh:
                        reactionsModel.setLaugh(reactionsModel.getLaugh() + 1);
                        break;
                    case R.id.hurray:
                        reactionsModel.setHooray(reactionsModel.getHooray() + 1);
                        break;
                }
                comment.setReactions(reactionsModel);
                appendEmojies(reactionsModel);
                bind(comment);
            }
        }
    }

    private CommentsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter,
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
        reactionsText.setOnClickListener(this);
    }

    public static CommentsViewHolder newInstance(@NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter,
                                                 @NonNull String login, @NonNull OnToggleView onToggleView, boolean showEmojies) {
        return new CommentsViewHolder(getView(viewGroup, R.layout.comments_row_item), adapter, login, onToggleView, showEmojies);
    }

    @Override public void bind(@NonNull Comment commentsModel) {
        if (commentsModel.getUser() != null) {
            avatar.setUrl(commentsModel.getUser().getAvatarUrl(), commentsModel.getUser().getLogin(), commentsModel.getUser().isOrganizationType());
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
                appendEmojies(reaction);
            }
        }
        emojiesList.setVisibility(showEmojies ? View.VISIBLE : View.GONE);
        if (onToggleView != null) onToggle(onToggleView.isCollapsed(getAdapterPosition()));
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
        hooray.setText(SpannableBuilder.builder()
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

    private void onToggle(boolean expanded) {
        toggle.setRotation(!expanded ? 0.0F : 180F);
        commentOptions.setVisibility(!expanded ? View.GONE : View.VISIBLE);
        if (!InputHelper.isEmpty(reactionsText)) {
            reactionsText.setVisibility(!expanded ? View.VISIBLE : View.GONE);
        }
    }

}
