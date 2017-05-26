package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fastaccess.R;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.ReactionsModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.provider.timeline.HtmlHelper;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.adapter.callback.ReactionsCallback;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

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
    @BindView(R.id.reactionsText) TextView reactionsText;
    private OnToggleView onToggleView;
    private ReactionsCallback reactionsCallback;
    private ViewGroup viewGroup;

    private IssueDetailsViewHolder(@NonNull View itemView, @NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter,
                                   @NonNull OnToggleView onToggleView, @NonNull ReactionsCallback reactionsCallback) {
        super(itemView, adapter);
        this.onToggleView = onToggleView;
        this.viewGroup = viewGroup;
        this.reactionsCallback = reactionsCallback;
        itemView.setOnClickListener(null);
        itemView.setOnLongClickListener(null);
        commentMenu.setOnClickListener(this);
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
    }

    public static IssueDetailsViewHolder newInstance(@NonNull ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter,
                                                     @NonNull OnToggleView onToggleView, @NonNull ReactionsCallback reactionsCallback) {
        return new IssueDetailsViewHolder(getView(viewGroup, R.layout.issue_detail_header_row_item), viewGroup,
                adapter, onToggleView, reactionsCallback);
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
            Logger.e(timelineModel);
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
        avatar.setUrl(issueModel.getUser().getAvatarUrl(), issueModel.getUser().getLogin());
        name.setText(issueModel.getUser().getLogin());
        date.setText(ParseDateFormat.getTimeAgo(issueModel.getCreatedAt()));
        if (!InputHelper.isEmpty(issueModel.getBodyHtml())) {
            HtmlHelper.htmlIntoTextView(comment, issueModel.getBodyHtml());
        } else {
            comment.setText(R.string.no_description_provided);
        }
        if (issueModel.getReactions() != null) {
            appendEmojies(issueModel.getReactions());
        }
    }

    private void bind(@NonNull PullRequest pullRequest) {
        avatar.setUrl(pullRequest.getUser().getAvatarUrl(), pullRequest.getUser().getLogin());
        name.setText(pullRequest.getUser().getLogin());
        date.setText(ParseDateFormat.getTimeAgo(pullRequest.getCreatedAt()));
        if (!InputHelper.isEmpty(pullRequest.getBodyHtml())) {
            HtmlHelper.htmlIntoTextView(comment, pullRequest.getBodyHtml());
        } else {
            comment.setText(R.string.no_description_provided);
        }
        if (pullRequest.getReactions() != null) {
            appendEmojies(pullRequest.getReactions());
        }
    }

    private void appendEmojies(@NonNull ReactionsModel reaction) {
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
}
