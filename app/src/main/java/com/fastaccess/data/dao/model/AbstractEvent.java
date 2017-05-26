package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.App;
import com.fastaccess.data.dao.PayloadModel;
import com.fastaccess.data.dao.converters.PayloadConverter;
import com.fastaccess.data.dao.converters.RepoConverter;
import com.fastaccess.data.dao.converters.UserConverter;
import com.fastaccess.data.dao.types.EventsType;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import io.requery.Convert;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import io.requery.rx.SingleEntityStore;
import lombok.NoArgsConstructor;
import rx.Observable;
import rx.Single;

/**
 * Created by Kosh on 16 Mar 2017, 7:29 PM
 */

@Entity @NoArgsConstructor public abstract class AbstractEvent implements Parcelable {
    @Key long id;
    EventsType type;
    Date createdAt;
    @Convert(UserConverter.class) User actor;
    @Convert(RepoConverter.class) Repo repo;
    @Convert(PayloadConverter.class) PayloadModel payload;
    @SerializedName("public") boolean publicEvent;

    @NonNull public static Single save(@NonNull List<Event> events) {
        SingleEntityStore<Persistable> dataSource = App.getInstance().getDataStore();
        return dataSource.delete(Event.class)
                .get()
                .toSingle()
                .flatMap(i -> dataSource.insert(events));
    }

    @NonNull public static Observable<List<Event>> getEvents() {
        return App.getInstance().getDataStore()
                .select(Event.class)
                .orderBy(Event.CREATED_AT.desc())
                .get()
                .toObservable()
                .toList();
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeParcelable(this.actor, flags);
        dest.writeParcelable(this.repo, flags);
        dest.writeParcelable(this.payload, flags);
        dest.writeByte(this.publicEvent ? (byte) 1 : (byte) 0);
    }

    protected AbstractEvent(Parcel in) {
        this.id = in.readLong();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : EventsType.values()[tmpType];
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.actor = in.readParcelable(User.class.getClassLoader());
        this.repo = in.readParcelable(Repo.class.getClassLoader());
        this.payload = in.readParcelable(PayloadModel.class.getClassLoader());
        this.publicEvent = in.readByte() != 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override public Event createFromParcel(Parcel source) {return new Event(source);}

        @Override public Event[] newArray(int size) {return new Event[size];}
    };
}
