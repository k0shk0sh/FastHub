package com.fastaccess.ui.adapter.viewholder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.IssueEventAdapterModel;
import com.fastaccess.data.dao.IssueEventModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.types.IssueEventType;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindString;
import butterknife.BindView;

/**
 * Created by Kosh on 13 Dec 2016, 1:42 AM
 */

public class IssueTimelineViewHolder extends BaseViewHolder<IssueEventAdapterModel> {

    @BindView(R.id.stateImage) ForegroundImageView stateImage;
    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.stateText) FontTextView stateText;
    @BindString(R.string.to) String to;

    private IssueTimelineViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
        itemView.setEnabled(false);
    }

    public static IssueTimelineViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter) {
        return new IssueTimelineViewHolder(getView(viewGroup, R.layout.issue_timeline_row_item), adapter);
    }

    @Override public void bind(@NonNull IssueEventAdapterModel model) {
        IssueEventModel issueEventModel = model.getIssueEvent();
        IssueEventType event = issueEventModel.getEvent();
        SpannableBuilder spannableBuilder = SpannableBuilder.builder()
                .bold(issueEventModel.getActor().getLogin());
        if (event != null) {
            stateImage.setContentDescription(event.name());
            spannableBuilder
                    .append(" ")
                    .append(event.name());
            stateImage.setImageResource(event.getIconResId());
            if (event == IssueEventType.labeled || event == IssueEventType.unlabeled) {
                LabelModel labelModel = issueEventModel.getLabel();
                spannableBuilder
                        .append(" ")
                        .background(labelModel.getName(), Color.parseColor("#" + labelModel.getColor()));
            } else if (event == IssueEventType.assigned || event == IssueEventType.unassigned) {
                spannableBuilder
                        .append(" ")
                        .bold(issueEventModel.getAssigner().getLogin());
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
                itemView.setEnabled(true);
                spannableBuilder
                        .append(" ")
                        .url(stateText.getResources().getString(R.string.this_value));
            } else if (event == IssueEventType.closed && issueEventModel.getCommitUrl() != null) {
                itemView.setEnabled(true);
            }
        } else {
            stateImage.setImageResource(R.drawable.ic_label);
        }
        avatarLayout.setUrl(issueEventModel.getActor().getAvatarUrl(), issueEventModel.getActor().getLogin());
        stateText.setText(spannableBuilder
                .append(" ")
                .append(ParseDateFormat.getTimeAgo(issueEventModel.getCreatedAt())));
    }


}
