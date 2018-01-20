package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.App;

import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.reactivex.Observable;
import io.requery.Column;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import lombok.NoArgsConstructor;

/**
 * Created by Kosh on 11.11.17.
 */
@Entity @NoArgsConstructor public class AbstractFastHubNotification implements Parcelable {

    public enum NotificationType {
        UPDATE, GUIDE, PURCHASE, REPORT_ISSUE, PROMOTION, STAR_REPO
    }

    @Generated @Key long id;
    @io.requery.Nullable @Column(name = "notification_date") Date date;
    @io.requery.Nullable boolean read;
    @io.requery.Nullable String body;
    @io.requery.Nullable String title;
    @io.requery.Nullable NotificationType type;

    public static void update(@Nonnull FastHubNotification notification) {
        App.getInstance().getDataStore().toBlocking().update(notification);
    }

    public static void save(@Nonnull FastHubNotification notification) {
        App.getInstance().getDataStore().toBlocking().insert(notification);
    }

    @Nullable public static FastHubNotification getLatest() {
        return App.getInstance().getDataStore().toBlocking()
                .select(FastHubNotification.class)
                .where(FastHubNotification.READ.eq(false))
                .orderBy(FastHubNotification.DATE.desc())
                .limit(1)
                .get()
                .firstOrNull();
    }

    @Nonnull public static Observable<FastHubNotification> getNotifications() {
        return App.getInstance().getDataStore()
                .select(FastHubNotification.class)
                .orderBy(FastHubNotification.DATE.desc())
                .get()
                .observable();
    }

    public static boolean hasNotifications() {
        return App.getInstance().getDataStore()
                .count(FastHubNotification.class)
                .get()
                .value() > 0;
    }

    @Override public String toString() {
        return "AbstractFastHubNotification{" +
                "date=" + date +
                ", isRead=" + read +
                ", body='" + body + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                '}';
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeByte(this.read ? (byte) 1 : (byte) 0);
        dest.writeString(this.body);
        dest.writeString(this.title);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    protected AbstractFastHubNotification(Parcel in) {
        this.id = in.readLong();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.read = in.readByte() != 0;
        this.body = in.readString();
        this.title = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : NotificationType.values()[tmpType];
    }

    public static final Creator<FastHubNotification> CREATOR = new Creator<FastHubNotification>() {
        @Override public FastHubNotification createFromParcel(Parcel source) {return new FastHubNotification(source);}

        @Override public FastHubNotification[] newArray(int size) {return new FastHubNotification[size];}
    };
}
