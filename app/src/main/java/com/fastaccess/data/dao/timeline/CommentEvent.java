package com.fastaccess.data.dao.timeline;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.ReactionsModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.User;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 16 Mar 2017, 7:24 PM
 */
@Getter @Setter public class CommentEvent implements Parcelable {
    private long id;
    private User user;
    private String url;
    private String body;
    private String bodyHtml;
    private String htmlUrl;
    private Date createdAt;
    private Date updatedAt;
    private int position;
    private int line;
    private String path;
    private String commitId;
    private String repoId;
    private String login;
    private String gistId;
    private String issueId;
    private String pullRequestId;
    private ReactionsModel reactions;

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment that = (Comment) o;
        return id == that.getId();

    }

    @Override public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public CommentEvent() {}

    @Override public String toString() {
        return "CommentEvent{" +
                "id=" + id +
                ", user=" + user +
                ", url='" + url + '\'' +
                ", body='" + body + '\'' +
                ", bodyHtml='" + bodyHtml + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", position=" + position +
                ", line=" + line +
                ", path='" + path + '\'' +
                ", commitId='" + commitId + '\'' +
                ", repoId='" + repoId + '\'' +
                ", login='" + login + '\'' +
                ", gistId='" + gistId + '\'' +
                ", issueId='" + issueId + '\'' +
                ", pullRequestId='" + pullRequestId + '\'' +
                ", reactions=" + reactions +
                '}';
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.url);
        dest.writeString(this.body);
        dest.writeString(this.bodyHtml);
        dest.writeString(this.htmlUrl);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeInt(this.position);
        dest.writeInt(this.line);
        dest.writeString(this.path);
        dest.writeString(this.commitId);
        dest.writeString(this.repoId);
        dest.writeString(this.login);
        dest.writeString(this.gistId);
        dest.writeString(this.issueId);
        dest.writeString(this.pullRequestId);
        dest.writeParcelable(this.reactions, flags);
    }

    private CommentEvent(Parcel in) {
        this.id = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.url = in.readString();
        this.body = in.readString();
        this.bodyHtml = in.readString();
        this.htmlUrl = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.position = in.readInt();
        this.line = in.readInt();
        this.path = in.readString();
        this.commitId = in.readString();
        this.repoId = in.readString();
        this.login = in.readString();
        this.gistId = in.readString();
        this.issueId = in.readString();
        this.pullRequestId = in.readString();
        this.reactions = in.readParcelable(ReactionsModel.class.getClassLoader());
    }

    public static final Creator<CommentEvent> CREATOR = new Creator<CommentEvent>() {
        @Override public CommentEvent createFromParcel(Parcel source) {return new CommentEvent(source);}

        @Override public CommentEvent[] newArray(int size) {return new CommentEvent[size];}
    };
}
