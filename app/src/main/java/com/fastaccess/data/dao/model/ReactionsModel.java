package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 28 Mar 2017, 9:15 PM
 */

@Getter @Setter public class ReactionsModel implements Parcelable {

    private long id;
    private String url;
    private int total_count;
    @SerializedName("+1") private int plusOne; // FIXME check this code
    @SerializedName("-1") private int minusOne;
    private int laugh;
    private int hooray;
    private int confused;
    private int heart;

    public ReactionsModel() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.url);
        dest.writeInt(this.total_count);
        dest.writeInt(this.plusOne);
        dest.writeInt(this.minusOne);
        dest.writeInt(this.laugh);
        dest.writeInt(this.hooray);
        dest.writeInt(this.confused);
        dest.writeInt(this.heart);
    }

    protected ReactionsModel(Parcel in) {
        this.id = in.readLong();
        this.url = in.readString();
        this.total_count = in.readInt();
        this.plusOne = in.readInt();
        this.minusOne = in.readInt();
        this.laugh = in.readInt();
        this.hooray = in.readInt();
        this.confused = in.readInt();
        this.heart = in.readInt();
    }

    public static final Creator<ReactionsModel> CREATOR = new Creator<ReactionsModel>() {
        @Override public ReactionsModel createFromParcel(Parcel source) {return new ReactionsModel(source);}

        @Override public ReactionsModel[] newArray(int size) {return new ReactionsModel[size];}
    };
}
