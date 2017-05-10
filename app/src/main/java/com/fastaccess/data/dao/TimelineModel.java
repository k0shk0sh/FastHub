package com.fastaccess.data.dao;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.IssueEvent;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.types.IssueEventType;
import com.fastaccess.data.dao.types.ReviewStateType;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.widgets.LabelSpan;
import com.fastaccess.ui.widgets.SpannableBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.annimon.stream.Collectors.toList;

/**
 * Created by Kosh on 30 Mar 2017, 9:03 PM
 */

@Getter @Setter @NoArgsConstructor public class TimelineModel implements Parcelable {
    public static final int HEADER = 0;
    public static final int STATUS = 1;
    public static final int REVIEW = 2;
    public static final int GROUPED_REVIEW = 3;
    public static final int EVENT = 4;
    public static final int COMMENT = 5;

    private int type;
    private Issue issue;
    private Comment comment;
    private IssueEvent event;
    private PullRequest pullRequest;
    private PullRequestStatusModel status;
    private ReviewModel review;
    private GroupedReviewModel groupedReview;
    private ReviewCommentModel reviewComment;
    private Date sortedDate;

    private TimelineModel(Issue issue) {
        this.type = HEADER;
        this.issue = issue;
        this.sortedDate = issue.getCreatedAt();
    }

    private TimelineModel(PullRequest pullRequest) {
        this.type = HEADER;
        this.pullRequest = pullRequest;
        this.sortedDate = pullRequest.getCreatedAt();
    }

    private TimelineModel(Comment comment) {
        this.type = COMMENT;
        this.comment = comment;
        this.sortedDate = comment.getCreatedAt() == null ? new Date() : comment.getCreatedAt();
    }

    private TimelineModel(IssueEvent event) {
        this.type = EVENT;
        this.event = event;
        this.sortedDate = event.getCreatedAt();
    }

    private TimelineModel(PullRequestStatusModel status) {
        this.type = STATUS;
        this.status = status;
        this.sortedDate = status.getCreatedAt();
    }

    private TimelineModel(ReviewModel review) {
        this.type = REVIEW;
        this.review = review;
        this.sortedDate = review.getSubmittedAt();
    }

    private TimelineModel(GroupedReviewModel groupedReview) {
        this.type = GROUPED_REVIEW;
        this.groupedReview = groupedReview;
        this.sortedDate = groupedReview.getDate();
    }

    @NonNull public static TimelineModel constructHeader(@NonNull Issue issue) {
        return new TimelineModel(issue);
    }

    @NonNull public static TimelineModel constructHeader(@NonNull PullRequest pullRequest) {
        return new TimelineModel(pullRequest);
    }

    @NonNull public static TimelineModel constructComment(@NonNull Comment comment) {
        return new TimelineModel(comment);
    }

    @NonNull public static List<TimelineModel> construct(@Nullable List<Comment> commentList) {
        ArrayList<TimelineModel> list = new ArrayList<>();
        if (commentList != null && !commentList.isEmpty()) {
            list.addAll(Stream.of(commentList)
                    .map(TimelineModel::new)
                    .collect(Collectors.toList()));
        }
        return list;
    }

    @NonNull public static List<TimelineModel> construct(@NonNull List<Comment> commentList, @NonNull List<IssueEvent> eventList) {
        ArrayList<TimelineModel> list = new ArrayList<>();
        if (!commentList.isEmpty()) {
            list.addAll(Stream.of(commentList)
                    .map(TimelineModel::new)
                    .collect(Collectors.toList()));
        }

        if (!eventList.isEmpty()) {
            list.addAll(constructLabels(eventList));
        }

        return Stream.of(list).sorted((o1, o2) -> {
            if (o1.getEvent() != null && o2.getComment() != null) {
                return o1.getEvent().getCreatedAt().compareTo(o2.getComment().getCreatedAt());
            } else if (o1.getComment() != null && o2.getEvent() != null) {
                return o1.getComment().getCreatedAt().compareTo(o2.getEvent().getCreatedAt());
            } else {
                return Integer.valueOf(o1.getType()).compareTo(o2.getType());
            }
        }).collect(Collectors.toList());
    }

