package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.User;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 31 Dec 2016, 1:28 PM
 */

@Getter @Setter public class ReleasesAssetsModel implements Parcelable {
    private String url;
    private String browserDownloadUrl;
    private long id;
    private String name;
    private String label;
    private String state;
    private String contentType;
    private int size;
    private int downloadCount;
    private Date createdAt;
    private Date updatedAt;
    private User uploader;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.browserDownloadUrl);
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.label);
        dest.writeString(this.state);
        dest.writeString(this.contentType);
        dest.writeInt(this.size);
        dest.writeInt(this.downloadCount);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeParcelable(this.uploader, flags);
    }

    public ReleasesAssetsModel() {}

    @SuppressWarnings("WeakerAccess") protected ReleasesAssetsModel(Parcel in) {
        this.url = in.readString();
        this.browserDownloadUrl = in.readString();
        this.id = in.readLong();
        this.name = in.readString();
        this.label = in.readString();
        this.state = in.readString();
        this.contentType = in.readString();
        this.size = in.readInt();
        this.downloadCount = in.readInt();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.uploader = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<ReleasesAssetsModel> CREATOR = new Creator<ReleasesAssetsModel>() {
        @Override public ReleasesAssetsModel createFromParcel(Parcel source) {return new ReleasesAssetsModel(source);}

        @Override public ReleasesAssetsModel[] newArray(int size) {return new ReleasesAssetsModel[size];}
    };
}
