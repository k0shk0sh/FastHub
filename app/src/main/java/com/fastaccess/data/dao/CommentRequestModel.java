package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 20 Nov 2016, 10:40 AM
 */

@Getter @Setter @NoArgsConstructor
public class CommentRequestModel implements Parcelable {
    private String body;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {dest.writeString(this.body);}

    @SuppressWarnings("WeakerAccess") protected CommentRequestModel(Parcel in) {this.body = in.readString();}

    public static final Creator<CommentRequestModel> CREATOR = new Creator<CommentRequestModel>() {
        @Override public CommentRequestModel createFromParcel(Parcel source) {return new CommentRequestModel(source);}

        @Override public CommentRequestModel[] newArray(int size) {return new CommentRequestModel[size];}
    };
}
