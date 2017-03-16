package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 19 Feb 2017, 6:11 PM
 */

@Getter @Setter @NoArgsConstructor
public class NotificationSubjectModel implements Parcelable {
    String title;
    String url;
    String type;
    String latestCommentUrl;

    @Override public String toString() {
        return "NotificationSubjectModel{" +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", latestCommentUrl='" + latestCommentUrl + '\'' +
                '}';
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeString(this.type);
        dest.writeString(this.latestCommentUrl);
    }

    protected NotificationSubjectModel(Parcel in) {
        this.title = in.readString();
        this.url = in.readString();
        this.type = in.readString();
        this.latestCommentUrl = in.readString();
    }

    public static final Creator<NotificationSubjectModel> CREATOR = new Creator<NotificationSubjectModel>() {
        @Override public NotificationSubjectModel createFromParcel(Parcel source) {return new NotificationSubjectModel(source);}

        @Override public NotificationSubjectModel[] newArray(int size) {return new NotificationSubjectModel[size];}
    };
}
