package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 12 Nov 2016, 11:09 AM
 */

@Getter @Setter
public class FilesListModel implements Parcelable, Serializable {
    public String filename;
    public String type;
    public String rawUrl;
    public Long size;
    public String content;
    public Boolean needFetching;
    public String language;

    public FilesListModel() {
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filename);
        dest.writeString(this.type);
        dest.writeString(this.rawUrl);
        dest.writeValue(this.size);
        dest.writeString(this.content);
        dest.writeValue(this.needFetching);
        dest.writeString(this.language);
    }

    protected FilesListModel(Parcel in) {
        this.filename = in.readString();
        this.type = in.readString();
        this.rawUrl = in.readString();
        this.size = (Long) in.readValue(Long.class.getClassLoader());
        this.content = in.readString();
        this.needFetching = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.language = in.readString();
    }

    public static final Creator<FilesListModel> CREATOR = new Creator<FilesListModel>() {
        @Override public FilesListModel createFromParcel(Parcel source) {return new FilesListModel(source);}

        @Override public FilesListModel[] newArray(int size) {return new FilesListModel[size];}
    };
}
