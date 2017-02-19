package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.types.NotificationReason;
import com.siimkinks.sqlitemagic.Delete;
import com.siimkinks.sqlitemagic.NotificationThreadModelTable;
import com.siimkinks.sqlitemagic.Select;
import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Id;
import com.siimkinks.sqlitemagic.annotation.Table;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import rx.Completable;
import rx.Observable;

/**
 * Created by Kosh on 19 Feb 2017, 6:09 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class NotificationThreadModel implements Parcelable {

    @Column @Id(autoIncrement = false) long id;
    @Column(onDeleteCascade = true) RepoModel repository;
    @Column(onDeleteCascade = true) NotificationSubjectModel subject;
    @Column NotificationReason reason;
    @Column String url;
    @Column boolean unread;
    @Column Date updatedAt;
    @Column Date lastReadAt;

    public Completable save() {
        return persist().observe().toCompletable();
    }

    public static Completable save(@NonNull List<NotificationThreadModel> models) {
        return Delete.from(NotificationThreadModelTable.NOTIFICATION_THREAD_MODEL)
                .observe()
                .toCompletable()
                .andThen(persist(models).observe());
    }

    public static Observable<List<NotificationThreadModel>> getNotifications() {
        return Select.from(NotificationThreadModelTable.NOTIFICATION_THREAD_MODEL)
                .orderBy(NotificationThreadModelTable.NOTIFICATION_THREAD_MODEL.UPDATED_AT.desc(),
                        NotificationThreadModelTable.NOTIFICATION_THREAD_MODEL.UNREAD.is(false).desc())
                .observe()
                .runQuery();
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.repository, flags);
        dest.writeParcelable(this.subject, flags);
        dest.writeInt(this.reason == null ? -1 : this.reason.ordinal());
        dest.writeString(this.url);
        dest.writeValue(this.unread);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeLong(this.lastReadAt != null ? this.lastReadAt.getTime() : -1);
    }

    protected NotificationThreadModel(Parcel in) {
        this.id = in.readLong();
        this.repository = in.readParcelable(RepoModel.class.getClassLoader());
        this.subject = in.readParcelable(NotificationSubjectModel.class.getClassLoader());
        int tmpReason = in.readInt();
        this.reason = tmpReason == -1 ? null : NotificationReason.values()[tmpReason];
        this.url = in.readString();
        this.unread = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        long tmpLastReadAt = in.readLong();
        this.lastReadAt = tmpLastReadAt == -1 ? null : new Date(tmpLastReadAt);
    }

    public static final Creator<NotificationThreadModel> CREATOR = new Creator<NotificationThreadModel>() {
        @Override public NotificationThreadModel createFromParcel(Parcel source) {return new NotificationThreadModel(source);}

        @Override public NotificationThreadModel[] newArray(int size) {return new NotificationThreadModel[size];}
    };

    @Override public String toString() {
        return "NotificationThreadModel{" +
                "subject=" + subject +
                ", reason=" + reason +
                ", unread=" + unread +
                '}';
    }
}
