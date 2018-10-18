package com.fastaccess.provider.timeline;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.BackgroundColorSpan;

import com.fastaccess.R;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.timeline.GenericEvent;
import com.fastaccess.data.dao.timeline.SourceModel;
import com.fastaccess.data.dao.types.IssueEventType;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.zzhoujay.markdown.style.CodeSpan;

import java.util.Date;

/**
 * Created by Kosh on 20 Apr 2017, 7:18 PM
 */

public class TimelineProvider {

    @NonNull public static SpannableBuilder getStyledEvents(@NonNull GenericEvent issueEventModel,
                                                            @NonNull Context context, boolean isMerged) {
        IssueEventType event = issueEventModel.getEvent();
        SpannableBuilder spannableBuilder = SpannableBuilder.builder();
        Date date = issueEventModel.getCreatedAt() != null
                    ? issueEventModel.getCreatedAt()
                    : issueEventModel.getAuthor() != null
                      ? issueEventModel.getAuthor().getDate() : null;
        if (event != null) {
            String to = context.getString(R.string.to);
            String from = context.getString(R.string.from);
            String thisString = context.getString(R.string.this_value);
            String in = context.getString(R.string.in_value);
            if (event == IssueEventType.labeled || event == IssueEventType.unlabeled) {
                spannableBuilder.bold(issueEventModel.getActor() != null ? issueEventModel.getActor().getLogin() : "anonymous");
                spannableBuilder.append(" ").append(event.name().replaceAll("_", " "));
                LabelModel labelModel = issueEventModel.getLabel();
                int color = Color.parseColor("#" + labelModel.getColor());
                spannableBuilder.append(" ").append(" " + labelModel.getName() + " ", new CodeSpan(color, ViewHelper.generateTextColor(color), 5));
                spannableBuilder.append(" ").append(getDate(issueEventModel.getCreatedAt()));
            } else if (event == IssueEventType.committed) {
                spannableBuilder.append(issueEventModel.getMessage().replaceAll("\n", " "))
                        .append(" ")
                        .url(substring(issueEventModel.getSha()));
            } else {
                User user = null;
                if (issueEventModel.getAssignee() != null && issueEventModel.getAssigner() != null) {
                    user = issueEventModel.getAssigner();
                } else if (issueEventModel.getActor() != null) {
                    user = issueEventModel.getActor();
                } else if (issueEventModel.getAuthor() != null) {
                    user = issueEventModel.getAuthor();
                }
                if (user != null) {
                    spannableBuilder.bold(user.getLogin());
                }
                if ((event == IssueEventType.review_requested || (event == IssueEventType.review_dismissed ||
                        event == IssueEventType.review_request_removed)) && user != null) {
                    appendReviews(issueEventModel, event, spannableBuilder, from, issueEventModel.getReviewRequester());
                } else if (event == IssueEventType.closed || event == IssueEventType.reopened) {
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
                    if ((user != null && issueEventModel.getAssignee() != null) && user.getLogin()
                            .equalsIgnoreCase(issueEventModel.getAssignee().getLogin())) {
                        spannableBuilder
                                .append(event == IssueEventType.assigned ? "self-assigned this" : "removed their assignment");
                    } else {
                        spannableBuilder
                                .append(event == IssueEventType.assigned ? "assigned" : "unassigned");
                        spannableBuilder
                                .append(" ")
                                .bold(issueEventModel.getAssignee() != null ? issueEventModel.getAssignee().getLogin() : "");
                    }
                } else if (event == IssueEventType.locked || event == IssueEventType.unlocked) {
                    spannableBuilder
                            .append(" ")
                            .append(event == IssueEventType.locked ? "locked and limited conversation to collaborators" : "unlocked this " +
                                    "conversation");
                } else if (event == IssueEventType.head_ref_deleted || event == IssueEventType.head_ref_restored) {
                    spannableBuilder.append(" ").append(event.name().replaceAll("_", " "),
                            new BackgroundColorSpan(HtmlHelper.getWindowBackground(PrefGetter.getThemeType())));
                } else if (event == IssueEventType.milestoned || event == IssueEventType.demilestoned) {
                    spannableBuilder.append(" ")
                            .append(event == IssueEventType.milestoned ? "added this to the" : "removed this from the")
                            .append(" ")
                            .bold(issueEventModel.getMilestone().getTitle())
                            .append(" ")
                            .append("milestone");
                } else if (event == IssueEventType.deployed) {
                    spannableBuilder.append(" ")
                            .bold("deployed");
                } else {
                    spannableBuilder.append(" ").append(event.name().replaceAll("_", " "));
                }
                if (event == IssueEventType.renamed) {
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
                } else if (event == IssueEventType.cross_referenced) {
                    SourceModel sourceModel = issueEventModel.getSource();
                    if (sourceModel != null) {
                        String type = sourceModel.getType();
                        SpannableBuilder title = SpannableBuilder.builder();
                        if (sourceModel.getPullRequest() != null) {
                            if (sourceModel.getIssue() != null) title.url("#" + sourceModel.getIssue().getNumber());
                            type = "pull request";
                        } else if (sourceModel.getIssue() != null) {
                            title.url("#" + sourceModel.getIssue().getNumber());
                        } else if (sourceModel.getCommit() != null) {
                            title.url(substring(sourceModel.getCommit().getSha()));
                        } else if (sourceModel.getRepository() != null) {
                            title.url(sourceModel.getRepository().getName());
                        }
                        if (!InputHelper.isEmpty(title)) {
                            spannableBuilder.append(" ")
                                    .append(thisString)
                                    .append(" in ")
                                    .append(type)
                                    .append(" ")
                                    .append(title);
                        }
                    }
                }
                spannableBuilder.append(" ").append(getDate(date));
            }
        }
        return spannableBuilder;
    }

    private static void appendReviews(@NonNull GenericEvent issueEventModel, @NonNull IssueEventType event,
                                      @NonNull SpannableBuilder spannableBuilder, @NonNull String from,
                                      @NonNull User user) {
        spannableBuilder.append(" ");
        User reviewer = issueEventModel.getRequestedReviewer();
        if (reviewer != null && user.getLogin().equalsIgnoreCase(reviewer.getLogin())) {
            spannableBuilder
                    .append(event == IssueEventType.review_requested
                            ? "self-requested a review" : "removed their request for review");
        } else {
            spannableBuilder
                    .append(event == IssueEventType.review_requested ? "Requested a review" : "dismissed the review")
                    .append(" ")
                    .append(reviewer != null && !reviewer.getLogin().equalsIgnoreCase(user.getLogin()) ? from : " ")
                    .append(reviewer != null && !reviewer.getLogin().equalsIgnoreCase(user.getLogin()) ? " " : "");
        }
        if (issueEventModel.getRequestedTeam() != null) {
            String name = !InputHelper.isEmpty(issueEventModel.getRequestedTeam().getName())
                          ? issueEventModel.getRequestedTeam().getName() : issueEventModel.getRequestedTeam().getSlug();
            spannableBuilder
                    .bold(name)
                    .append(" ")
                    .append("team");
        } else if (reviewer != null && !user.getLogin().equalsIgnoreCase(reviewer.getLogin())) {
            spannableBuilder.bold(issueEventModel.getRequestedReviewer().getLogin());
        }
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
