package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 12 Mar 2017, 3:16 AM
 */

@Getter @Setter @NoArgsConstructor
public class AuthModel implements Parcelable {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private List<String> scopes;
    private String state;
    private String note;
    private String noteUrl;
    @SerializedName("X-GitHub-OTP") private String otpCode;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.clientId);
        dest.writeString(this.clientSecret);
        dest.writeString(this.redirectUri);
        dest.writeStringList(this.scopes);
        dest.writeString(this.state);
        dest.writeString(this.note);
        dest.writeString(this.noteUrl);
        dest.writeString(this.otpCode);
    }

    private AuthModel(Parcel in) {
        this.clientId = in.readString();
        this.clientSecret = in.readString();
        this.redirectUri = in.readString();
        this.scopes = in.createStringArrayList();
        this.state = in.readString();
        this.note = in.readString();
        this.noteUrl = in.readString();
        this.otpCode = in.readString();
    }

    public static final Creator<AuthModel> CREATOR = new Creator<AuthModel>() {
        @Override public AuthModel createFromParcel(Parcel source) {return new AuthModel(source);}

        @Override public AuthModel[] newArray(int size) {return new AuthModel[size];}
    };
}
