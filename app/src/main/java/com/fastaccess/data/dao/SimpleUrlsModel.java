package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 31 Dec 2016, 3:32 PM
 */


@Getter @Setter
public class SimpleUrlsModel implements Parcelable {

    public String item;
    public String url;

    public SimpleUrlsModel(String item, String url) {
        this.item = item;
        this.url = url;
    }

    @Override public String toString() {
        return item;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.item);
        dest.writeString(this.url);
    }

    @SuppressWarnings("WeakerAccess") protected SimpleUrlsModel(Parcel in) {
        this.item = in.readString();
        this.url = in.readString();
    }

    public static final Creator<SimpleUrlsModel> CREATOR = new Creator<SimpleUrlsModel>() {
        @Override public SimpleUrlsModel createFromParcel(Parcel source) {return new SimpleUrlsModel(source);}

        @Override public SimpleUrlsModel[] newArray(int size) {return new SimpleUrlsModel[size];}
    };
}
