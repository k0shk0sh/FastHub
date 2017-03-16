package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.App;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import lombok.NoArgsConstructor;
import rx.Completable;
import rx.Observable;

/**
 * Created by Kosh on 06 Dec 2016, 10:42 PM
 */

@Entity @NoArgsConstructor public abstract class AbstractViewerFile implements Parcelable {
    @Key @Generated long id;
    boolean markdown;
    String content;
    String fullUrl;
    boolean repo;

    public Completable save(ViewerFile modelEntity) {
        return App.getInstance().getDataStore()
                .upsert(modelEntity)
                .toCompletable();
    }

    public static Observable<ViewerFile> get(@NonNull String url) {
        return App.getInstance()
                .getDataStore()
                .select(ViewerFile.class)
                .where(ViewerFile.FULL_URL.equal(url))
                .get()
                .toObservable();
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeByte(this.markdown ? (byte) 1 : (byte) 0);
        dest.writeString(this.content);
        dest.writeString(this.fullUrl);
        dest.writeByte(this.repo ? (byte) 1 : (byte) 0);
    }

    protected AbstractViewerFile(Parcel in) {
        this.id = in.readLong();
        this.markdown = in.readByte() != 0;
        this.content = in.readString();
        this.fullUrl = in.readString();
        this.repo = in.readByte() != 0;
    }
}
