package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.siimkinks.sqlitemagic.FileModelTable;
import com.siimkinks.sqlitemagic.Select;
import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rx.Completable;
import rx.Observable;

/**
 * Created by Kosh on 06 Dec 2016, 10:42 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class FileModel implements Parcelable {

    @Column boolean isMarkdown;
    @Column String content;
    @Column String fullUrl;
    @Column boolean isRepo;

    public Completable save() {
        return this.persist()
                .observe()
                .toCompletable();
    }

    public static Completable save(@NonNull FileModel model) {
        return model.save();
    }

    public static Observable<FileModel> get(@NonNull String url) {
        return Select.from(FileModelTable.FILE_MODEL)
                .where(FileModelTable.FILE_MODEL.FULL_URL.is(url))
                .takeFirst()
                .observe()
                .runQueryOnceOrDefault(null);
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isMarkdown ? (byte) 1 : (byte) 0);
        dest.writeString(this.content);
        dest.writeString(this.fullUrl);
        dest.writeByte(this.isRepo ? (byte) 1 : (byte) 0);
        dest.writeLong(this.id);
    }

    protected FileModel(Parcel in) {
        this.isMarkdown = in.readByte() != 0;
        this.content = in.readString();
        this.fullUrl = in.readString();
        this.isRepo = in.readByte() != 0;
        this.id = in.readLong();
    }

    public static final Creator<FileModel> CREATOR = new Creator<FileModel>() {
        @Override public FileModel createFromParcel(Parcel source) {return new FileModel(source);}

        @Override public FileModel[] newArray(int size) {return new FileModel[size];}
    };
}
