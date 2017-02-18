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
 * Created by Kosh on 08 Feb 2017, 10:03 PM
 */


@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class PayloadModel implements Parcelable {

    @Id @Column long id;
    @Column String action;
    @Column(onDeleteCascade = true, handleRecursively = false) RepoModel forkee;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.action);
        dest.writeParcelable(this.forkee, flags);
    }

    protected PayloadModel(Parcel in) {
        this.id = in.readLong();
        this.action = in.readString();
        this.forkee = in.readParcelable(RepoModel.class.getClassLoader());
    }

    public static final Creator<PayloadModel> CREATOR = new Creator<PayloadModel>() {
        @Override public PayloadModel createFromParcel(Parcel source) {return new PayloadModel(source);}

        @Override public PayloadModel[] newArray(int size) {return new PayloadModel[size];}
    };
}
