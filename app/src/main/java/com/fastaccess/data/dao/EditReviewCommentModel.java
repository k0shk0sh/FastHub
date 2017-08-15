package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 22 May 2017, 8:36 PM
 */

@Getter @Setter public class EditReviewCommentModel implements Parcelable {

    public int groupPosition;
    public int commentPosition;
    public String comment;
    public ReviewCommentModel commentModel;
    @SerializedName("in_reply_to") public long inReplyTo;


    public EditReviewCommentModel() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.groupPosition);
        dest.writeInt(this.commentPosition);
        dest.writeString(this.comment);
        dest.writeParcelable(this.commentModel, flags);
        dest.writeLong(this.inReplyTo);
    }

    private EditReviewCommentModel(Parcel in) {
        this.groupPosition = in.readInt();
        this.commentPosition = in.readInt();
        this.comment = in.readString();
        this.commentModel = in.readParcelable(ReviewCommentModel.class.getClassLoader());
        this.inReplyTo = in.readLong();
    }

    public static final Creator<EditReviewCommentModel> CREATOR = new Creator<EditReviewCommentModel>() {
        @Override public EditReviewCommentModel createFromParcel(Parcel source) {return new EditReviewCommentModel(source);}

        @Override public EditReviewCommentModel[] newArray(int size) {return new EditReviewCommentModel[size];}
    };
}
