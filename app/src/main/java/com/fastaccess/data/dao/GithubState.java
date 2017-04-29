package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 08 Dec 2016, 8:57 PM
 */

@Getter @Setter @NoArgsConstructor
public class GithubState implements Parcelable {
    private int additions;
    private int deletions;
    private int total;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.additions);
        dest.writeInt(this.deletions);
        dest.writeInt(this.total);
    }

    protected GithubState(Parcel in) {
        this.additions = in.readInt();
        this.deletions = in.readInt();
        this.total = in.readInt();
    }

    public static final Creator<GithubState> CREATOR = new Creator<GithubState>() {
        @Override public GithubState createFromParcel(Parcel source) {return new GithubState(source);}

        @Override public GithubState[] newArray(int size) {return new GithubState[size];}
    };
}
