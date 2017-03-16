package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.App;
import com.fastaccess.data.dao.NotificationSubjectModel;
import com.fastaccess.data.dao.converters.NotificationSubjectConverter;
import com.fastaccess.data.dao.converters.RepoConverter;
import com.fastaccess.data.dao.types.NotificationReason;

import java.util.Date;
import java.util.List;

import io.requery.Convert;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import io.requery.rx.SingleEntityStore;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import rx.Completable;
import rx.Observable;

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

    public Completable save(Notification notification) {
        return App.getInstance().getDataStore().upsert(notification).toCompletable();
    }

    public static Completable save(@NonNull List<Notification> models) {
        SingleEntityStore<Persistable> dataSource = App.getInstance().getDataStore();
        return dataSource.delete(Notification.class)
                .get()
                .toSingle()
                .toCompletable()
                .andThen(dataSource.insert(models))
                .toCompletable();
    }

    public static Observable<List<Notification>> getNotifications() {
        return App.getInstance()
                .getDataStore()
                .select(Notification.class)
                .orderBy(Notification.UPDATED_AT.desc(),
                        Notification.UNREAD.eq(false).getLeftOperand())
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
    }
}
