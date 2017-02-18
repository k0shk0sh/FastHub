package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Id;
import com.siimkinks.sqlitemagic.annotation.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 01 Jan 2017, 1:15 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class LicenseModel implements Parcelable {
    @Id @Column long id;
    @Column String key;
    @Column String name;
    @Column String spdxId;
    @Column String url;
    @Column boolean featured;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.key);
        dest.writeString(this.name);
        dest.writeString(this.spdxId);
        dest.writeString(this.url);
        dest.writeByte(this.featured ? (byte) 1 : (byte) 0);
    }

    protected LicenseModel(Parcel in) {
        this.id = in.readLong();
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
