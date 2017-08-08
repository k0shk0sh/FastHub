package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 01 May 2017, 1:06 AM
 */

@Getter @Setter public class SlackInvitePostModel implements Parcelable {
    private String email;
    private String first_name;
    private String last_name;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
    }

    public SlackInvitePostModel() {}

    private SlackInvitePostModel(Parcel in) {
        this.email = in.readString();
        this.first_name = in.readString();
        this.last_name = in.readString();
    }

    public static final Parcelable.Creator<SlackInvitePostModel> CREATOR = new Parcelable.Creator<SlackInvitePostModel>() {
        @Override public SlackInvitePostModel createFromParcel(Parcel source) {return new SlackInvitePostModel(source);}

        @Override public SlackInvitePostModel[] newArray(int size) {return new SlackInvitePostModel[size];}
    };
}
