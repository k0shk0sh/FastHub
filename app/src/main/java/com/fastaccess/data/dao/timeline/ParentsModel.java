package com.fastaccess.data.dao.timeline;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter public class ParentsModel implements Parcelable {
    private String sha;
    private String url;
    private String htmlUrl;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sha);
        dest.writeString(this.url);
        dest.writeString(this.htmlUrl);
    }

    public ParentsModel() {}

    private ParentsModel(Parcel in) {
        this.sha = in.readString();
        this.url = in.readString();
        this.htmlUrl = in.readString();
    }

    public static final Parcelable.Creator<ParentsModel> CREATOR = new Parcelable.Creator<ParentsModel>() {
        @Override public ParentsModel createFromParcel(Parcel source) {return new ParentsModel(source);}

        @Override public ParentsModel[] newArray(int size) {return new ParentsModel[size];}
    };
}