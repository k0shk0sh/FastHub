package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 07 May 2017, 5:08 PM
 */

@Getter @Setter public class GroupedReviewModel implements Parcelable {

    private int position; //to group with!
    private String diffText;
    private Date date;
    private String path;
    private List<ReviewCommentModel> comments;


    public GroupedReviewModel() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.position);
        dest.writeString(this.diffText);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeString(this.path);
        dest.writeTypedList(this.comments);
    }

    protected GroupedReviewModel(Parcel in) {
        this.position = in.readInt();
        this.diffText = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.path = in.readString();
        this.comments = in.createTypedArrayList(ReviewCommentModel.CREATOR);
    }

    public static final Creator<GroupedReviewModel> CREATOR = new Creator<GroupedReviewModel>() {
        @Override public GroupedReviewModel createFromParcel(Parcel source) {return new GroupedReviewModel(source);}

        @Override public GroupedReviewModel[] newArray(int size) {return new GroupedReviewModel[size];}
    };
}
