package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.data.dao.types.EventsType;
import com.google.gson.annotations.SerializedName;
import com.siimkinks.sqlitemagic.Delete;
import com.siimkinks.sqlitemagic.EventsModelTable;
import com.siimkinks.sqlitemagic.Select;
import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Id;
import com.siimkinks.sqlitemagic.annotation.Table;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rx.Completable;
import rx.Observable;

/**
 * Created by Kosh on 08 Feb 2017, 10:02 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class EventsModel implements Parcelable {
    @Id(autoIncrement = false) @Column long id;
    @Column EventsType type;
    @Column UserModel actor;
    @Column RepoModel repo;
    @Column PayloadModel payload;
    @Column Date createdAt;
    @SerializedName("public") @Column boolean publicEvent;

    public static Completable save(@NonNull List<EventsModel> events) {
        return Delete.from(EventsModelTable.EVENTS_MODEL)
                .observe()
                .toCompletable()
                .andThen(persist(events).observe());
    }

    @NonNull public static Observable<List<EventsModel>> getEvents() {
        return Select.from(EventsModelTable.EVENTS_MODEL)
                .orderBy(EventsModelTable.EVENTS_MODEL.CREATED_AT.desc())
                .queryDeep()
                .observe()
                .runQuery();
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeParcelable(this.actor, flags);
        dest.writeParcelable(this.repo, flags);
        dest.writeParcelable(this.payload, flags);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeByte(this.publicEvent ? (byte) 1 : (byte) 0);
    }

    protected EventsModel(Parcel in) {
        this.id = in.readLong();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : EventsType.values()[tmpType];
        this.actor = in.readParcelable(UserModel.class.getClassLoader());
        this.repo = in.readParcelable(RepoModel.class.getClassLoader());
        this.payload = in.readParcelable(PayloadModel.class.getClassLoader());
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.publicEvent = in.readByte() != 0;
    }

    public static final Creator<EventsModel> CREATOR = new Creator<EventsModel>() {
        @Override public EventsModel createFromParcel(Parcel source) {return new EventsModel(source);}

        @Override public EventsModel[] newArray(int size) {return new EventsModel[size];}
    };
}
