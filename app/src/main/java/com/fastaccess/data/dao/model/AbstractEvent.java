package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.fastaccess.App;
import com.fastaccess.data.dao.PayloadModel;
import com.fastaccess.data.dao.converters.PayloadConverter;
import com.fastaccess.data.dao.converters.RepoConverter;
import com.fastaccess.data.dao.converters.UserConverter;
import com.fastaccess.data.dao.types.EventsType;
import com.fastaccess.helper.RxHelper;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.requery.BlockingEntityStore;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Nullable;
import io.requery.Persistable;
import lombok.NoArgsConstructor;

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
    @Nullable String login;

    @NonNull
    public static Disposable save(@android.support.annotation.Nullable List<Event> events, @android.support.annotation.Nullable String user) {
        return RxHelper.getSingle(Single.fromPublisher(s -> {
            try {
                Login login = Login.getUser();
                if (login == null) {
                    s.onNext("");
                    s.onComplete();
                    return;
                }
                BlockingEntityStore<Persistable> dataSource = App.getInstance().getDataStore().toBlocking();
                dataSource.delete(Event.class)
                        .where(Event.LOGIN.isNull()
                                .or(Event.LOGIN.eq(login.getLogin())))
                        .get()
                        .value();
                if (events != null && !events.isEmpty() && TextUtils.equals(login.getLogin(), user)) {
                    for (Event event : events) {
                        dataSource.delete(Event.class).where(Event.ID.eq(event.getId())).get().value();
                        event.setLogin(login.getLogin());
                        dataSource.insert(event);
                    }
                }
                s.onNext("");
            } catch (Exception e) {
                s.onError(e);
            }
            s.onComplete();
        })).subscribe(o -> {/*donothing*/}, Throwable::printStackTrace);
    }

    @NonNull public static Single<List<Event>> getEvents(@NonNull String login) {
        return RxHelper.getSingle(
                App.getInstance().getDataStore()
                        .select(Event.class)
                        .where(Event.LOGIN.eq(login))
                        .orderBy(Event.CREATED_AT.desc())
                        .get()
                        .observable()
                        .toList());
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
