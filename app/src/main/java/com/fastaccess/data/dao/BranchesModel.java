package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.Commit;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 03 Mar 2017, 9:08 PM
 */

@Getter @Setter @NoArgsConstructor
public class BranchesModel implements Parcelable {

    public String name;
    public Commit commit;
    @SerializedName("protected") public boolean protectedBranch;
    public String protectionUrl;
    public boolean isTag;

    @Override public String toString() {
        return name;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeParcelable(this.commit, flags);
        dest.writeByte(this.protectedBranch ? (byte) 1 : (byte) 0);
        dest.writeString(this.protectionUrl);
        dest.writeByte(this.isTag ? (byte) 1 : (byte) 0);
    }

    private BranchesModel(Parcel in) {
        this.name = in.readString();
        this.commit = in.readParcelable(Commit.class.getClassLoader());
        this.protectedBranch = in.readByte() != 0;
        this.protectionUrl = in.readString();
        this.isTag = in.readByte() != 0;
    }

    public static final Creator<BranchesModel> CREATOR = new Creator<BranchesModel>() {
        @Override public BranchesModel createFromParcel(Parcel source) {return new BranchesModel(source);}

        @Override public BranchesModel[] newArray(int size) {return new BranchesModel[size];}
    };
}
