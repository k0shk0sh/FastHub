package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.IssueEvent;
import com.fastaccess.data.dao.types.IssueEventType;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.timeline.TimelineProvider;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 13 Dec 2016, 1:42 AM
 */

public class IssueTimelineViewHolder extends BaseViewHolder<TimelineModel> {

    @BindView(R.id.stateImage) ForegroundImageView stateImage;
    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.stateText) FontTextView stateText;
    private boolean isMerged;

    private IssueTimelineViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter, boolean isMerged) {
        super(itemView, adapter);
        this.isMerged = isMerged;
    }

    public static IssueTimelineViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter, boolean isMerged) {
        return new IssueTimelineViewHolder(getView(viewGroup, R.layout.issue_timeline_row_item), adapter, isMerged);
    }

    @Override public void bind(@NonNull TimelineModel timelineModel) {
        IssueEvent issueEventModel = timelineModel.getEvent();
        IssueEventType event = issueEventModel.getEvent();
        if (issueEventModel.getAssignee() != null && issueEventModel.getAssigner() != null) {
            avatarLayout.setUrl(issueEventModel.getAssigner().getAvatarUrl(), issueEventModel.getAssigner().getLogin());
        } else {
            if (issueEventModel.getActor() != null) {
                avatarLayout.setUrl(issueEventModel.getActor().getAvatarUrl(), issueEventModel.getActor().getLogin());
            }
        }
        if (event != null) {
            if (isMerged && event == IssueEventType.closed) {
                stateImage.setContentDescription(IssueEventType.merged.name());
                stateImage.setImageResource(IssueEventType.merged.getIconResId());
            } else {
                stateImage.setContentDescription(event.name());
                stateImage.setImageResource(event.getIconResId());
            }
        }
        if (issueEventModel.getLabels() == null) {
            if (event != null) {
                stateText.setText(TimelineProvider.getStyledEvents(issueEventModel, itemView.getContext(), isMerged));
            } else {
                stateText.setText("");
                stateImage.setImageResource(R.drawable.ic_label);
            }
        } else {
            stateText.setText(issueEventModel.getLabels());
        }
        itemView.setEnabled(!InputHelper.isEmpty(issueEventModel.getCommitUrl()));
    }

}
