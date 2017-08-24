package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.timeline.GenericEvent;
import com.fastaccess.data.dao.types.IssueEventType;

import java.util.List;

import io.reactivex.Observable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by Kosh on 30 Mar 2017, 9:03 PM
 */

@Getter @Setter public class TimelineModel implements Parcelable {
    public static final int HEADER = 1;
    public static final int EVENT = 2;
    public static final int COMMENT = 3;

    public IssueEventType event;
    public Comment comment;
    public GenericEvent genericEvent;
    public ReviewCommentModel reviewComment;
    public PullRequestStatusModel status;
    public Issue issue;
    public PullRequest pullRequest;
    public ReviewModel review;

    public TimelineModel(Issue issue) {
        this.issue = issue;
    }

    public TimelineModel(PullRequest pullRequest) {
        this.pullRequest = pullRequest;
    }

    public TimelineModel(Comment comment) {
        this.comment = comment;
        this.event = IssueEventType.commented;
    }

    public TimelineModel(PullRequestStatusModel statusModel) {
        this.status = statusModel;
    }


    public TimelineModel() {}

    public int getType() {
        if (getEvent() != null) {
            switch (getEvent()) {
                case commented:
                    return COMMENT;
                default:
                    return EVENT;
            }
        } else {
            if (issue != null || pullRequest != null) return HEADER;
            return 0;
        }
    }

    public static TimelineModel constructHeader(Issue issue) {
        return new TimelineModel(issue);
    }

    public static TimelineModel constructHeader(PullRequest pullRequest) {
        return new TimelineModel(pullRequest);
    }

    public static TimelineModel constructComment(Comment comment) {
        return new TimelineModel(comment);
    }

    @NonNull public static Observable<List<TimelineModel>> construct(@Nullable List<Comment> comments) {
        if (comments == null || comments.isEmpty()) return Observable.empty();
        return Observable.fromIterable(comments)
                .map(TimelineModel::new)
                .toList()
                .toObservable();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimelineModel that = (TimelineModel) o;
        if (comment != null) {
            return comment.equals(that.comment);
        } else if (reviewComment != null) {
            return reviewComment.equals(that.reviewComment);
        } else if (review != null) {
            return review.equals(that.review);
        }
        return false;
    }

    @Override public int hashCode() {
        if (comment != null) return comment.hashCode();
        else if (reviewComment != null) return reviewComment.hashCode();
        else if (review != null) return review.hashCode();
        else return -1;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.event == null ? -1 : this.event.ordinal());
        dest.writeParcelable(this.comment, flags);
        dest.writeParcelable(this.genericEvent, flags);
        dest.writeParcelable(this.reviewComment, flags);
        dest.writeParcelable(this.status, flags);
        dest.writeParcelable(this.issue, flags);
        dest.writeParcelable(this.pullRequest, flags);
        dest.writeParcelable(this.review, flags);
    }

    protected TimelineModel(Parcel in) {
        int tmpEvent = in.readInt();
        this.event = tmpEvent == -1 ? null : IssueEventType.values()[tmpEvent];
        this.comment = in.readParcelable(Comment.class.getClassLoader());
        this.genericEvent = in.readParcelable(GenericEvent.class.getClassLoader());
        this.reviewComment = in.readParcelable(ReviewCommentModel.class.getClassLoader());
        this.status = in.readParcelable(PullRequestStatusModel.class.getClassLoader());
        this.issue = in.readParcelable(Issue.class.getClassLoader());
        this.pullRequest = in.readParcelable(PullRequest.class.getClassLoader());
        this.review = in.readParcelable(ReviewModel.class.getClassLoader());
    }

    public static final Creator<TimelineModel> CREATOR = new Creator<TimelineModel>() {
        @Override public TimelineModel createFromParcel(Parcel source) {return new TimelineModel(source);}

        @Override public TimelineModel[] newArray(int size) {return new TimelineModel[size];}
    };
}
