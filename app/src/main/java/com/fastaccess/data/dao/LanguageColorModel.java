package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Kosh on 27 May 2017, 9:47 PM
 */

@Getter @Setter @ToString public class LanguageColorModel implements Parcelable {
    public String color;
    public String url;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.color);
        dest.writeString(this.url);
    }

    public LanguageColorModel() {}

    private LanguageColorModel(Parcel in) {
        this.color = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<LanguageColorModel> CREATOR = new Parcelable.Creator<LanguageColorModel>() {
        @Override public LanguageColorModel createFromParcel(Parcel source) {return new LanguageColorModel(source);}

        @Override public LanguageColorModel[] newArray(int size) {return new LanguageColorModel[size];}
    };
}

