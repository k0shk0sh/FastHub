package com.fastaccess.ui.adapter.viewholder;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.PullRequestStatusModel;
import com.fastaccess.data.dao.types.StatusStateType;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindColor;
import butterknife.BindView;

/**
 * Created by Kosh on 10 Apr 2017, 3:40 AM
 */

public class PullStatusViewHolder extends BaseViewHolder<PullRequestStatusModel> {

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

    @Override public void bind(@NonNull PullRequestStatusModel pullRequestStatusModel) {
        if (pullRequestStatusModel.getState() != null) {
            StatusStateType stateType = pullRequestStatusModel.getState();
            stateImage.setImageResource(stateType.getDrawableRes());
            if (stateType == StatusStateType.failure) {
                stateImage.tintDrawableColor(red);
                status.setText(R.string.checks_failed);
            } else if (stateType == StatusStateType.pending) {
                if (pullRequestStatusModel.isMergable()) {
                    stateImage.setImageResource(R.drawable.ic_check_small);
                    stateImage.tintDrawableColor(green);
                    status.setText(R.string.commit_can_be_merged);
                } else {
                    stateImage.setImageResource(stateType.getDrawableRes());
                    stateImage.tintDrawableColor(indigo);
                    status.setText(R.string.checks_pending);
                }
            } else {
                stateImage.tintDrawableColor(green);
                if (pullRequestStatusModel.isMergable()) {
                    status.setText(R.string.commit_can_be_merged);
                } else {
                    status.setText(R.string.checks_passed);
                }
            }
        }
        if (pullRequestStatusModel.getStatuses() != null && !pullRequestStatusModel.getStatuses().isEmpty()) {
            SpannableBuilder builder = SpannableBuilder.builder();
            Stream.of(pullRequestStatusModel.getStatuses())
                    .filter(statusesModel -> statusesModel.getState() != null)
                    .forEach(statusesModel -> builder
                            .append(ContextCompat.getDrawable(statuses.getContext(), statusesModel.getState().getDrawableRes()))
                            .append(" ")
                            .url(statusesModel.getDescription(), v -> {
                                SchemeParser.launchUri(v.getContext(), Uri.parse(statusesModel.getTargetUrl()));
                            })
                            .append("\n"));
            if (!InputHelper.isEmpty(builder)) {
                statuses.setText(builder);
                statuses.setVisibility(View.VISIBLE);
            } else {
                statuses.setVisibility(View.GONE);
            }
        } else {
            statuses.setVisibility(View.GONE);
        }
    }
}