    @NonNull public static List<TimelineModel> construct(@NonNull List<Comment> commentList, @NonNull List<IssueEvent> eventList,
                                                         @Nullable PullRequestStatusModel status, @Nullable List<ReviewModel> reviews,
                                                         @Nullable List<ReviewCommentModel> reviewComments) {
        ArrayList<TimelineModel> list = new ArrayList<>();
        if (status != null) {
            list.add(new TimelineModel(status));
        }
        if (reviews != null && !reviews.isEmpty()) {
            list.addAll(constructReviews(reviews, reviewComments));
        }
        if (!commentList.isEmpty()) {
            list.addAll(Stream.of(commentList)
                    .map(TimelineModel::new)
                    .collect(Collectors.toList()));
        }
        if (!eventList.isEmpty()) {
            list.addAll(constructLabels(eventList));
        }

        return Stream.of(list).sortBy(model -> {
            if (model.getSortedDate() != null) {
                return model.getSortedDate().getTime();
            } else {
                return (long) model.getType();
            }
        }).collect(Collectors.toList());
    }

    @NonNull private static List<TimelineModel> constructLabels(@NonNull List<IssueEvent> eventList) {
        List<TimelineModel> models = new ArrayList<>();
        Map<String, List<IssueEvent>> issueEventMap = Stream.of(eventList)
                .filter(value -> value.getEvent() != null && value.getEvent() != IssueEventType.subscribed &&
                        value.getEvent() != IssueEventType.unsubscribed && value.getEvent() != IssueEventType.mentioned)
                .collect(Collectors.groupingBy(issueEvent -> {
                    if (issueEvent.getAssigner() != null && issueEvent.getAssignee() != null) {
                        return issueEvent.getAssigner().getLogin();
                    }
                    return issueEvent.getActor().getLogin();
                }));
        for (List<IssueEvent> issueEvents : issueEventMap.values()) {
            IssueEvent toAdd = null;
            SpannableBuilder spannableBuilder = SpannableBuilder.builder();
            for (IssueEvent issueEventModel : issueEvents) {
                if (issueEventModel != null) {
                    IssueEventType event = issueEventModel.getEvent();
                    if (event != null) {
                        if (toAdd == null) {
                            toAdd = issueEventModel;
                        }
                        long time = toAdd.getCreatedAt().after(issueEventModel.getCreatedAt()) ? (toAdd.getCreatedAt().getTime() - issueEventModel
                                .getCreatedAt().getTime()) : (issueEventModel.getCreatedAt().getTime() - toAdd.getCreatedAt().getTime());
                        if (TimeUnit.MINUTES.toMinutes(time) <= 2 && toAdd.getEvent() == event) {
                            if (event == IssueEventType.labeled || event == IssueEventType.unlabeled) {
                                LabelModel labelModel = issueEventModel.getLabel();
                                int color = Color.parseColor("#" + labelModel.getColor());
                                spannableBuilder.append(" ")
                                        .append(" " + labelModel.getName() + " ", new LabelSpan(color))
                                        .append(" ");
                            } else if (event == IssueEventType.assigned || event == IssueEventType.unassigned) {
                                spannableBuilder.append(" ")
                                        .bold(issueEventModel.getAssignee() != null ? issueEventModel.getAssignee().getLogin() : "",
                                                new LabelSpan(Color.TRANSPARENT))
                                        .append(" ");
                            }
                        } else {
                            models.add(new TimelineModel(issueEventModel));
                        }
                    } else {
                        models.add(new TimelineModel(issueEventModel));
                    }
                }
            }
            if (toAdd != null) {
                SpannableBuilder builder = SpannableBuilder.builder();
                if (toAdd.getAssignee() != null && toAdd.getAssigner() != null) {
                    builder.bold(toAdd.getAssigner().getLogin(), new LabelSpan(Color.TRANSPARENT));
                } else {
                    if (toAdd.getActor() != null) {
                        builder.bold(toAdd.getActor().getLogin(), new LabelSpan(Color.TRANSPARENT));
                    }
                }
                builder.append(" ")
                        .append(toAdd.getEvent().name().replaceAll("_", " "), new LabelSpan(Color.TRANSPARENT));
                toAdd.setLabels(SpannableBuilder.builder()
                        .append(builder)
                        .append(spannableBuilder)
                        .append(" ")
                        .append(ParseDateFormat.getTimeAgo(toAdd.getCreatedAt()), new LabelSpan(Color.TRANSPARENT)));
                models.add(new TimelineModel(toAdd));
            }
        }
        return Stream.of(models)
                .sortBy(timelineModel -> timelineModel.getEvent().getCreatedAt())
                .collect(Collectors.toList());
    }

