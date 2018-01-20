package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 01 Apr 2017, 12:42 PM
 */
@Getter @Setter public class CommitCountModel implements Parcelable {

    private List<Integer> all;
    private List<Integer> owner;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.all);
        dest.writeList(this.owner);
    }

    public CommitCountModel() {}

    private CommitCountModel(Parcel in) {
        this.all = new ArrayList<Integer>();
        in.readList(this.all, Integer.class.getClassLoader());
        this.owner = new ArrayList<Integer>();
        in.readList(this.owner, Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<CommitCountModel> CREATOR = new Parcelable.Creator<CommitCountModel>() {
        @Override public CommitCountModel createFromParcel(Parcel source) {return new CommitCountModel(source);}

        @Override public CommitCountModel[] newArray(int size) {return new CommitCountModel[size];}
    };
}
