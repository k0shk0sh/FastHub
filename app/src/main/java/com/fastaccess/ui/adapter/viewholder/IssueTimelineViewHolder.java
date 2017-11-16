package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.timeline.GenericEvent;
import com.fastaccess.data.dao.types.IssueEventType;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.provider.timeline.TimelineProvider;
import com.fastaccess.provider.timeline.handler.drawable.DrawableGetter;
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
        GenericEvent issueEventModel = timelineModel.getGenericEvent();
        IssueEventType event = issueEventModel.getEvent();
        if (issueEventModel.getAssignee() != null && issueEventModel.getAssigner() != null) {
            avatarLayout.setUrl(issueEventModel.getAssigner().getAvatarUrl(), issueEventModel.getAssigner().getLogin(),
                    false, LinkParserHelper.isEnterprise(issueEventModel.getUrl()));
        } else {
            if (event != IssueEventType.committed) {
                avatarLayout.setVisibility(View.VISIBLE);
                if (issueEventModel.getActor() != null) {
                    avatarLayout.setUrl(issueEventModel.getActor().getAvatarUrl(), issueEventModel.getActor().getLogin(),
                            false, LinkParserHelper.isEnterprise(issueEventModel.getUrl()));
                } else if (issueEventModel.getAuthor() != null) {
                    avatarLayout.setUrl(issueEventModel.getAuthor().getAvatarUrl(), issueEventModel.getAuthor().getLogin(),
                            false, LinkParserHelper.isEnterprise(issueEventModel.getUrl()));
                }
            } else {
                avatarLayout.setVisibility(View.GONE);
            }
        }
        if (event != null) {
            stateImage.setContentDescription(event.name());
            stateImage.setImageResource(event.getIconResId());
        }
        if (event != null) {
            stateText.setText(TimelineProvider.getStyledEvents(issueEventModel, itemView.getContext(), isMerged));
        } else {
            stateText.setText("");
            stateImage.setImageResource(R.drawable.ic_label);
        }
    }

    @Override protected void onViewIsDetaching() {
        DrawableGetter drawableGetter = (DrawableGetter) stateText.getTag(R.id.drawable_callback);
        if (drawableGetter != null) {
            drawableGetter.clear(drawableGetter);
        }
    }

}
