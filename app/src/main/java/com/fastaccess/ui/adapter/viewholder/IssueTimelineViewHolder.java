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

    private IssueTimelineViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    public static IssueTimelineViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter) {
        return new IssueTimelineViewHolder(getView(viewGroup, R.layout.issue_timeline_row_item), adapter);
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
            stateImage.setContentDescription(event.name());
            stateImage.setImageResource(event.getIconResId());
        }
        if (issueEventModel.getLabels() == null) {
            if (event != null) {
                stateText.setText(TimelineProvider.getStyledEvents(issueEventModel, itemView.getContext()));
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
