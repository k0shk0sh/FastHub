package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.User;

import java.util.Date;

/**
 * Created by Kosh on 04 May 2017, 7:10 PM
 */

 public class ReviewCommentModel implements Parcelable {

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
    private String body;
    private Date createdAt;
    private Date updatedAt;
    private String htmlUrl;
    private String pullRequestUrl;
    private ReactionsModel reactions;
    private String authorAssociation;

    public ReviewCommentModel() {}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getPullRequestReviewId() {
        return pullRequestReviewId;
    }

    public void setPullRequestReviewId(long pullRequestReviewId) {
        this.pullRequestReviewId = pullRequestReviewId;
    }

    public String getDiffHunk() {
        return diffHunk;
    }

    public void setDiffHunk(String diffHunk) {
        this.diffHunk = diffHunk;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getOriginalPosition() {
        return originalPosition;
    }

    public void setOriginalPosition(int originalPosition) {
        this.originalPosition = originalPosition;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getOriginalCommitId() {
        return originalCommitId;
    }

    public void setOriginalCommitId(String originalCommitId) {
        this.originalCommitId = originalCommitId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getPullRequestUrl() {
        return pullRequestUrl;
    }

    public void setPullRequestUrl(String pullRequestUrl) {
        this.pullRequestUrl = pullRequestUrl;
    }

    public ReactionsModel getReactions() {
        return reactions;
    }

    public void setReactions(ReactionsModel reactions) {
        this.reactions = reactions;
    }

    public String getAuthorAssociation() {
        return authorAssociation;
    }

    public void setAuthorAssociation(String authorAssociation) {
        this.authorAssociation = authorAssociation;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReviewCommentModel that = (ReviewCommentModel) o;

        return id == that.id;
    }

    @Override public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
        dest.writeString(this.body);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeString(this.htmlUrl);
        dest.writeString(this.pullRequestUrl);
        dest.writeParcelable(this.reactions, flags);
        dest.writeString(this.authorAssociation);
    }

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
        this.body = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.htmlUrl = in.readString();
        this.pullRequestUrl = in.readString();
        this.reactions = in.readParcelable(ReactionsModel.class.getClassLoader());
        this.authorAssociation = in.readString();
    }

    public static final Creator<ReviewCommentModel> CREATOR = new Creator<ReviewCommentModel>() {
        @Override public ReviewCommentModel createFromParcel(Parcel source) {return new ReviewCommentModel(source);}

        @Override public ReviewCommentModel[] newArray(int size) {return new ReviewCommentModel[size];}
    };
}
