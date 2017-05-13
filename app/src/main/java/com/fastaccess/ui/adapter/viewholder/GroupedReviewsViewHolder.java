package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.GroupedReviewModel;
import com.fastaccess.data.dao.ReviewCommentModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.ReviewCommentsAdapter;
import com.fastaccess.ui.adapter.callback.OnToggleView;
import com.fastaccess.ui.adapter.callback.ReactionsCallback;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline.PullRequestTimelineMvp;
import com.fastaccess.ui.widgets.DiffLineSpan;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;

/**
 * Created by Kosh on 13 Dec 2016, 1:42 AM
 */

public class GroupedReviewsViewHolder extends BaseViewHolder<TimelineModel> implements BaseViewHolder.OnItemClickListener<ReviewCommentModel> {

    @BindView(R.id.stateImage) ForegroundImageView stateImage;
    @BindView(R.id.nestedRecyclerView) DynamicRecyclerView nestedRecyclerView;
    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.toggle) ForegroundImageView toggle;
    @BindView(R.id.patch) FontTextView patch;
    @BindView(R.id.minimized) View minimized;
    private final int patchAdditionColor;
    private final int patchDeletionColor;
    private final int patchRefColor;
    private OnToggleView onToggleView;
    private ReactionsCallback reactionsCallback;
    private String pathText;
    private PullRequestTimelineMvp.ReviewCommentCallback reviewCommentCallback;
    private ViewGroup viewGroup;

    @Override public void onClick(View v) {
        int position = getAdapterPosition();
        onToggleView.onToggle(position, !onToggleView.isCollapsed(position));
        onToggle(onToggleView.isCollapsed(position), true);
    }

    private GroupedReviewsViewHolder(@NonNull View itemView, ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter,
                                     @NonNull OnToggleView onToggleView,
                                     @NonNull ReactionsCallback reactionsCallback,
                                     @NonNull PullRequestTimelineMvp.ReviewCommentCallback reviewCommentCallback) {
        super(itemView, adapter);
        this.onToggleView = onToggleView;
        this.viewGroup = viewGroup;
        this.reactionsCallback = reactionsCallback;
        this.reviewCommentCallback = reviewCommentCallback;
        patchAdditionColor = ViewHelper.getPatchAdditionColor(itemView.getContext());
        patchDeletionColor = ViewHelper.getPatchDeletionColor(itemView.getContext());
        patchRefColor = ViewHelper.getPatchRefColor(itemView.getContext());
        this.onToggleView = onToggleView;
        nestedRecyclerView.setNestedScrollingEnabled(false);
    }

    public static GroupedReviewsViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter,
                                                       @NonNull OnToggleView onToggleView,
                                                       @NonNull ReactionsCallback reactionsCallback,
                                                       @NonNull PullRequestTimelineMvp.ReviewCommentCallback reviewCommentCallback) {
        return new GroupedReviewsViewHolder(getView(viewGroup, R.layout.grouped_review_timeline_row_item), viewGroup, adapter,
                onToggleView, reactionsCallback, reviewCommentCallback);
    }

    @Override public void bind(@NonNull TimelineModel model) {
        GroupedReviewModel groupedReviewModel = model.getGroupedReview();
        this.pathText = groupedReviewModel.getDiffText();
        name.setText(groupedReviewModel.getPath());
        stateImage.setImageResource(R.drawable.ic_eye);
        if (groupedReviewModel.getComments() == null || groupedReviewModel.getComments().isEmpty()) {
            nestedRecyclerView.setVisibility(View.GONE);
            nestedRecyclerView.setAdapter(null);
        } else {
            nestedRecyclerView.setVisibility(View.VISIBLE);
            nestedRecyclerView.setAdapter(new ReviewCommentsAdapter(groupedReviewModel.getComments(), this, onToggleView, reactionsCallback));
            nestedRecyclerView.addDivider();
        }
        onToggle(onToggleView.isCollapsed(getAdapterPosition()), false);
    }

    @Override public void onItemClick(int position, View v, ReviewCommentModel item) {
        if (reviewCommentCallback != null) {
            reviewCommentCallback.onClick(getAdapterPosition(), position, v, item);
        }
    }

    @Override public void onItemLongClick(int position, View v, ReviewCommentModel item) {
        if (reviewCommentCallback != null) {
            reviewCommentCallback.onLongClick(getAdapterPosition(), position, v, item);
        }
    }

    private void onToggle(boolean expanded, boolean animate) {
        if (animate) {
            TransitionManager.beginDelayedTransition(viewGroup, new ChangeBounds());
        }
        if (!expanded) {
            minimized.setVisibility(View.GONE);
            patch.setText("");
            name.setMaxLines(2);
            toggle.setRotation(0.0f);
        } else {
            minimized.setVisibility(View.VISIBLE);
            name.setMaxLines(5);
            setPatchText(pathText);
            toggle.setRotation(180f);
        }
    }

    private void setPatchText(@NonNull String text) {
        patch.setText(DiffLineSpan.getSpannable(text, patchAdditionColor, patchDeletionColor, patchRefColor, true));
    }
}
