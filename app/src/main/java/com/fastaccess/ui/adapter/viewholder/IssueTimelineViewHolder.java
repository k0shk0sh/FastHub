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
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.provider.timeline.TimelineProvider;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.LabelSpan;
import com.fastaccess.ui.widgets.SpannableBuilder;
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
            avatarLayout.setUrl(issueEventModel.getAssigner().getAvatarUrl(), issueEventModel.getAssigner().getLogin(),
                    false, LinkParserHelper.isEnterprise(issueEventModel.getUrl()));
        } else {
            if (issueEventModel.getActor() != null) {
                avatarLayout.setUrl(issueEventModel.getActor().getAvatarUrl(), issueEventModel.getActor().getLogin(),
                        false, LinkParserHelper.isEnterprise(issueEventModel.getUrl()));
            }
        }
        if (event != null) {
            stateImage.setContentDescription(event.name());
            stateImage.setImageResource(event.getIconResId());
        }
        if (issueEventModel.getLabels() == null || issueEventModel.getLabels().isEmpty()) {
            if (event != null) {
                stateText.setText(TimelineProvider.getStyledEvents(issueEventModel, itemView.getContext(), isMerged));
            } else {
                stateText.setText("");
                stateImage.setImageResource(R.drawable.ic_label);
            }
        } else {
            if (event != null) {
                SpannableBuilder spannableBuilder = SpannableBuilder.builder();
                if (issueEventModel.getAssignee() != null && issueEventModel.getAssigner() != null) {
                    spannableBuilder.bold(issueEventModel.getAssigner().getLogin(), new LabelSpan(Color.TRANSPARENT));
                } else if (issueEventModel.getActor() != null) {
                    spannableBuilder.bold(issueEventModel.getActor().getLogin(), new LabelSpan(Color.TRANSPARENT));
                }
                spannableBuilder.append(" ").append(event.name().replaceAll("_", " "), new LabelSpan(Color.TRANSPARENT));
                for (LabelModel labelModel : issueEventModel.getLabels()) {
                    TimelineProvider.appendLabels(labelModel, spannableBuilder);
                }
                spannableBuilder.append(" ").append(ParseDateFormat.getTimeAgo(issueEventModel.getCreatedAt()), new LabelSpan(Color.TRANSPARENT));
                stateText.setText(spannableBuilder);
                stateImage.setImageResource(R.drawable.ic_label);
            }
        }
        itemView.setEnabled(!InputHelper.isEmpty(issueEventModel.getCommitUrl()));
    }

}
