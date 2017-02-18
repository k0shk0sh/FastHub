package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 08 Dec 2016, 8:59 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class GitCommitModel implements Parcelable {
    @Column String sha;
    @Column String url;
    @Column String message;
    @Column UserModel author;
    @Column UserModel committer;
    @Column UserModel tree;
    @Column @SerializedName("distinct") boolean distincted;
    @Column GitCommitListModel parents;
    @Column int commentCount;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sha);
        dest.writeString(this.url);
        dest.writeString(this.message);
        dest.writeParcelable(this.author, flags);
        dest.writeParcelable(this.committer, flags);
        dest.writeParcelable(this.tree, flags);
        dest.writeByte(this.distincted ? (byte) 1 : (byte) 0);
        dest.writeList(this.parents);
        dest.writeInt(this.commentCount);
    }

    protected GitCommitModel(Parcel in) {
        this.sha = in.readString();
        this.url = in.readString();
        this.message = in.readString();
        this.author = in.readParcelable(UserModel.class.getClassLoader());
        this.committer = in.readParcelable(UserModel.class.getClassLoader());
        this.tree = in.readParcelable(UserModel.class.getClassLoader());
        this.distincted = in.readByte() != 0;
        in.readList(parents, parents.getClass().getClassLoader());
        in.readList(this.parents, GitCommitModel.class.getClassLoader());
        this.commentCount = in.readInt();
    }

    public static final Creator<GitCommitModel> CREATOR = new Creator<GitCommitModel>() {
        @Override public GitCommitModel createFromParcel(Parcel source) {return new GitCommitModel(source);}

        @Override public GitCommitModel[] newArray(int size) {return new GitCommitModel[size];}
    };
}
