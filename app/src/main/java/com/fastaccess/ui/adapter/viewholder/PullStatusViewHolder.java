package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.timeline.PullRequestTimelineModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindColor;
import butterknife.BindView;
import pr.PullRequestTimelineQuery;
import pr.type.StatusState;

/**
 * Created by Kosh on 10 Apr 2017, 3:40 AM
 */

public class PullStatusViewHolder extends BaseViewHolder<PullRequestTimelineModel> {

    @BindView(R.id.stateImage) ForegroundImageView stateImage;
    @BindView(R.id.status) FontTextView status;
    @BindView(R.id.statuses) FontTextView statuses;
    @BindColor(R.color.material_green_700) int green;
    @BindColor(R.color.material_red_700) int red;
    @BindColor(R.color.material_indigo_700) int indigo;

    private PullStatusViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(null);
        itemView.setOnLongClickListener(null);
    }

    public static PullStatusViewHolder newInstance(@NonNull ViewGroup parent) {
        return new PullStatusViewHolder(getView(parent, R.layout.pull_status_row_item));
    }

    @Override public void bind(@NonNull PullRequestTimelineModel model) {
        PullRequestTimelineQuery.Status pullRequestStatusModel = model.getStatus();
        StatusState stateType = pullRequestStatusModel.state();
        stateImage.setImageResource(getDrawable(stateType));
        if (stateType == StatusState.FAILURE) {
            stateImage.tintDrawableColor(red);
            status.setText(R.string.checks_failed);
        } else if (stateType == StatusState.PENDING) {
            if (model.isMergeable) {
                stateImage.setImageResource(R.drawable.ic_check_small);
                stateImage.tintDrawableColor(green);
                status.setText(R.string.commit_can_be_merged);
            } else {
                stateImage.tintDrawableColor(indigo);
                status.setText(R.string.checks_pending);
            }
        } else {
            stateImage.tintDrawableColor(green);
            if (model.isMergeable) {
                status.setText(R.string.commit_can_be_merged);
            } else {
                status.setText(R.string.checks_passed);
            }
        }
        if (!pullRequestStatusModel.contexts().isEmpty()) {
            SpannableBuilder builder = SpannableBuilder.builder();
            Stream.of(pullRequestStatusModel.contexts())
                    .forEachIndexed((index, statusesModel) -> {
                        builder.append(" ").append(ContextCompat.getDrawable(statuses.getContext(), getDrawable(statusesModel.state())));
                        Object targetUrl = statusesModel.targetUrl();
                        if (targetUrl != null) {
                            boolean canAdd = index < pullRequestStatusModel.contexts().size();
                            builder.append(" ")
                                    .append(statusesModel.context())
                                    .append(" ")
                                    .url(statusesModel.description(), v -> SchemeParser.launchUri(v.getContext(), targetUrl.toString()))
                                    .append(canAdd ? "\n" : "");
                        } else {
                            builder.append("\n");
                        }
                    });
            if (!InputHelper.isEmpty(builder)) {
                statuses.setMovementMethod(LinkMovementMethod.getInstance());
                statuses.setText(builder);
                statuses.setVisibility(View.VISIBLE);
            } else {
                statuses.setVisibility(View.GONE);
            }
        } else {
            statuses.setVisibility(View.GONE);
        }
    }

    @DrawableRes private int getDrawable(StatusState stateType) {
        switch (stateType) {
            case EXPECTED:
            case ERROR:
            case FAILURE:
                return R.drawable.ic_issues_small;
            case PENDING:
                return R.drawable.ic_time_small;
            case SUCCESS:
                return R.drawable.ic_check_small;
        }
        return R.drawable.ic_time_small;
    }
}
