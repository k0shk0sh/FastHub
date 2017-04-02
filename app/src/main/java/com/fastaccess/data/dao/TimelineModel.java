package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.IssueEvent;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.types.IssueEventType;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 30 Mar 2017, 9:03 PM
 */

@Getter @Setter @NoArgsConstructor public class TimelineModel implements Parcelable {
    public static final int HEADER = 0;
    public static final int EVENT = 1;
    public static final int COMMENT = 2;

    private int type;
    private Issue issue;
    private Comment comment;
    private IssueEvent event;
    private PullRequest pullRequest;

    private TimelineModel(Issue issue) {
        this.type = HEADER;
        this.issue = issue;
    }

    private TimelineModel(PullRequest pullRequest) {
        this.type = HEADER;
        this.pullRequest = pullRequest;
    }

    private TimelineModel(Comment comment) {
        this.type = COMMENT;
        this.comment = comment;
    }

    private TimelineModel(IssueEvent event) {
        this.type = EVENT;
        this.event = event;
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

    @NonNull public static TimelineModel constructEvent(@NonNull IssueEvent event) {
        return new TimelineModel(event);
    }

    @NonNull public static List<TimelineModel> construct(@NonNull List<Comment> commentList, @NonNull List<IssueEvent> eventList) {
        ArrayList<TimelineModel> list = new ArrayList<>();
        if (!commentList.isEmpty()) {
            list.addAll(Stream.of(commentList)
                    .map(TimelineModel::new)
                    .collect(Collectors.toList()));
        }

        if (!eventList.isEmpty()) {
            list.addAll(Stream.of(eventList)
                    .filter(value -> value.getEvent() != IssueEventType.subscribed && value.getEvent() != IssueEventType.unsubscribed
                            && value.getEvent() != IssueEventType.mentioned)
                    .map(TimelineModel::new)
                    .collect(Collectors.toList()));
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

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeParcelable(this.issue, flags);
        dest.writeParcelable(this.comment, flags);
        dest.writeParcelable(this.event, flags);
        dest.writeParcelable(this.pullRequest, flags);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimelineModel model = (TimelineModel) o;
        return comment != null && model.getComment() != null && comment.getId() == model.comment.getId();
    }

    @Override public int hashCode() {
        return comment != null ? (int) comment.getId() : 0;
    }

    protected TimelineModel(Parcel in) {
        this.type = in.readInt();
        this.issue = in.readParcelable(Issue.class.getClassLoader());
        this.comment = in.readParcelable(Comment.class.getClassLoader());
        this.event = in.readParcelable(IssueEvent.class.getClassLoader());
        this.pullRequest = in.readParcelable(PullRequest.class.getClassLoader());
    }

    public static final Creator<TimelineModel> CREATOR = new Creator<TimelineModel>() {
        @Override public TimelineModel createFromParcel(Parcel source) {return new TimelineModel(source);}

        @Override public TimelineModel[] newArray(int size) {return new TimelineModel[size];}
    };
}
