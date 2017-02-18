package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 16 Dec 2016, 11:40 PM
 */

@Getter @Setter @NoArgsConstructor
public class MergeResponseModel implements Parcelable {

    private String sha;
    private boolean merged;
    private String message;
    private String documentationUrl;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sha);
        dest.writeByte(this.merged ? (byte) 1 : (byte) 0);
        dest.writeString(this.message);
        dest.writeString(this.documentationUrl);
    }

    @SuppressWarnings("WeakerAccess") protected MergeResponseModel(Parcel in) {
        this.sha = in.readString();
        this.merged = in.readByte() != 0;
        this.message = in.readString();
        this.documentationUrl = in.readString();
    }

    public static final Creator<MergeResponseModel> CREATOR = new Creator<MergeResponseModel>() {
        @Override public MergeResponseModel createFromParcel(Parcel source) {return new MergeResponseModel(source);}

        @Override public MergeResponseModel[] newArray(int size) {return new MergeResponseModel[size];}
    };
}
