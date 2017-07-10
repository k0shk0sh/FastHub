package com.fastaccess.data.dao;

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

    @NonNull public static List<TimelineModel> construct(@Nullable List<Comment> commentList, @Nullable List<IssueEvent> eventList) {
        ArrayList<TimelineModel> list = new ArrayList<>();
        if (commentList != null && !commentList.isEmpty()) {
            list.addAll(Stream.of(commentList)
                    .map(TimelineModel::new)
                    .collect(Collectors.toList()));
        }
        if (eventList != null && !eventList.isEmpty()) {
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

    @NonNull public static List<TimelineModel> construct(@Nullable List<Comment> commentList, @Nullable List<IssueEvent> eventList,
                                                         @Nullable PullRequestStatusModel status, @Nullable List<ReviewModel> reviews,
                                                         @Nullable List<ReviewCommentModel> reviewComments) {
        ArrayList<TimelineModel> list = new ArrayList<>();
        if (status != null) {
            list.add(new TimelineModel(status));
        }
        if (reviews != null && !reviews.isEmpty()) {
            list.addAll(constructReviews(reviews, reviewComments));
        }
        if (commentList != null && !commentList.isEmpty()) {
            list.addAll(Stream.of(commentList)
                    .map(TimelineModel::new)
                    .collect(Collectors.toList()));
        }
        if (eventList != null && !eventList.isEmpty()) {
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
        if (issueEventMap != null && !issueEventMap.isEmpty()) {
            for (Map.Entry<String, List<IssueEvent>> stringListEntry : issueEventMap.entrySet()) {
                List<LabelModel> labelModels = new ArrayList<>();
                List<IssueEvent> events = stringListEntry.getValue();
                IssueEvent toAdd = null;
                for (IssueEvent event : events) {
                    if (event.getEvent() == IssueEventType.labeled || event.getEvent() == IssueEventType.unlabeled) {
                        if (toAdd == null) {
                            toAdd = event;
                        }
                        long time = toAdd.getCreatedAt().after(event.getCreatedAt()) ? (toAdd.getCreatedAt().getTime() - event
                                .getCreatedAt().getTime()) : (event.getCreatedAt().getTime() - toAdd.getCreatedAt().getTime());
                        if (TimeUnit.MINUTES.toMinutes(time) <= 2 && toAdd.getEvent() == event.getEvent()) {
                            labelModels.add(event.getLabel());
                        } else {
                            models.add(new TimelineModel(event));
                        }
                    } else {
                        models.add(new TimelineModel(event));
                    }
                }
                if (toAdd != null) {
                    toAdd.setLabels(labelModels);
                    models.add(new TimelineModel(toAdd));
                }
            }
        }
        return Stream.of(models)
                .sortBy(TimelineModel::getSortedDate)
                .toList();
    }

    @NonNull private static List<TimelineModel> constructReviews
            (@NonNull List<ReviewModel> reviews, @Nullable List<ReviewCommentModel> comments) {
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
                    groupedReviewModel.setId(reviewCommentModel.getId());
                }
                for (ReviewCommentModel reviewCommentModel : reviewCommentModels) {
                    if (reviewCommentModel.getCreatedAt() != null) {
                        groupedReviewModel.setDate(reviewCommentModel.getCreatedAt());
                        break;
                    }
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
        dest.writeParcelable(this.groupedReview, flags);
        dest.writeParcelable(this.reviewComment, flags);
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
        this.groupedReview = in.readParcelable(GroupedReviewModel.class.getClassLoader());
        this.reviewComment = in.readParcelable(ReviewCommentModel.class.getClassLoader());
        long tmpSortedDate = in.readLong();
        this.sortedDate = tmpSortedDate == -1 ? null : new Date(tmpSortedDate);
    }

    public static final Creator<TimelineModel> CREATOR = new Creator<TimelineModel>() {
        @Override public TimelineModel createFromParcel(Parcel source) {return new TimelineModel(source);}

        @Override public TimelineModel[] newArray(int size) {return new TimelineModel[size];}
    };
}
