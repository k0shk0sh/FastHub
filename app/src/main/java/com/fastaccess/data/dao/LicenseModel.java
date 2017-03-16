package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 01 Jan 2017, 1:15 PM
 */

@Getter @Setter @NoArgsConstructor
public class LicenseModel implements Parcelable {
    String key;
    String name;
    String spdxId;
    String url;
    boolean featured;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.name);
        dest.writeString(this.spdxId);
        dest.writeString(this.url);
        dest.writeByte(this.featured ? (byte) 1 : (byte) 0);
    }

    protected LicenseModel(Parcel in) {
        this.key = in.readString();
        this.name = in.readString();
        this.spdxId = in.readString();
        this.url = in.readString();
        this.featured = in.readByte() != 0;
    }

    public static final Creator<LicenseModel> CREATOR = new Creator<LicenseModel>() {
        @Override public LicenseModel createFromParcel(Parcel source) {return new LicenseModel(source);}

        @Override public LicenseModel[] newArray(int size) {return new LicenseModel[size];}
    };
}
