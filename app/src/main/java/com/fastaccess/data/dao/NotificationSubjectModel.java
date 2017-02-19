package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 19 Feb 2017, 6:11 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class NotificationSubjectModel implements Parcelable {
    @Column String title;
    @Column String url;
    @Column String type;
    @Column String latestCommentUrl;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeString(this.type);
        dest.writeString(this.latestCommentUrl);
        dest.writeLong(this.id);
    }

    protected NotificationSubjectModel(Parcel in) {
        this.title = in.readString();
        this.url = in.readString();
        this.type = in.readString();
        this.latestCommentUrl = in.readString();
        this.id = in.readLong();
    }

    public static final Parcelable.Creator<NotificationSubjectModel> CREATOR = new Parcelable.Creator<NotificationSubjectModel>() {
        @Override public NotificationSubjectModel createFromParcel(Parcel source) {return new NotificationSubjectModel(source);}

        @Override public NotificationSubjectModel[] newArray(int size) {return new NotificationSubjectModel[size];}
    };

    @Override public String toString() {
        return "NotificationSubjectModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", latestCommentUrl='" + latestCommentUrl + '\'' +
                '}';
    }
}
