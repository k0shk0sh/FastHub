package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 16 Dec 2016, 11:42 PM
 */

@Getter @Setter @NoArgsConstructor
public class MergeRequestModel implements Parcelable {

    private String commitMessage;
    private String sha;
    private String base;
    private String head;
    private String mergeMethod = "merge";

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.commitMessage);
        dest.writeString(this.sha);
        dest.writeString(this.base);
        dest.writeString(this.head);
    }

    @SuppressWarnings("WeakerAccess") protected MergeRequestModel(Parcel in) {
        this.commitMessage = in.readString();
        this.sha = in.readString();
        this.base = in.readString();
        this.head = in.readString();
    }

    public static final Creator<MergeRequestModel> CREATOR = new Creator<MergeRequestModel>() {
        @Override public MergeRequestModel createFromParcel(Parcel source) {return new MergeRequestModel(source);}

        @Override public MergeRequestModel[] newArray(int size) {return new MergeRequestModel[size];}
    };
}
