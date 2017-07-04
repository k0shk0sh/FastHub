package com.fastaccess.provider.timeline;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.R;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.model.IssueEvent;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.IssueEventType;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.widgets.LabelSpan;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.zzhoujay.markdown.style.CodeSpan;

import java.util.Date;

/**
 * Created by Kosh on 20 Apr 2017, 7:18 PM
 */

public class TimelineProvider {

    @NonNull public static SpannableBuilder getStyledEvents(@NonNull IssueEvent issueEventModel, @NonNull Context context, boolean isMerged) {
        IssueEventType event = issueEventModel.getEvent();
        SpannableBuilder spannableBuilder = SpannableBuilder.builder();
        if (event != null) {
            String to = context.getString(R.string.to);
            String from = context.getString(R.string.from);
            String thisString = context.getString(R.string.this_value);
            String in = context.getString(R.string.in_value);
            if (event == IssueEventType.labeled || event == IssueEventType.unlabeled) {
                if (issueEventModel.getAssignee() != null && issueEventModel.getAssigner() != null) {
                    spannableBuilder.bold(issueEventModel.getAssigner().getLogin());
                } else if (issueEventModel.getActor() != null) {
                    spannableBuilder.bold(issueEventModel.getActor().getLogin());
                }
                spannableBuilder.append(" ").append(event.name().replaceAll("_", " "));
                LabelModel labelModel = issueEventModel.getLabel();
                int color = Color.parseColor("#" + labelModel.getColor());
                spannableBuilder.append(" ").append(" " + labelModel.getName() + " ", new CodeSpan(color, ViewHelper.generateTextColor(color), 5));
                spannableBuilder.append(" ").append(getDate(issueEventModel.getCreatedAt()));
            } else {
                User user = null;
                if (issueEventModel.getAssignee() != null && issueEventModel.getAssigner() != null) {
                    user = issueEventModel.getAssigner();
                } else if (issueEventModel.getActor() != null) {
                    user = issueEventModel.getActor();
                }
                if (user != null) {
                    spannableBuilder.bold(user.getLogin());
                }
                if (event == IssueEventType.closed || event == IssueEventType.reopened) {
                    if (isMerged) {
                        spannableBuilder.append(" ").append(IssueEventType.merged.name());
                    } else {
                        spannableBuilder
                                .append(" ")
                                .append(event.name().replaceAll("_", " "))
                                .append(" ")
                                .append(thisString);
                    }
                    if (issueEventModel.getCommitId() != null) {
                        spannableBuilder
                                .append(" ")
                                .append(in)
                                .append(" ")
                                .url(substring(issueEventModel.getCommitId()));

                    }
                } else if (event == IssueEventType.assigned || event == IssueEventType.unassigned) {
                    spannableBuilder
                            .append(" ");
                    if (user != null && user.getLogin().equalsIgnoreCase(issueEventModel.getAssignee().getLogin())){
                        spannableBuilder
                                .append(event == IssueEventType.assigned ? "self-assigned this" : "removed their assignment");
                    } else {
                        spannableBuilder
                                .append(event == IssueEventType.assigned ? "assigned" : "unassigned");
                        spannableBuilder
                                .append(" ")
                                .bold(issueEventModel.getAssignee().getLogin());
                    }
                } else {
                    spannableBuilder.append(" ").append(event.name().replaceAll("_", " "));
                }
                if (event == IssueEventType.milestoned || event == IssueEventType.demilestoned) {
                    spannableBuilder.append(" ")
                            .append(event == IssueEventType.milestoned ? to : from)
                            .append(" ")
                            .bold(issueEventModel.getMilestone().getTitle());
                } else if (event == IssueEventType.renamed) {
                    spannableBuilder
                            .append(" ")
                            .append(from)
                            .append(" ")
                            .bold(issueEventModel.getRename().getFromValue())
                            .append(" ")
                            .append(to)
                            .append(" ")
                            .bold(issueEventModel.getRename().getToValue());
                } else if (event == IssueEventType.referenced || event == IssueEventType.merged) {
                    spannableBuilder
                            .append(" ")
                            .append("commit")
                            .append(" ")
                            .url(substring(issueEventModel.getCommitId()));
                } else if (event == IssueEventType.review_requested) {
                    spannableBuilder
                            .append(" ")
                            .append(from)
                            .append(" ")
                            .bold(issueEventModel.getRequestedReviewer().getLogin());
                }
                spannableBuilder.append(" ").append(getDate(issueEventModel.getCreatedAt()));
            }
        }
        return spannableBuilder;
    }

    public static void appendLabels(@NonNull LabelModel labelModel, @NonNull SpannableBuilder spannableBuilder) {
        int color = Color.parseColor("#" + labelModel.getColor());
        spannableBuilder.append(" ").append(" " + labelModel.getName() + " ", new LabelSpan(color));
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
