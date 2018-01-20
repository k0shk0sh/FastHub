package com.fastaccess.data.dao.timeline;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter public class AuthorModel implements Parcelable {
    private String name;
    private String email;
    private Date date;

    public AuthorModel() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
    }

    private AuthorModel(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Creator<AuthorModel> CREATOR = new Creator<AuthorModel>() {
        @Override public AuthorModel createFromParcel(Parcel source) {return new AuthorModel(source);}

        @Override public AuthorModel[] newArray(int size) {return new AuthorModel[size];}
    };
}