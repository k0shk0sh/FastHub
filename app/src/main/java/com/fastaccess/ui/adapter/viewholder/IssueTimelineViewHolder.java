package com.fastaccess.ui.adapter.viewholder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.IssueEvent;
import com.fastaccess.data.dao.types.IssueEventType;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.RoundBackgroundSpan;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindString;
import butterknife.BindView;

/**
 * Created by Kosh on 13 Dec 2016, 1:42 AM
 */

public class IssueTimelineViewHolder extends BaseViewHolder<TimelineModel> {

    @BindView(R.id.stateImage) ForegroundImageView stateImage;
    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.stateText) FontTextView stateText;
    @BindString(R.string.to) String to;

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
            SpannableBuilder spannableBuilder = SpannableBuilder.builder();
            if (issueEventModel.getAssignee() != null && issueEventModel.getAssigner() != null) {
                spannableBuilder.bold(issueEventModel.getAssigner().getLogin());
            } else {
                if (issueEventModel.getActor() != null) {
                    spannableBuilder.bold(issueEventModel.getActor().getLogin());
                }
            }
            if (event != null) {
                spannableBuilder
                        .append(" ")
                        .append(event.name().replaceAll("_", " "));
                if (event == IssueEventType.labeled || event == IssueEventType.unlabeled) {
                    LabelModel labelModel = issueEventModel.getLabel();
                    int color = Color.parseColor("#" + labelModel.getColor());
                    spannableBuilder
                            .append(" ")
                            .append(" " + labelModel.getName() + " ", new RoundBackgroundSpan(color));
                } else if (event == IssueEventType.assigned || event == IssueEventType.unassigned) {
                    spannableBuilder
                            .append(" ")
                            .bold(issueEventModel.getAssignee().getLogin());
                } else if (event == IssueEventType.milestoned || event == IssueEventType.demilestoned) {
                    spannableBuilder
                            .append(" ")
                            .append(to)
                            .append(" ")
                            .bold(issueEventModel.getMilestone().getTitle());
                } else if (event == IssueEventType.renamed) {
                    spannableBuilder
                            .append(" ")
                            .bold(issueEventModel.getRename().getFromValue())
                            .append(" ")
                            .append(to)
                            .append(" ")
                            .bold(issueEventModel.getRename().getToValue());
                } else if (event == IssueEventType.referenced || event == IssueEventType.merged) {
                    spannableBuilder
                            .append(" ")
                            .url(stateText.getResources().getString(R.string.this_value));
                }
            } else {
                stateImage.setImageResource(R.drawable.ic_label);
            }
            stateText.setText(spannableBuilder
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo(issueEventModel.getCreatedAt())));
        } else {
            stateText.setText(issueEventModel.getLabels());
        }
        itemView.setEnabled(!InputHelper.isEmpty(issueEventModel.getCommitUrl()));
    }

}
