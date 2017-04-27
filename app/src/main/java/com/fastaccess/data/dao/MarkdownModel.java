package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 18 Feb 2017, 7:20 PM
 */

@Getter @Setter @NoArgsConstructor
public class MarkdownModel implements Parcelable {
    private String text;
    private String mode = "gfm";
    private String context;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeString(this.mode);
        dest.writeString(this.context);
    }

    @SuppressWarnings("WeakerAccess") protected MarkdownModel(Parcel in) {
        this.text = in.readString();
        this.mode = in.readString();
        this.context = in.readString();
    }

    public static final Parcelable.Creator<MarkdownModel> CREATOR = new Parcelable.Creator<MarkdownModel>() {
        @Override public MarkdownModel createFromParcel(Parcel source) {return new MarkdownModel(source);}

        @Override public MarkdownModel[] newArray(int size) {return new MarkdownModel[size];}
    };
}
