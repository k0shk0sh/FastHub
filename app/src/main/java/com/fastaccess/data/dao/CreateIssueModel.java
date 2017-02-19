package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 19 Feb 2017, 12:13 PM
 */

@Getter @Setter @NoArgsConstructor
public class CreateIssueModel implements Parcelable {
    private String title;
    private String body;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.body);
    }

    @SuppressWarnings("WeakerAccess") protected CreateIssueModel(Parcel in) {
        this.title = in.readString();
        this.body = in.readString();
    }

    public static final Creator<CreateIssueModel> CREATOR = new Creator<CreateIssueModel>() {
        @Override public CreateIssueModel createFromParcel(Parcel source) {return new CreateIssueModel(source);}

        @Override public CreateIssueModel[] newArray(int size) {return new CreateIssueModel[size];}
    };
}
