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

import io.requery.Convert;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Nullable;
import io.requery.Persistable;
import io.requery.rx.SingleEntityStore;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import rx.Completable;
import rx.Observable;
import rx.Single;

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

    public Single<Notification> save(Notification notification) {
        return App.getInstance().getDataStore()
                .delete(Notification.class)
                .where(Notification.ID.eq(notification.getId()))
                .get()
                .toSingle()
                .flatMap(integer -> App.getInstance().getDataStore().insert(notification));
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
            return Observable.empty().toSingle();
        });
    }

    public static Observable<Notification> save(@NonNull List<Notification> models) {
        SingleEntityStore<Persistable> dataSource = App.getInstance().getDataStore();
        return RxHelper.safeObservable(dataSource.delete(Notification.class)
                .get()
                .toSingle()
                .toObservable()
                .flatMap(integer -> Observable.from(models)))
                .flatMap(notification -> notification.save(notification).toObservable());
    }

    public static Observable<List<Notification>> getUnreadNotifications() {
        return App.getInstance()
                .getDataStore()
                .select(Notification.class)
                .where(Notification.UNREAD.eq(true))
                .orderBy(Notification.UPDATED_AT.desc())
                .get()
                .toObservable()
                .toList();
    }

    public static Observable<List<Notification>> getAlltNotifications() {
        return App.getInstance()
                .getDataStore()
                .select(Notification.class)
                .orderBy(Notification.UPDATED_AT.desc(), Notification.UNREAD.eq(false).getLeftOperand())
                .get()
                .toObservable()
                .toList();
    }

    public static boolean hasUnreadNotifications() {
        return App.getInstance()
                .getDataStore()
                .count(Notification.class)
                .where(Notification.UNREAD.equal(true))
                .get()
                .toSingle()
                .toBlocking()
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
