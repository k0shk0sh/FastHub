package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kosh on 01 Jan 2017, 9:00 PM
 */
public class CommitFileModel implements Parcelable {

    private String sha;
    private String filename;
    private String status;
    private int additions;
    private int deletions;
    private int changes;
    private String blobUrl;
    private String rawUrl;
    private String contentsUrl;
    private String patch;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sha);
        dest.writeString(this.filename);
        dest.writeString(this.status);
        dest.writeInt(this.additions);
        dest.writeInt(this.deletions);
        dest.writeInt(this.changes);
        dest.writeString(this.blobUrl);
        dest.writeString(this.rawUrl);
        dest.writeString(this.contentsUrl);
        dest.writeString(this.patch);
    }

    @SuppressWarnings("WeakerAccess") protected CommitFileModel(Parcel in) {
        this.sha = in.readString();
        this.filename = in.readString();
        this.status = in.readString();
        this.additions = in.readInt();
        this.deletions = in.readInt();
        this.changes = in.readInt();
        this.blobUrl = in.readString();
        this.rawUrl = in.readString();
        this.contentsUrl = in.readString();
        this.patch = in.readString();
    }

    public static final Creator<CommitFileModel> CREATOR = new Creator<CommitFileModel>() {
        @Override public CommitFileModel createFromParcel(Parcel source) {return new CommitFileModel(source);}

        @Override public CommitFileModel[] newArray(int size) {return new CommitFileModel[size];}
    };

    @Override public String toString() {
        return "CommitFileModel{" +
                "sha='" + sha + '\'' +
                ", filename='" + filename + '\'' +
                ", status='" + status + '\'' +
                ", additions=" + additions +
                ", deletions=" + deletions +
                ", changes=" + changes +
                ", blobUrl='" + blobUrl + '\'' +
                ", rawUrl='" + rawUrl + '\'' +
                ", contentsUrl='" + contentsUrl + '\'' +
                ", patch='" + patch + '\'' +
                '}';
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAdditions() {
        return additions;
    }

    public void setAdditions(int additions) {
        this.additions = additions;
    }

    public int getDeletions() {
        return deletions;
    }

    public void setDeletions(int deletions) {
        this.deletions = deletions;
    }

    public int getChanges() {
        return changes;
    }

    public void setChanges(int changes) {
        this.changes = changes;
    }

    public String getBlobUrl() {
        return blobUrl;
    }

    public void setBlobUrl(String blobUrl) {
        this.blobUrl = blobUrl;
    }

    public String getRawUrl() {
        return rawUrl;
    }

    public void setRawUrl(String rawUrl) {
        this.rawUrl = rawUrl;
    }

    public String getContentsUrl() {
        return contentsUrl;
    }

    public void setContentsUrl(String contentsUrl) {
        this.contentsUrl = contentsUrl;
    }

    public String getPatch() {
        return patch;
    }

    public void setPatch(String patch) {
        this.patch = patch;
    }
}
