package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.App;
import com.fastaccess.data.dao.NotificationSubjectModel;
import com.fastaccess.data.dao.converters.NotificationSubjectConverter;
import com.fastaccess.data.dao.converters.RepoConverter;
import com.fastaccess.data.dao.types.NotificationReason;
import com.fastaccess.helper.RxHelper;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Nullable;
import lombok.NoArgsConstructor;

/**
 * Created by Kosh on 16 Mar 2017, 7:37 PM
 */

@Entity @NoArgsConstructor public abstract class AbstractNotification implements Parcelable {
    @Key long id;
    @Convert(RepoConverter.class) Repo repository;
    @Convert(NotificationSubjectConverter.class) NotificationSubjectModel subject;
    NotificationReason reason;
    String url;
    boolean unread;
    Date updatedAt;
    Date lastReadAt;
    @Nullable boolean isSubscribed;

    public Single<Notification> save(Notification entity) {
        return RxHelper.getSingle(App.getInstance().getDataStore()
                .delete(Notification.class)
                .where(Notification.ID.eq(entity.getId()))
                .get()
                .single()
                .flatMap(integer -> App.getInstance().getDataStore().insert(entity)));
    }

    public static Completable markAsRead(long id) {
        return Completable.fromCallable(() -> {
            Notification notification = App.getInstance().getDataStore()
                    .select(Notification.class)
                    .where(Notification.ID.eq(id))
                    .get()
                    .firstOrNull();
            if (notification != null) {
                notification.setUnread(false);
                return notification.save(notification);
            }
            return "";
        });
    }

    public static Observable<Notification> save(@android.support.annotation.Nullable List<Notification> models) {
        if (models == null) {
            return Observable.empty();
        }
        return RxHelper.safeObservable(Observable.fromIterable(models)
                .flatMap(notification -> notification.save(notification).toObservable()));
    }

    public static Single<List<Notification>> getUnreadNotifications() {
        return App.getInstance()
                .getDataStore()
                .select(Notification.class)
                .where(Notification.UNREAD.eq(true))
                .orderBy(Notification.UPDATED_AT.desc())
                .get()
                .observable()
                .toList();
    }

    public static Single<List<Notification>> getAllNotifications() {
        return App.getInstance()
                .getDataStore()
                .select(Notification.class)
                .orderBy(Notification.UPDATED_AT.desc(), Notification.UNREAD.eq(false).getLeftOperand())
                .get()
                .observable()
                .toList();
    }

    public static boolean hasUnreadNotifications() {
        return App.getInstance()
                .getDataStore()
                .count(Notification.class)
                .where(Notification.UNREAD.equal(true))
                .limit(1)
                .get()
                .value() > 0;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return repository != null && that.repository != null && repository.getId() == that.repository.getId();
    }

    @Override public int hashCode() {
        return repository != null ? (int) repository.getId() : 0;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.repository, flags);
        dest.writeParcelable(this.subject, flags);
        dest.writeInt(this.reason == null ? -1 : this.reason.ordinal());
        dest.writeString(this.url);
        dest.writeByte(this.unread ? (byte) 1 : (byte) 0);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeLong(this.lastReadAt != null ? this.lastReadAt.getTime() : -1);
        dest.writeByte(this.isSubscribed ? (byte) 1 : (byte) 0);
    }

    protected AbstractNotification(Parcel in) {
        this.id = in.readLong();
        this.repository = in.readParcelable(Repo.class.getClassLoader());
        this.subject = in.readParcelable(NotificationSubjectModel.class.getClassLoader());
        int tmpReason = in.readInt();
        this.reason = tmpReason == -1 ? null : NotificationReason.values()[tmpReason];
        this.url = in.readString();
        this.unread = in.readByte() != 0;
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        long tmpLastReadAt = in.readLong();
        this.lastReadAt = tmpLastReadAt == -1 ? null : new Date(tmpLastReadAt);
        this.isSubscribed = in.readByte() != 0;
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override public Notification createFromParcel(Parcel source) {return new Notification(source);}

        @Override public Notification[] newArray(int size) {return new Notification[size];}
    };
}
