package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Hashemsergani on 18.10.17.
 */

public class GitHubStatusModel implements Parcelable {

    private String status;
    private String body;
    private Date createdOn;

    public String getStatus() { return status;}

    public void setStatus(String status) { this.status = status;}

    public String getBody() { return body;}

    public void setBody(String body) { this.body = body;}

    public Date getCreatedOn() { return createdOn;}

    public void setCreatedOn(Date createdOn) { this.createdOn = createdOn;}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeString(this.body);
        dest.writeLong(this.createdOn != null ? this.createdOn.getTime() : -1);
    }

    public GitHubStatusModel() {}

    protected GitHubStatusModel(Parcel in) {
        this.status = in.readString();
        this.body = in.readString();
        long tmpCreatedOn = in.readLong();
        this.createdOn = tmpCreatedOn == -1 ? null : new Date(tmpCreatedOn);
    }

    public static final Parcelable.Creator<GitHubStatusModel> CREATOR = new Parcelable.Creator<GitHubStatusModel>() {
        @Override public GitHubStatusModel createFromParcel(Parcel source) {return new GitHubStatusModel(source);}

        @Override public GitHubStatusModel[] newArray(int size) {return new GitHubStatusModel[size];}
    };
}
