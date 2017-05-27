package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 20 Nov 2016, 10:40 AM
 */

@Getter @Setter @NoArgsConstructor
public class CommentRequestModel implements Parcelable {
    private String body;
    @SerializedName("in_reply_to") private long inReplyTo;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeLong(this.inReplyTo);
    }

    protected CommentRequestModel(Parcel in) {
        this.body = in.readString();
        this.inReplyTo = in.readLong();
    }

    public static final Creator<CommentRequestModel> CREATOR = new Creator<CommentRequestModel>() {
        @Override public CommentRequestModel createFromParcel(Parcel source) {return new CommentRequestModel(source);}

        @Override public CommentRequestModel[] newArray(int size) {return new CommentRequestModel[size];}
    };
}
