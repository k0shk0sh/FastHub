package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.GroupedReviewModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.adapter.ReviewCommentsAdapter;
import com.fastaccess.ui.adapter.callback.OnToggleView;
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

public class GroupedReviewsViewHolder extends BaseViewHolder<TimelineModel> {

    @BindView(R.id.stateImage) ForegroundImageView stateImage;
    @BindView(R.id.nestedRecyclerView) DynamicRecyclerView nestedRecyclerView;
    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.toggle) ForegroundImageView toggle;
    @BindView(R.id.patch) FontTextView patch;
    private final int patchAdditionColor;
    private final int patchDeletionColor;
    private final int patchRefColor;
    private OnToggleView onToggleView;
    private String pathText;

    @Override public void onClick(View v) {
        int position = getAdapterPosition();
        onToggleView.onToggle(position, !onToggleView.isCollapsed(position));
        onToggle(onToggleView.isCollapsed(position));
    }

    private GroupedReviewsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter, @Nullable OnToggleView onToggleView) {
        super(itemView, adapter);
        this.onToggleView = onToggleView;
        patchAdditionColor = ViewHelper.getPatchAdditionColor(itemView.getContext());
        patchDeletionColor = ViewHelper.getPatchDeletionColor(itemView.getContext());
        patchRefColor = ViewHelper.getPatchRefColor(itemView.getContext());
        this.onToggleView = onToggleView;
        nestedRecyclerView.setNestedScrollingEnabled(false);
    }

    public static GroupedReviewsViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter, @Nullable OnToggleView onToggleView) {
        return new GroupedReviewsViewHolder(getView(viewGroup, R.layout.grouped_review_timeline_row_item), adapter, onToggleView);
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
            nestedRecyclerView.setAdapter(new ReviewCommentsAdapter(groupedReviewModel.getComments()));
            nestedRecyclerView.addDivider();
        }
        onToggle(onToggleView.isCollapsed(getAdapterPosition()));
    }

    private void onToggle(boolean expanded) {
        if (!expanded) {
            patch.setText(".....");
            name.setMaxLines(2);
            toggle.setRotation(0.0f);
        } else {
            name.setMaxLines(5);
            setPatchText(pathText);
            toggle.setRotation(180f);
        }
    }

    private void setPatchText(@NonNull String text) {
        patch.setText(DiffLineSpan.getSpannable(text, patchAdditionColor, patchDeletionColor, patchRefColor));
    }
}
