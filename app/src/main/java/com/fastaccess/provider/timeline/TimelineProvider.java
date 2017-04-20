package com.fastaccess.provider.timeline;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.R;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.model.IssueEvent;
import com.fastaccess.data.dao.types.IssueEventType;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.widgets.LabelSpan;
import com.fastaccess.ui.widgets.SpannableBuilder;

import java.util.Date;

/**
 * Created by Kosh on 20 Apr 2017, 7:18 PM
 */

public class TimelineProvider {


    @NonNull public static SpannableBuilder getStyledEvents(@NonNull IssueEvent issueEventModel, @NonNull Context context) {
        IssueEventType event = issueEventModel.getEvent();
        SpannableBuilder spannableBuilder = SpannableBuilder.builder();
        if (event != null) {
            String to = context.getString(R.string.to);
            String from = context.getString(R.string.from);
            String thisString = context.getString(R.string.this_value);
            String in = context.getString(R.string.in_value);
            switch (event) {
                case labeled:
                case unlabeled: {
                    if (issueEventModel.getAssignee() != null && issueEventModel.getAssigner() != null) {
                        spannableBuilder.append(SpannableBuilder.builder().bold(issueEventModel.getAssigner().getLogin()),
                                new LabelSpan(Color.TRANSPARENT));
                    } else if (issueEventModel.getActor() != null) {
                        spannableBuilder.append(SpannableBuilder.builder().bold(issueEventModel.getActor().getLogin()),
                                new LabelSpan(Color.TRANSPARENT));
                    }
                    spannableBuilder.append(event.name().replaceAll("_", " "), new LabelSpan(Color.TRANSPARENT));
                    LabelModel labelModel = issueEventModel.getLabel();
                    int color = Color.parseColor("#" + labelModel.getColor());
                    spannableBuilder.append(" " + labelModel.getName() + " ", new LabelSpan(color));
                    spannableBuilder.append(getDate(issueEventModel.getCreatedAt()), new LabelSpan(Color.TRANSPARENT));
                }
                default: {
                    if (issueEventModel.getAssignee() != null && issueEventModel.getAssigner() != null) {
                        spannableBuilder.bold(issueEventModel.getAssigner().getLogin());
                    } else if (issueEventModel.getActor() != null) {
                        spannableBuilder.bold(issueEventModel.getActor().getLogin());
                    }

                    spannableBuilder.append(" ").append(event.name().replaceAll("_", " "));
                    switch (event) {
                        case assigned:
                        case unassigned:
                            spannableBuilder
                                    .append(" ")
                                    .bold(issueEventModel.getAssignee().getLogin());
                            Logger.e("Hello: " + spannableBuilder);
                            break;
                        case milestoned:
                        case demilestoned:
                            spannableBuilder.append(" ").append(event == IssueEventType.milestoned ? to : from)
                                    .bold(issueEventModel.getMilestone().getTitle());
                            break;
                        case renamed:
                            spannableBuilder
                                    .append(" ")
                                    .append(from)
                                    .bold(issueEventModel.getRename().getFromValue())
                                    .append(to)
                                    .append(" ")
                                    .bold(issueEventModel.getRename().getToValue());
                            break;
                        case referenced:
                        case merged:
                            spannableBuilder
                                    .append(" ")
                                    .append(thisString)
                                    .append(" ")
                                    .append(in)
                                    .append(" ")
                                    .url(substring(issueEventModel.getCommitId()));
                            break;
                        case review_requested:
                            spannableBuilder
                                    .append(" ")
                                    .append(from)
                                    .append(" ")
                                    .bold(issueEventModel.getRequestedReviewer().getLogin());
                            break;
                        case closed:
                        case reopened:
                            if (issueEventModel.getCommitId() != null) {
                                spannableBuilder
                                        .append(" ")
                                        .append(thisString)
                                        .append(" ")
                                        .append(in)
                                        .append(" ")
                                        .url(substring(issueEventModel.getCommitId()));
                            }
                            break;
                    }
                    spannableBuilder.append(" ").append(getDate(issueEventModel.getCreatedAt()));
                }
            }
        }
        return spannableBuilder;
    }

    @NonNull private static CharSequence getDate(@Nullable Date date) {
        return ParseDateFormat.getTimeAgo(date);
    }

    @NonNull private static String substring(@Nullable String value) {
        if (value == null) {
            return "";
        }
        if (value.length() <= 7) return value;
        else return value.substring(0, 7);
    }

}
