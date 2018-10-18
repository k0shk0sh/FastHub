package com.fastaccess.ui.adapter.viewholder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fastaccess.R;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.ReactionsModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.provider.timeline.HtmlHelper;
import com.fastaccess.provider.timeline.handler.drawable.DrawableGetter;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.adapter.callback.ReactionsCallback;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.LabelSpan;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 13 Dec 2016, 1:03 AM
 */

public class IssueDetailsViewHolder extends BaseViewHolder<TimelineModel> {

    @BindView(R.id.avatarView) AvatarLayout avatar;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.comment) FontTextView comment;
    @BindView(R.id.thumbsUp) FontTextView thumbsUp;
    @BindView(R.id.thumbsDown) FontTextView thumbsDown;
    @BindView(R.id.laugh) FontTextView laugh;
    @BindView(R.id.sad) FontTextView sad;
    @BindView(R.id.hurray) FontTextView hooray;
    @BindView(R.id.heart) FontTextView heart;
    @BindView(R.id.toggle) View toggle;
    @BindView(R.id.commentMenu) View commentMenu;
    @BindView(R.id.commentOptions) View commentOptions;
    @BindView(R.id.toggleHolder) View toggleHolder;
    @BindView(R.id.emojiesList) View emojiesList;
    @BindView(R.id.owner) TextView owner;
    @BindView(R.id.labels) TextView labels;
    @BindView(R.id.labelsHolder) View labelsHolder;
    @BindView(R.id.reactionsList) View reactionsList;
    @BindView(R.id.thumbsUpReaction) FontTextView thumbsUpReaction;
    @BindView(R.id.thumbsDownReaction) FontTextView thumbsDownReaction;
    @BindView(R.id.laughReaction) FontTextView laughReaction;
    @BindView(R.id.hurrayReaction) FontTextView hurrayReaction;
    @BindView(R.id.sadReaction) FontTextView sadReaction;
    @BindView(R.id.heartReaction) FontTextView heartReaction;
    private OnToggleView onToggleView;
    private ReactionsCallback reactionsCallback;
    private ViewGroup viewGroup;
    private String repoOwner;
    private String poster;

    private IssueDetailsViewHolder(@NonNull View itemView, @NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter,
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
        commentMenu.setOnClickListener(this);
        toggle.setOnClickListener(this);
        toggleHolder.setOnClickListener(this);
        laugh.setOnClickListener(this);
        sad.setOnClickListener(this);
        thumbsDown.setOnClickListener(this);
        thumbsUp.setOnClickListener(this);
        hooray.setOnClickListener(this);
        laugh.setOnLongClickListener(this);
        sad.setOnLongClickListener(this);
        thumbsDown.setOnLongClickListener(this);
        thumbsUp.setOnLongClickListener(this);
        hooray.setOnLongClickListener(this);
        heart.setOnLongClickListener(this);
        heart.setOnClickListener(this);
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

    public static IssueDetailsViewHolder newInstance(@NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter,
                                                     @NonNull OnToggleView onToggleView, @NonNull ReactionsCallback reactionsCallback,
                                                     @NonNull String repoOwner, @NonNull String poster) {
        return new IssueDetailsViewHolder(getView(viewGroup, R.layout.issue_detail_header_row_item), viewGroup,
                adapter, onToggleView, reactionsCallback, repoOwner, poster);
    }

    @Override public void bind(@NonNull TimelineModel timelineModel) {
        if (timelineModel.getIssue() != null) {
            bind(timelineModel.getIssue());
        } else if (timelineModel.getPullRequest() != null) {
            bind(timelineModel.getPullRequest());
        }
        if (onToggleView != null) onToggle(onToggleView.isCollapsed(getAdapterPosition()), false);
    }

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

    private void addReactionCount(View v) {
        if (adapter != null) {
            TimelineModel timelineModel = (TimelineModel) adapter.getItem(getAdapterPosition());
            if (timelineModel == null) return;
            ReactionsModel reactionsModel = null;
            PullRequest pullRequest = timelineModel.getPullRequest();
            Issue issue = timelineModel.getIssue();
            int number = 0;
            if (pullRequest != null) {
                reactionsModel = pullRequest.getReactions();
                number = pullRequest.getNumber();
            } else if (issue != null) {
                reactionsModel = issue.getReactions();
                number = issue.getNumber();
            }
            if (reactionsModel == null) reactionsModel = new ReactionsModel();
            boolean isReacted = reactionsCallback == null || reactionsCallback.isPreviouslyReacted(number, v.getId());
            boolean isCallingApi = reactionsCallback != null && reactionsCallback.isCallingApi(number, v.getId());
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
            if (pullRequest != null) {
                pullRequest.setReactions(reactionsModel);
                appendEmojies(reactionsModel);
                timelineModel.setPullRequest(pullRequest);
            } else if (issue != null) {
                issue.setReactions(reactionsModel);
                appendEmojies(reactionsModel);
                timelineModel.setIssue(issue);
            }
        }
    }

    private void bind(@NonNull Issue issueModel) {
        setup(issueModel.getUser(), issueModel.getBodyHtml(), issueModel.getReactions());
        setupDate(issueModel.getCreatedAt(), issueModel.getUpdatedAt());
        setupLabels(issueModel.getLabels());
    }

    private void bind(@NonNull PullRequest pullRequest) {
        setup(pullRequest.getUser(), pullRequest.getBodyHtml(), pullRequest.getReactions());
        setupDate(pullRequest.getCreatedAt(), pullRequest.getUpdatedAt());
        setupLabels(pullRequest.getLabels());
    }

    private void setup(User user, String description, ReactionsModel reactionsModel) {
        avatar.setUrl(user.getAvatarUrl(), user.getLogin(), user.isOrganizationType(), LinkParserHelper.isEnterprise(user.getHtmlUrl()));
        name.setText(user.getLogin());
        boolean isOwner = TextUtils.equals(repoOwner, user.getLogin());
        if (isOwner) {
            owner.setVisibility(View.VISIBLE);
            owner.setText(R.string.owner);
        } else {
            owner.setText("");
            owner.setVisibility(View.GONE);
        }
        if (reactionsModel != null) {
            appendEmojies(reactionsModel);
        }
        if (!InputHelper.isEmpty(description)) {
            HtmlHelper.htmlIntoTextView(comment, description, viewGroup.getWidth() - ViewHelper.dpToPx(itemView.getContext(), 24));
        } else {
            comment.setText(R.string.no_description_provided);
        }
    }

    private void setupDate(@NonNull Date createdDate, @NonNull Date updated) {
        date.setText(ParseDateFormat.getTimeAgo(createdDate));
    }

    private void setupLabels(@Nullable List<LabelModel> labelList) {
        if (labelList != null && !labelList.isEmpty()) {
            SpannableBuilder builder = SpannableBuilder.builder();
            for (LabelModel labelModel : labelList) {
                int color = Color.parseColor("#" + labelModel.getColor());
                builder.append(" ").append(" " + labelModel.getName() + " ", new LabelSpan(color));
            }
            labels.setText(builder);
            labelsHolder.setVisibility(View.VISIBLE);
        } else {
            labels.setText("");
            labelsHolder.setVisibility(View.GONE);
        }
    }

    private void appendEmojies(ReactionsModel reaction) {
        CommentsHelper.appendEmojies(reaction, thumbsUp, thumbsUpReaction, thumbsDown, thumbsDownReaction, hooray, hurrayReaction, sad,
                sadReaction, laugh, laughReaction, heart, heartReaction, reactionsList);
    }

    private void onToggle(boolean expanded, boolean animate) {
        if (animate) {
            TransitionManager.beginDelayedTransition(viewGroup, new ChangeBounds());
        }
        toggle.setRotation(!expanded ? 0.0F : 180F);
        commentOptions.setVisibility(!expanded ? View.GONE : View.VISIBLE);
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
