package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.ReactionsModel;
import com.fastaccess.data.dao.model.User;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 04 May 2017, 7:10 PM
 */

@Getter @Setter public class ReviewCommentModel implements Parcelable {

    private long id;
    private String url;
    private long pullRequestReviewId;
    private String diffHunk;
    private String path;
    private int position;
    private int originalPosition;
    private String commitId;
    private String originalCommitId;
    private User user;
    private String bodyHtml;
    private Date createdAt;
    private Date updatedAt;
    private String htmlUrl;
    private String pullRequestUrl;
    private ReactionsModel reactions;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.url);
        dest.writeLong(this.pullRequestReviewId);
        dest.writeString(this.diffHunk);
        dest.writeString(this.path);
        dest.writeInt(this.position);
        dest.writeInt(this.originalPosition);
        dest.writeString(this.commitId);
        dest.writeString(this.originalCommitId);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.bodyHtml);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeString(this.htmlUrl);
        dest.writeString(this.pullRequestUrl);
    }

    public ReviewCommentModel() {}

    protected ReviewCommentModel(Parcel in) {
        this.id = in.readLong();
        this.url = in.readString();
        this.pullRequestReviewId = in.readLong();
        this.diffHunk = in.readString();
        this.path = in.readString();
        this.position = in.readInt();
        this.originalPosition = in.readInt();
        this.commitId = in.readString();
        this.originalCommitId = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.bodyHtml = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.htmlUrl = in.readString();
        this.pullRequestUrl = in.readString();
    }

    public static final Parcelable.Creator<ReviewCommentModel> CREATOR = new Parcelable.Creator<ReviewCommentModel>() {
        @Override public ReviewCommentModel createFromParcel(Parcel source) {return new ReviewCommentModel(source);}

        @Override public ReviewCommentModel[] newArray(int size) {return new ReviewCommentModel[size];}
    };
}
