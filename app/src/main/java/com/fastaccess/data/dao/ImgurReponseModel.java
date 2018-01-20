package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 15 Apr 2017, 8:09 PM
 */

@Getter @Setter public class ImgurReponseModel implements Parcelable {
    private boolean success;
    private int status;
    private ImgurImage data;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.success ? (byte) 1 : (byte) 0);
        dest.writeInt(this.status);
        dest.writeParcelable(this.data, flags);
    }

    public ImgurReponseModel() {}

    private ImgurReponseModel(Parcel in) {
        this.success = in.readByte() != 0;
        this.status = in.readInt();
        this.data = in.readParcelable(ImgurImage.class.getClassLoader());
    }

    public static final Parcelable.Creator<ImgurReponseModel> CREATOR = new Parcelable.Creator<ImgurReponseModel>() {
        @Override public ImgurReponseModel createFromParcel(Parcel source) {return new ImgurReponseModel(source);}

        @Override public ImgurReponseModel[] newArray(int size) {return new ImgurReponseModel[size];}
    };

    @Getter @Setter public static class ImgurImage implements Parcelable {
        private String title;
        private String description;
        private String link;

        public ImgurImage() {}

        @Override public int describeContents() { return 0; }

        @Override public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.title);
            dest.writeString(this.description);
            dest.writeString(this.link);
        }

        private ImgurImage(Parcel in) {
            this.title = in.readString();
            this.description = in.readString();
            this.link = in.readString();
        }

        public static final Creator<ImgurImage> CREATOR = new Creator<ImgurImage>() {
            @Override public ImgurImage createFromParcel(Parcel source) {return new ImgurImage(source);}

            @Override public ImgurImage[] newArray(int size) {return new ImgurImage[size];}
        };
    }
}
