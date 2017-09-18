package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 15 Nov 2016, 7:04 PM
 */


@Getter @Setter @NoArgsConstructor
public class IssuesPageable<M> implements Parcelable {

    public int first;
    public int next;
    public int prev;
    public int last;
    public int totalCount;
    public boolean incompleteResults;
    public List<M> items;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.first);
        dest.writeInt(this.next);
        dest.writeInt(this.prev);
        dest.writeInt(this.last);
        dest.writeInt(this.totalCount);
        dest.writeByte(this.incompleteResults ? (byte) 1 : (byte) 0);
    }

    @SuppressWarnings("WeakerAccess") protected IssuesPageable(Parcel in) {
        this.first = in.readInt();
        this.next = in.readInt();
        this.prev = in.readInt();
        this.last = in.readInt();
        this.totalCount = in.readInt();
        this.incompleteResults = in.readByte() != 0;
    }

    public static final Creator<IssuesPageable> CREATOR = new Creator<IssuesPageable>() {
        @Override public IssuesPageable createFromParcel(Parcel source) {return new IssuesPageable(source);}

        @Override public IssuesPageable[] newArray(int size) {return new IssuesPageable[size];}
    };
}
