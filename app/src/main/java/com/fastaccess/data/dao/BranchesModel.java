package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 03 Mar 2017, 9:08 PM
 */

@Getter @Setter @NoArgsConstructor
public class BranchesModel implements Parcelable {

    private String name;
    private CommitModel commit;
    @SerializedName("protected") private boolean protectedBranch;
    private String protectionUrl;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeParcelable(this.commit, flags);
        dest.writeByte(this.protectedBranch ? (byte) 1 : (byte) 0);
        dest.writeString(this.protectionUrl);
    }

    protected BranchesModel(Parcel in) {
        this.name = in.readString();
        this.commit = in.readParcelable(CommitModel.class.getClassLoader());
        this.protectedBranch = in.readByte() != 0;
        this.protectionUrl = in.readString();
    }

    public static final Parcelable.Creator<BranchesModel> CREATOR = new Parcelable.Creator<BranchesModel>() {
        @Override public BranchesModel createFromParcel(Parcel source) {return new BranchesModel(source);}

        @Override public BranchesModel[] newArray(int size) {return new BranchesModel[size];}
    };

    @Override public String toString() {
        return name;
    }
}
