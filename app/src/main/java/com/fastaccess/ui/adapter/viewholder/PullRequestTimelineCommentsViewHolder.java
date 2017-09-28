package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fastaccess.R;
import com.fastaccess.data.dao.ReactionsModel;
import com.fastaccess.data.dao.timeline.PullRequestTimelineModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.provider.timeline.HtmlHelper;
import com.fastaccess.provider.timeline.handler.drawable.DrawableGetter;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

import butterknife.BindView;
import github.PullRequestTimelineQuery;
import github.type.ReactionContent;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class PullRequestTimelineCommentsViewHolder extends BaseViewHolder<PullRequestTimelineModel> {


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
    private ViewGroup viewGroup;

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

    private PullRequestTimelineCommentsViewHolder(@NonNull View itemView, @NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter,
                                                  @NonNull OnToggleView onToggleView) {
        super(itemView, adapter);
        this.viewGroup = viewGroup;
        this.onToggleView = onToggleView;
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

    public static PullRequestTimelineCommentsViewHolder newInstance(@NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter,
                                                                    @NonNull OnToggleView onToggleView) {
        return new PullRequestTimelineCommentsViewHolder(getView(viewGroup, R.layout.comments_row_item), viewGroup, adapter, onToggleView);
    }

    @Override public void bind(@NonNull PullRequestTimelineModel timelineModel) {
        PullRequestTimelineQuery.AsIssueComment commentsModel = timelineModel.getNode().asIssueComment();
        if (commentsModel != null) {
            PullRequestTimelineQuery.Author4 author3 = commentsModel.author();
            owner.setVisibility(View.VISIBLE);
            owner.setText("none".equalsIgnoreCase(commentsModel.authorAssociation().name().toLowerCase())
                          ? "" : commentsModel.authorAssociation().name().toLowerCase());
            if (author3 != null) {
                avatar.setUrl(author3.avatarUrl().toString(), author3.login(),
                        false, LinkParserHelper.isEnterprise(author3.url().toString()));
                name.setText(author3.login());
            } else {
                avatar.setUrl(null, null, false, false);
                name.setText(null);
            }
            if (!InputHelper.isEmpty(commentsModel.bodyHTML())) {
                String body = commentsModel.bodyHTML().toString();
                HtmlHelper.htmlIntoTextView(comment, body, viewGroup.getWidth());
            } else {
                comment.setText("");
            }
            if (commentsModel.createdAt().equals(commentsModel.lastEditedAt())) {
                date.setText(String.format("%s %s", ParseDateFormat.getTimeAgo(commentsModel.lastEditedAt().toString()), itemView
                        .getResources().getString(R.string.edited)));
            } else {
                date.setText(ParseDateFormat.getTimeAgo(commentsModel.createdAt().toString()));
            }
            appendEmojies(timelineModel.getReactions());
            emojiesList.setVisibility(View.VISIBLE);
            if (onToggleView != null) onToggle(onToggleView.isCollapsed(getAdapterPosition()), false);
        }
    }

    private void addReactionCount(View v) {
        if (adapter != null) {
            PullRequestTimelineModel timelineModel = (PullRequestTimelineModel) adapter.getItem(getAdapterPosition());
            if (timelineModel == null) return;
            List<ReactionsModel> reactions = timelineModel.getReactions();
            if (reactions != null && !reactions.isEmpty()) {
                int reactionIndex = getReaction(v.getId(), reactions);
                if (reactionIndex != -1) {
                    ReactionsModel reaction = reactions.get(reactionIndex);
                    if (!reaction.isViewerHasReacted()) {
                        reaction.setViewerHasReacted(true);
                        reaction.setTotal_count(reaction.getTotal_count() + 1);
                    } else {
                        reaction.setViewerHasReacted(false);
                        reaction.setTotal_count(reaction.getTotal_count() - 1);
                    }
                    reactions.set(reactionIndex, reaction);
                }
                appendEmojies(reactions);
                timelineModel.setReactions(reactions);
            }
        }
    }

    private int getReaction(int id, @NonNull List<ReactionsModel> reactionGroup) {
        for (int i = 0; i < reactionGroup.size(); i++) {
            ReactionsModel reactionGroup1 = reactionGroup.get(i);
            if (id == R.id.heart && reactionGroup1.getContent().equalsIgnoreCase(ReactionContent.HEART.name())) {
                return i;
            } else if (id == R.id.sad && reactionGroup1.getContent().equalsIgnoreCase(ReactionContent.CONFUSED.name())) {
                return i;
            } else if (id == R.id.hurray && reactionGroup1.getContent().equalsIgnoreCase(ReactionContent.HOORAY.name())) {
                return i;
            } else if (id == R.id.laugh && reactionGroup1.getContent().equalsIgnoreCase(ReactionContent.LAUGH.name())) {
                return i;
            } else if (id == R.id.thumbsDown && reactionGroup1.getContent().equalsIgnoreCase(ReactionContent.THUMBS_DOWN.name())) {
                return i;
            } else if (id == R.id.thumbsUp && reactionGroup1.getContent().equalsIgnoreCase(ReactionContent.THUMBS_UP.name())) {
                return i;
            }
        }
        return -1;
    }

    private void appendEmojies(@NonNull List<ReactionsModel> reactions) {
        reactionsText.setText("");
        SpannableBuilder spannableBuilder = SpannableBuilder.builder();
        for (ReactionsModel reaction : reactions) {
            CharSequence charSequence = null;
            if (reaction.getContent().equalsIgnoreCase(ReactionContent.THUMBS_UP.name())) {
                charSequence = SpannableBuilder.builder()
                        .append(CommentsHelper.getThumbsUp()).append(" ")
                        .append(String.valueOf(reaction.getTotal_count()))
                        .append("   ");
                thumbsUp.setText(charSequence);
            } else if (reaction.getContent().equalsIgnoreCase(ReactionContent.THUMBS_DOWN.name())) {
                charSequence = SpannableBuilder.builder()
                        .append(CommentsHelper.getThumbsDown()).append(" ")
                        .append(String.valueOf(reaction.getTotal_count()))
                        .append("   ");
                thumbsDown.setText(charSequence);
            } else if (reaction.getContent().equalsIgnoreCase(ReactionContent.LAUGH.name())) {
                charSequence = SpannableBuilder.builder()
                        .append(CommentsHelper.getLaugh()).append(" ")
                        .append(String.valueOf(reaction.getTotal_count()))
                        .append("   ");
                laugh.setText(charSequence);
            } else if (reaction.getContent().equalsIgnoreCase(ReactionContent.HOORAY.name())) {
                charSequence = SpannableBuilder.builder()
                        .append(CommentsHelper.getHooray()).append(" ")
                        .append(String.valueOf(reaction.getTotal_count()))
                        .append("   ");
                hurray.setText(charSequence);
            } else if (reaction.getContent().equalsIgnoreCase(ReactionContent.HEART.name())) {
                charSequence = SpannableBuilder.builder()
                        .append(CommentsHelper.getHeart()).append(" ")
                        .append(String.valueOf(reaction.getTotal_count()))
                        .append("   ");
                heart.setText(charSequence);
            } else if (reaction.getContent().equalsIgnoreCase(ReactionContent.CONFUSED.name())) {
                charSequence = SpannableBuilder.builder()
                        .append(CommentsHelper.getSad()).append(" ")
                        .append(String.valueOf(reaction.getTotal_count()))
                        .append("   ");
                sad.setText(charSequence);
            }
            if (charSequence != null && reaction.getTotal_count() > 0) {
                spannableBuilder.append(charSequence);
            }
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
