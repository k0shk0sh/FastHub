package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fastaccess.R;
import com.fastaccess.data.dao.ReactionsModel;
import com.fastaccess.data.dao.ReviewCommentModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.provider.timeline.HtmlHelper;
import com.fastaccess.provider.timeline.handler.drawable.DrawableGetter;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.adapter.callback.ReactionsCallback;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

public class ReviewCommentsViewHolder extends BaseViewHolder<ReviewCommentModel> {

    @BindView(R.id.avatarView) AvatarLayout avatarView;
    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.comment) FontTextView comment;
    @BindView(R.id.toggle) AppCompatImageView toggle;
    @BindView(R.id.toggleHolder) LinearLayout toggleHolder;
    @BindView(R.id.thumbsUp) FontTextView thumbsUp;
    @BindView(R.id.thumbsDown) FontTextView thumbsDown;
    @BindView(R.id.laugh) FontTextView laugh;
    @BindView(R.id.hurray) FontTextView hurray;
    @BindView(R.id.sad) FontTextView sad;
    @BindView(R.id.heart) FontTextView heart;
    @BindView(R.id.commentMenu) ImageView commentMenu;
    @BindView(R.id.commentOptions) RelativeLayout commentOptions;
    @BindView(R.id.reactionsText) FontTextView reactionsText;
    @BindView(R.id.owner) FontTextView owner;
    private OnToggleView onToggleView;
    private ReactionsCallback reactionsCallback;
    private ViewGroup viewGroup;
    private String repoOwner;
    private String poster;

    @Override public void onClick(View v) {
        if (v.getId() == R.id.toggle || v.getId() == R.id.toggleHolder) {
            if (onToggleView != null) {
                long id = getId();
                onToggleView.onToggle(id, !onToggleView.isCollapsed(id));
                onToggle(onToggleView.isCollapsed(id), true);
            }
        } else {
            addReactionCount(v);
            super.onClick(v);
        }
    }

    private ReviewCommentsViewHolder(@NonNull View itemView, ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter,
                                     @NonNull OnToggleView onToggleView, @NonNull ReactionsCallback reactionsCallback,
                                     String repoOwner, String poster) {
        super(itemView, adapter);
        this.onToggleView = onToggleView;
        this.viewGroup = viewGroup;
        this.reactionsCallback = reactionsCallback;
        this.repoOwner = repoOwner;
        this.poster = poster;
        itemView.setOnClickListener(null);
        itemView.setOnLongClickListener(null);
        toggle.setOnClickListener(this);
        commentMenu.setOnClickListener(this);
        toggleHolder.setOnClickListener(this);
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

    public static ReviewCommentsViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter,
                                                       @NonNull OnToggleView onToggleView, @NonNull ReactionsCallback reactionsCallback,
                                                       String repoOwner, String poster) {
        return new ReviewCommentsViewHolder(getView(viewGroup, R.layout.review_comments_row_item),
                viewGroup, adapter, onToggleView, reactionsCallback, repoOwner, poster);
    }

    @Override public void bind(@NonNull ReviewCommentModel commentModel) {
        if (commentModel.getUser() != null) {
            avatarView.setUrl(commentModel.getUser().getAvatarUrl(), commentModel.getUser().getLogin(), commentModel.getUser()
                    .isOrganizationType(), LinkParserHelper.isEnterprise(commentModel.getHtmlUrl()));
            name.setText(commentModel.getUser().getLogin());
            boolean isRepoOwner = TextUtils.equals(commentModel.getUser().getLogin(), repoOwner);
            if (isRepoOwner) {
                owner.setVisibility(View.VISIBLE);
                owner.setText(R.string.owner);
            } else {
                boolean isPoster = TextUtils.equals(commentModel.getUser().getLogin(), poster);
                if (isPoster) {
                    owner.setVisibility(View.VISIBLE);
                    owner.setText(R.string.original_poster);
                } else {
                    owner.setText(null);
                    owner.setVisibility(View.GONE);
                }
            }
        }
        date.setText(ParseDateFormat.getTimeAgo(commentModel.getCreatedAt()));
        if (!InputHelper.isEmpty(commentModel.getBodyHtml())) {
            HtmlHelper.htmlIntoTextView(comment, commentModel.getBodyHtml(), viewGroup.getWidth());
        } else {
            comment.setText("");
        }
        if (commentModel.getReactions() != null) {
            ReactionsModel reaction = commentModel.getReactions();
            appendEmojies(reaction);
        }
        if (onToggleView != null) onToggle(onToggleView.isCollapsed(getId()), false);
    }

    private void addReactionCount(View v) {
        if (adapter != null) {
            ReviewCommentModel comment = (ReviewCommentModel) adapter.getItem(getAdapterPosition());
            if (comment != null) {
                boolean isReacted = reactionsCallback == null || reactionsCallback.isPreviouslyReacted(comment.getId(), v.getId());
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
            if (!onToggleView.isCollapsed(getId())) {
                reactionsText.setVisibility(View.VISIBLE);
            }
        } else {
            reactionsText.setVisibility(View.GONE);
        }
    }

    private long getId() {
        if (adapter != null) {
            ReviewCommentModel comment = (ReviewCommentModel) adapter.getItem(getAdapterPosition());
            return comment.getId();
        }
        return -1;
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