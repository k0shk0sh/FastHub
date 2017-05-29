package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.App;
import com.fastaccess.helper.RxHelper;

import io.requery.Column;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import lombok.NoArgsConstructor;
import io.reactivex.Observable;import io.reactivex.Single;

/**
 * Created by Kosh on 06 Dec 2016, 10:42 PM
 */

@Entity @NoArgsConstructor public abstract class AbstractViewerFile implements Parcelable {
    @Key @Generated long id;
    boolean markdown;
    String content;
    @Column(unique = true) String fullUrl;
    boolean repo;

    public Single<ViewerFile> save(ViewerFile modelEntity) {
        return RxHelper.getSingle(App.getInstance().getDataStore()
                .delete(ViewerFile.class)
                .where(ViewerFile.FULL_URL.eq(modelEntity.getFullUrl()))
                .get()
                .single()
                .flatMap(i -> App.getInstance().getDataStore().insert(modelEntity)));
    }

    public static Observable<ViewerFile> get(@NonNull String url) {
        return App.getInstance()
                .getDataStore()
                .select(ViewerFile.class)
                .where(ViewerFile.FULL_URL.equal(url))
                .get()
                .observable();
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

    public static final Creator<ViewerFile> CREATOR = new Creator<ViewerFile>() {
        @Override public ViewerFile createFromParcel(Parcel source) {return new ViewerFile(source);}

        @Override public ViewerFile[] newArray(int size) {return new ViewerFile[size];}
    };
}
