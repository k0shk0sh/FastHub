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
 * Created by Kosh on 03 Dec 2016, 11:12 AM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class RepoPermissionsModel implements Parcelable {
    @Id @Column long id;
    @Column boolean admin;
    @Column boolean push;
    @Column boolean pull;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeByte(this.admin ? (byte) 1 : (byte) 0);
        dest.writeByte(this.push ? (byte) 1 : (byte) 0);
        dest.writeByte(this.pull ? (byte) 1 : (byte) 0);
    }

    protected RepoPermissionsModel(Parcel in) {
        this.id = in.readLong();
        this.admin = in.readByte() != 0;
        this.push = in.readByte() != 0;
        this.pull = in.readByte() != 0;
    }

    public static final Creator<RepoPermissionsModel> CREATOR = new Creator<RepoPermissionsModel>() {
        @Override public RepoPermissionsModel createFromParcel(Parcel source) {return new RepoPermissionsModel(source);}

        @Override public RepoPermissionsModel[] newArray(int size) {return new RepoPermissionsModel[size];}
    };
}
