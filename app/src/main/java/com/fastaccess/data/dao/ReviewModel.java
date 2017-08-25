package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.User;

import java.util.Date;
import java.util.List;

/**
 * Created by Kosh on 10 Apr 2017, 4:26 PM
 */

public class ReviewModel implements Parcelable {

    private long id;
    private User user;
    private String bodyHtml;
    private String state;
    private Date submittedAt;
    private String commitId;
    private String diffText;
    private List<ReviewCommentModel> comments;
    private ReactionsModel reactions;
    private String bodyText;

    public ReviewModel() {}

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewModel that = (ReviewModel) o;
        return id == that.id;
    }

    @Override public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.bodyHtml);
        dest.writeString(this.state);
        dest.writeLong(this.submittedAt != null ? this.submittedAt.getTime() : -1);
        dest.writeString(this.commitId);
        dest.writeString(this.diffText);
        dest.writeTypedList(this.comments);
        dest.writeParcelable(this.reactions, flags);
        dest.writeString(this.bodyText);
    }

    protected ReviewModel(Parcel in) {
        this.id = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.bodyHtml = in.readString();
        this.state = in.readString();
        long tmpSubmittedAt = in.readLong();
        this.submittedAt = tmpSubmittedAt == -1 ? null : new Date(tmpSubmittedAt);
        this.commitId = in.readString();
        this.diffText = in.readString();
        this.comments = in.createTypedArrayList(ReviewCommentModel.CREATOR);
        this.reactions = in.readParcelable(ReactionsModel.class.getClassLoader());
        this.bodyText = in.readString();
    }

    public static final Creator<ReviewModel> CREATOR = new Creator<ReviewModel>() {
        @Override public ReviewModel createFromParcel(Parcel source) {return new ReviewModel(source);}

        @Override public ReviewModel[] newArray(int size) {return new ReviewModel[size];}
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getDiffText() {
        return diffText;
    }

    public void setDiffText(String diffText) {
        this.diffText = diffText;
    }

    public List<ReviewCommentModel> getComments() {
        return comments;
    }

    public void setComments(List<ReviewCommentModel> comments) {
        this.comments = comments;
    }

    public ReactionsModel getReactions() {
        return reactions;
    }

    public void setReactions(ReactionsModel reactions) {
        this.reactions = reactions;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }
}
