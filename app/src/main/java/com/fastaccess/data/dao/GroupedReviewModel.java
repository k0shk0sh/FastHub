package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

/**
 * Created by Kosh on 07 May 2017, 5:08 PM
 */

public class GroupedReviewModel implements Parcelable {

    private int position; //to group with!
    private String diffText;
    private Date date;
    private String path;
    private long id;
    private List<ReviewCommentModel> comments;

    public GroupedReviewModel() {}

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDiffText() {
        return diffText;
    }

    public void setDiffText(String diffText) {
        this.diffText = diffText;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<ReviewCommentModel> getComments() {
        return comments;
    }

    public void setComments(List<ReviewCommentModel> comments) {
        this.comments = comments;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.position);
        dest.writeString(this.diffText);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeString(this.path);
        dest.writeLong(this.id);
        dest.writeTypedList(this.comments);
    }

    private GroupedReviewModel(Parcel in) {
        this.position = in.readInt();
        this.diffText = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.path = in.readString();
        this.id = in.readLong();
        this.comments = in.createTypedArrayList(ReviewCommentModel.CREATOR);
    }

    public static final Creator<GroupedReviewModel> CREATOR = new Creator<GroupedReviewModel>() {
        @Override public GroupedReviewModel createFromParcel(Parcel source) {return new GroupedReviewModel(source);}

        @Override public GroupedReviewModel[] newArray(int size) {return new GroupedReviewModel[size];}
    };
}
