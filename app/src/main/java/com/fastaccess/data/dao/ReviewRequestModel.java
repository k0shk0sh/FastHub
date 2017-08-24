package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 24 Jun 2017, 4:15 PM
 */

@Getter @Setter public class ReviewRequestModel implements Parcelable {
    public String commitId;
    public String body;
    public String event;
    public List<CommentRequestModel> comments;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.commitId);
        dest.writeString(this.body);
        dest.writeString(this.event);
        dest.writeTypedList(this.comments);
    }

    private ReviewRequestModel(Parcel in) {
        this.commitId = in.readString();
        this.body = in.readString();
        this.event = in.readString();
        this.comments = in.createTypedArrayList(CommentRequestModel.CREATOR);
    }

    public ReviewRequestModel() {
    }

    public static final Parcelable.Creator<ReviewRequestModel> CREATOR = new Parcelable.Creator<ReviewRequestModel>() {
        @Override public ReviewRequestModel createFromParcel(Parcel source) {return new ReviewRequestModel(source);}

        @Override public ReviewRequestModel[] newArray(int size) {return new ReviewRequestModel[size];}
    };
}
