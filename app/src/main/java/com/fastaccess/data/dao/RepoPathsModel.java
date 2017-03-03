package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 03 Mar 2017, 10:43 PM
 */

@Getter @Setter @NoArgsConstructor
public class RepoPathsModel implements Parcelable {
    private String path;
    private ArrayList<RepoFilesModel> files;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeTypedList(this.files);
    }

    protected RepoPathsModel(Parcel in) {
        this.path = in.readString();
        this.files = in.createTypedArrayList(RepoFilesModel.CREATOR);
    }

    public static final Parcelable.Creator<RepoPathsModel> CREATOR = new Parcelable.Creator<RepoPathsModel>() {
        @Override public RepoPathsModel createFromParcel(Parcel source) {return new RepoPathsModel(source);}

        @Override public RepoPathsModel[] newArray(int size) {return new RepoPathsModel[size];}
    };

    @Override public String toString() {
        return path;
    }
}
