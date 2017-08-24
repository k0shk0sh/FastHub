package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
    @BindView(R.id.addCommentPreview) View addCommentPreview;
    @BindView(R.id.toggleHolder) LinearLayout toggleHolder;
    @BindView(R.id.bottomToggle) View bottomToggle;

    private final int patchAdditionColor;
    private final int patchDeletionColor;
    private final int patchRefColor;
    private OnToggleView onToggleView;
    private ReactionsCallback reactionsCallback;
    private String pathText;
    private PullRequestTimelineMvp.ReviewCommentCallback reviewCommentCallback;
    private ViewGroup viewGroup;
    private String repoOwner;
    private String poster;

    @Override public void onClick(View v) {
        if (v.getId() == R.id.toggle || v.getId() == R.id.toggleHolder || v.getId() == R.id.bottomToggle) {
            long position = getId();
            onToggleView.onToggle(position, !onToggleView.isCollapsed(position));
            onToggle(onToggleView.isCollapsed(position), true);
        } else {
            super.onClick(v);
        }
    }

    private GroupedReviewsViewHolder(@NonNull View itemView, ViewGroup viewGroup, @Nullable BaseRecyclerAdapter adapter,
                                     @NonNull OnToggleView onToggleView,
                                     @NonNull ReactionsCallback reactionsCallback,
                                     @NonNull PullRequestTimelineMvp.ReviewCommentCallback reviewCommentCallback,
                                     String repoOwner, String poster) {
        super(itemView, adapter);
        this.onToggleView = onToggleView;
        this.viewGroup = viewGroup;
        this.reactionsCallback = reactionsCallback;
        this.reviewCommentCallback = reviewCommentCallback;
        patchAdditionColor = ViewHelper.getPatchAdditionColor(itemView.getContext());
        patchDeletionColor = ViewHelper.getPatchDeletionColor(itemView.getContext());
        patchRefColor = ViewHelper.getPatchRefColor(itemView.getContext());
        this.onToggleView = onToggleView;
        this.repoOwner = repoOwner;
        this.poster = poster;
        bottomToggle.setOnClickListener(this);
        nestedRecyclerView.setNestedScrollingEnabled(false);
        addCommentPreview.setOnClickListener(this);
        toggle.setOnClickListener(this);
        toggleHolder.setOnClickListener(this);
        itemView.setOnClickListener(null);
        itemView.setOnLongClickListener(null);
    }

    public static GroupedReviewsViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter,
                                                       @NonNull OnToggleView onToggleView,
                                                       @NonNull ReactionsCallback reactionsCallback,
                                                       @NonNull PullRequestTimelineMvp.ReviewCommentCallback reviewCommentCallback,
                                                       String repoOwner, String poster) {
        return new GroupedReviewsViewHolder(getView(viewGroup, R.layout.grouped_review_timeline_row_item), viewGroup, adapter,
                onToggleView, reactionsCallback, reviewCommentCallback, repoOwner, poster);
    }

    @Override public void bind(@NonNull TimelineModel model) {
        GroupedReviewModel groupedReviewModel = model.getGroupedReviewModel();
        this.pathText = groupedReviewModel.getDiffText();
        name.setText(groupedReviewModel.getPath());
        stateImage.setImageResource(R.drawable.ic_eye);
        if (groupedReviewModel.getComments() == null || groupedReviewModel.getComments().isEmpty()) {
            nestedRecyclerView.setVisibility(View.GONE);
            nestedRecyclerView.setAdapter(null);
        } else {
            nestedRecyclerView.setVisibility(View.VISIBLE);
            nestedRecyclerView.setAdapter(new ReviewCommentsAdapter(groupedReviewModel.getComments(), this,
                    onToggleView, reactionsCallback, repoOwner, poster));
            nestedRecyclerView.addDivider();
        }
        onToggle(onToggleView.isCollapsed(getId()), false);
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

    private long getId() {
        return getAdapterPosition();
    }

    private void setPatchText(@NonNull String text) {
        patch.setText(DiffLineSpan.getSpannable(text, patchAdditionColor, patchDeletionColor, patchRefColor, true));
    }
}