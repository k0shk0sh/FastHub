package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.User;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 08 Dec 2016, 8:59 PM
 */

@Getter @Setter @NoArgsConstructor
public class GitCommitModel implements Parcelable {

    public String sha;
    public String url;
    public String message;
    public User author;
    public User committer;
    public User tree;
    public @SerializedName("distinct") boolean distincted;
    public GitCommitListModel parents;
    public int commentCount;

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
        this.author = in.readParcelable(User.class.getClassLoader());
        this.committer = in.readParcelable(User.class.getClassLoader());
        this.tree = in.readParcelable(User.class.getClassLoader());
        this.distincted = in.readByte() != 0;
        this.parents = new GitCommitListModel();
        in.readList(this.parents, this.parents.getClass().getClassLoader());
        this.commentCount = in.readInt();
    }

    public static final Creator<GitCommitModel> CREATOR = new Creator<GitCommitModel>() {
        @Override public GitCommitModel createFromParcel(Parcel source) {return new GitCommitModel(source);}

        @Override public GitCommitModel[] newArray(int size) {return new GitCommitModel[size];}
    };

    @Override public String toString() {
	if (message != null) {
            return (sha != null && sha.length() > 7 ? sha.substring(0, 7) + " - " : "") + message.split(System.lineSeparator())[0];
        } else if (sha != null && sha.length() > 10) {
            return sha.substring(0, 10);
        }
        return "N/A";
    }
}
