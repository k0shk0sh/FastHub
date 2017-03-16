package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 08 Dec 2016, 9:05 PM
 */

@Getter @Setter @NoArgsConstructor
public class LabelModel implements Parcelable {
    String url;
    String name;
    String color;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.name);
        dest.writeString(this.color);
    }

    protected LabelModel(Parcel in) {
        this.url = in.readString();
        this.name = in.readString();
        this.color = in.readString();
    }

    public static final Creator<LabelModel> CREATOR = new Creator<LabelModel>() {
        @Override public LabelModel createFromParcel(Parcel source) {return new LabelModel(source);}

        @Override public LabelModel[] newArray(int size) {return new LabelModel[size];}
    };
}