    @NonNull private static List<TimelineModel> constructReviews(@NonNull List<ReviewModel> reviews, @Nullable List<ReviewCommentModel> comments) {
        List<TimelineModel> models = new ArrayList<>();
        if (comments == null || comments.isEmpty()) {
            models.addAll(Stream.of(reviews)
                    .map(TimelineModel::new)
                    .collect(Collectors.toList()));
        } else { // this is how bad github API is.
            Map<Integer, List<ReviewCommentModel>> mappedComments = Stream.of(comments)
                    .collect(Collectors.groupingBy(ReviewCommentModel::getOriginalPosition, LinkedHashMap::new,
                            Collectors.mapping(o -> o, toList())));
            for (Map.Entry<Integer, List<ReviewCommentModel>> entry : mappedComments.entrySet()) {
                List<ReviewCommentModel> reviewCommentModels = entry.getValue();
                GroupedReviewModel groupedReviewModel = new GroupedReviewModel();
                if (!reviewCommentModels.isEmpty()) {
                    ReviewCommentModel reviewCommentModel = reviewCommentModels.get(0);
                    groupedReviewModel.setPath(reviewCommentModel.getPath());
                    groupedReviewModel.setDiffText(reviewCommentModel.getDiffHunk());
                    groupedReviewModel.setDate(reviewCommentModel.getCreatedAt());
                    groupedReviewModel.setPosition(reviewCommentModel.getOriginalPosition());
                }
                groupedReviewModel.setComments(reviewCommentModels);
                models.add(new TimelineModel(groupedReviewModel));
            }
            models.addAll(Stream.of(reviews)
                    .filter(reviewModel -> !InputHelper.isEmpty(reviewModel.getBody()) || reviewModel.getState() == ReviewStateType.APPROVED)
                    .map(TimelineModel::new)
                    .collect(Collectors.toList()));
        }
        return models;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimelineModel model = (TimelineModel) o;
        return (comment != null && model.getComment() != null) && (comment.getId() == model.comment.getId());
    }

    @Override public int hashCode() {
        return comment != null ? (int) comment.getId() : 0;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeParcelable(this.issue, flags);
        dest.writeParcelable(this.comment, flags);
        dest.writeParcelable(this.event, flags);
        dest.writeParcelable(this.pullRequest, flags);
        dest.writeParcelable(this.status, flags);
        dest.writeParcelable(this.review, flags);
        dest.writeLong(this.sortedDate != null ? this.sortedDate.getTime() : -1);
    }

    protected TimelineModel(Parcel in) {
        this.type = in.readInt();
        this.issue = in.readParcelable(Issue.class.getClassLoader());
        this.comment = in.readParcelable(Comment.class.getClassLoader());
        this.event = in.readParcelable(IssueEvent.class.getClassLoader());
        this.pullRequest = in.readParcelable(PullRequest.class.getClassLoader());
        this.status = in.readParcelable(PullRequestStatusModel.class.getClassLoader());
        this.review = in.readParcelable(ReviewModel.class.getClassLoader());
        long tmpSortedDate = in.readLong();
        this.sortedDate = tmpSortedDate == -1 ? null : new Date(tmpSortedDate);
    }

    public static final Creator<TimelineModel> CREATOR = new Creator<TimelineModel>() {
        @Override public TimelineModel createFromParcel(Parcel source) {return new TimelineModel(source);}

        @Override public TimelineModel[] newArray(int size) {return new TimelineModel[size];}
    };
}
