package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 09 Nov 2016, 11:28 PM
 */


@Getter @Setter @NoArgsConstructor
public class AccessTokenModel implements Parcelable {
    private long id;
    private String token;
    private String hashedToken;
    private String accessToken;
    private String tokenType;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.token);
        dest.writeString(this.hashedToken);
        dest.writeString(this.accessToken);
        dest.writeString(this.tokenType);
    }

    private AccessTokenModel(Parcel in) {
        this.id = in.readLong();
        this.token = in.readString();
        this.hashedToken = in.readString();
        this.accessToken = in.readString();
        this.tokenType = in.readString();
    }

    public static final Creator<AccessTokenModel> CREATOR = new Creator<AccessTokenModel>() {
        @Override public AccessTokenModel createFromParcel(Parcel source) {return new AccessTokenModel(source);}

        @Override public AccessTokenModel[] newArray(int size) {return new AccessTokenModel[size];}
    };
}
