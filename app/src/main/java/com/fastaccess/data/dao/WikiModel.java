package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 11 Jun 2017, 7:13 PM
 */

@Getter @Setter public class WikiModel implements Parcelable {
    public String pageName;
    public String title;
    public String summary;
    public String action;
    public String sha;

    public String htmlUrl;

    public WikiModel() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pageName);
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeString(this.action);
        dest.writeString(this.sha);
        dest.writeString(this.htmlUrl);
    }

    private WikiModel(Parcel in) {
        this.pageName = in.readString();
        this.title = in.readString();
        this.summary = in.readString();
        this.action = in.readString();
        this.sha = in.readString();
        this.htmlUrl = in.readString();
    }

    public static final Creator<WikiModel> CREATOR = new Creator<WikiModel>() {
        @Override public WikiModel createFromParcel(Parcel source) {return new WikiModel(source);}

        @Override public WikiModel[] newArray(int size) {return new WikiModel[size];}
    };
}
