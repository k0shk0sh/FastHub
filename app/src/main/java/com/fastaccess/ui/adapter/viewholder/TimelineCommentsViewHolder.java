package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fastaccess.R;
import com.fastaccess.data.dao.ReactionsModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.ViewHelper;
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
    @BindView(R.id.owner) FontTextView owner;
    @BindView(R.id.pathText) FontTextView pathText;
    @BindView(R.id.reactionsList) View reactionsList;
    @BindView(R.id.thumbsUpReaction) FontTextView thumbsUpReaction;
    @BindView(R.id.thumbsDownReaction) FontTextView thumbsDownReaction;
    @BindView(R.id.laughReaction) FontTextView laughReaction;
    @BindView(R.id.hurrayReaction) FontTextView hurrayReaction;
    @BindView(R.id.sadReaction) FontTextView sadReaction;
    @BindView(R.id.heartReaction) FontTextView heartReaction;
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
            super.onClick(v);
            addReactionCount(v);
        }
    }

    private TimelineCommentsViewHolder(@NonNull View itemView, @NonNull ViewGroup viewGroup, @Nullable IssuesTimelineAdapter adapter,
                                       @NonNull OnToggleView onToggleView, boolean showEmojies, @NonNull ReactionsCallback reactionsCallback,
                                       String repoOwner, String poster) {
        super(itemView, adapter);
        if (adapter != null && adapter.getRowWidth() == 0) {
            itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override public boolean onPreDraw() {
                    itemView.getViewTreeObserver().removeOnPreDrawListener(this);
                    adapter.setRowWidth(itemView.getWidth() - ViewHelper.dpToPx(itemView.getContext(), 48));
                    return false;
                }
            });
        }
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
        heart.setOnClickListener(this);
        laugh.setOnLongClickListener(this);
        sad.setOnLongClickListener(this);
        thumbsDown.setOnLongClickListener(this);
        thumbsUp.setOnLongClickListener(this);
        hurray.setOnLongClickListener(this);
        heart.setOnLongClickListener(this);
        laughReaction.setOnClickListener(this);
        sadReaction.setOnClickListener(this);
        thumbsDownReaction.setOnClickListener(this);
        thumbsUpReaction.setOnClickListener(this);
        hurrayReaction.setOnClickListener(this);
        heartReaction.setOnClickListener(this);
        laughReaction.setOnLongClickListener(this);
        sadReaction.setOnLongClickListener(this);
        thumbsDownReaction.setOnLongClickListener(this);
        thumbsUpReaction.setOnLongClickListener(this);
        hurrayReaction.setOnLongClickListener(this);
        heartReaction.setOnLongClickListener(this);
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
            if (commentsModel.getAuthorAssociation() != null && !"none".equalsIgnoreCase(commentsModel.getAuthorAssociation())) {
                owner.setText(commentsModel.getAuthorAssociation().toLowerCase());
                owner.setVisibility(View.VISIBLE);
            } else {
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
                        owner.setText("");
                        owner.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            avatar.setUrl(null, null, false, false);
            name.setText("");
        }
        if (!InputHelper.isEmpty(commentsModel.getPath()) && commentsModel.getPosition() > 0) {
            pathText.setVisibility(View.VISIBLE);
            pathText.setText(String.format("Commented on %s#L%s", commentsModel.getPath(),
                    commentsModel.getLine() > 0 ? commentsModel.getLine() : commentsModel.getPosition()));
        } else {
            pathText.setText("");
            pathText.setVisibility(View.GONE);
        }
        if (!InputHelper.isEmpty(commentsModel.getBodyHtml())) {
            String body = commentsModel.getBodyHtml();
            int width = adapter != null ? adapter.getRowWidth() : 0;
            HtmlHelper.htmlIntoTextView(comment, body, width > 0 ? width : viewGroup.getWidth());
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
                    case R.id.heartReaction:
                        reactionsModel.setHeart(!isReacted ? reactionsModel.getHeart() + 1 : reactionsModel.getHeart() - 1);
                        break;
                    case R.id.sad:
                    case R.id.sadReaction:
                        reactionsModel.setConfused(!isReacted ? reactionsModel.getConfused() + 1 : reactionsModel.getConfused() - 1);
                        break;
                    case R.id.thumbsDown:
                    case R.id.thumbsDownReaction:
                        reactionsModel.setMinusOne(!isReacted ? reactionsModel.getMinusOne() + 1 : reactionsModel.getMinusOne() - 1);
                        break;
                    case R.id.thumbsUp:
                    case R.id.thumbsUpReaction:
                        reactionsModel.setPlusOne(!isReacted ? reactionsModel.getPlusOne() + 1 : reactionsModel.getPlusOne() - 1);
                        break;
                    case R.id.laugh:
                    case R.id.laughReaction:
                        reactionsModel.setLaugh(!isReacted ? reactionsModel.getLaugh() + 1 : reactionsModel.getLaugh() - 1);
                        break;
                    case R.id.hurray:
                    case R.id.hurrayReaction:
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
        CommentsHelper.appendEmojies(reaction, thumbsUp, thumbsUpReaction, thumbsDown, thumbsDownReaction, hurray, hurrayReaction, sad,
                sadReaction, laugh, laughReaction, heart, heartReaction, reactionsList);
    }

    private void onToggle(boolean expanded, boolean animate) {
        if (animate) {
            TransitionManager.beginDelayedTransition(viewGroup, new ChangeBounds());
        }
        toggle.setRotation(!expanded ? 0.0F : 180F);
        commentOptions.setVisibility(!expanded ? View.GONE : View.VISIBLE);
        reactionsList.setVisibility(expanded ? View.GONE : View.VISIBLE);
        reactionsList.setVisibility(expanded ? View.GONE : reactionsList.getTag() == null || (!((Boolean) reactionsList.getTag()))
                                                           ? View.GONE : View.VISIBLE);
    }

    @Override protected void onViewIsDetaching() {
        DrawableGetter drawableGetter = (DrawableGetter) comment.getTag(R.id.drawable_callback);
        if (drawableGetter != null) {
            drawableGetter.clear(drawableGetter);
        }
    }
}
