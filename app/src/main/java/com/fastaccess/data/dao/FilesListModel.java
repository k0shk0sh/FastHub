package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.siimkinks.sqlitemagic.annotation.Table;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 12 Nov 2016, 11:09 AM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class FilesListModel implements Parcelable, Serializable {

    String filename;
    String type;
    String language;
    String rawUrl;
    long size;
    String content;
    boolean needFetching;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filename);
        dest.writeString(this.type);
        dest.writeString(this.language);
        dest.writeString(this.rawUrl);
        dest.writeLong(this.size);
        dest.writeString(this.content);
        dest.writeByte(this.needFetching ? (byte) 1 : (byte) 0);
        dest.writeLong(this.id);
    }

    protected FilesListModel(Parcel in) {
        this.filename = in.readString();
        this.type = in.readString();
        this.language = in.readString();
        this.rawUrl = in.readString();
        this.size = in.readLong();
        this.content = in.readString();
        this.needFetching = in.readByte() != 0;
        this.id = in.readLong();
    }

    public static final Creator<FilesListModel> CREATOR = new Creator<FilesListModel>() {
        @Override public FilesListModel createFromParcel(Parcel source) {return new FilesListModel(source);}

        @Override public FilesListModel[] newArray(int size) {return new FilesListModel[size];}
    };

    public long getId() {
        return id;
    }
}
