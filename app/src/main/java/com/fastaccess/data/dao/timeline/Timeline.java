package com.fastaccess.data.dao.timeline;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.PullRequestStatusModel;
import com.fastaccess.data.dao.ReviewCommentModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.types.IssueEventType;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kosh on 25/07/2017.
 */

@Getter @Setter public class Timeline implements Parcelable {
    public static final int HEADER = 0;
    public static final int LINE_COMMENT = 1;
    public static final int EVENT = 2;
    public static final int COMMENT = 3;
    public static final int STATUS = 4;

    private IssueEventType event;
    private Comment comment;
    private GenericEvent genericEvent;
    private ReviewCommentModel reviewComment;
    private PullRequestStatusModel statusModel;

    public int getType() {
        if (getEvent() != null) {
            switch (getEvent()) {
                case commented:
                    return COMMENT;
                case line_commented:
                    return LINE_COMMENT;
                default:
                    return EVENT;
            }
        } else {
            if (statusModel != null) return STATUS;
            return EVENT;
        }
    }

    public Timeline() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.event == null ? -1 : this.event.ordinal());
        dest.writeParcelable(this.comment, flags);
        dest.writeParcelable(this.genericEvent, flags);
        dest.writeParcelable(this.reviewComment, flags);
        dest.writeParcelable(this.statusModel, flags);
    }

    protected Timeline(Parcel in) {
        int tmpEvent = in.readInt();
        this.event = tmpEvent == -1 ? null : IssueEventType.values()[tmpEvent];
        this.comment = in.readParcelable(Comment.class.getClassLoader());
        this.genericEvent = in.readParcelable(GenericEvent.class.getClassLoader());
        this.reviewComment = in.readParcelable(ReviewCommentModel.class.getClassLoader());
        this.statusModel = in.readParcelable(PullRequestStatusModel.class.getClassLoader());
    }

    public static final Creator<Timeline> CREATOR = new Creator<Timeline>() {
        @Override public Timeline createFromParcel(Parcel source) {return new Timeline(source);}

        @Override public Timeline[] newArray(int size) {return new Timeline[size];}
    };
}
