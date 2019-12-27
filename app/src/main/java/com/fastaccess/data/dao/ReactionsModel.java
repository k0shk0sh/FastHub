package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.fastaccess.data.dao.model.User;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import github.PullRequestTimelineQuery;

/**
 * Created by Kosh on 28 Mar 2017, 9:15 PM
 */

@Getter @Setter public class ReactionsModel implements Parcelable {

    public long id;
    public String url;
    public int total_count;
    @SerializedName(value = "+1", alternate = "thumbs_up") public int plusOne;
    @SerializedName(value = "-1", alternate = "thumbs_down") public int minusOne;
    public int laugh;
    public int hooray;
    public int confused;
    public int heart;
    public int rocket;
    public int eyes;
    public String content;
    public User user;
    public boolean viewerHasReacted;
    public boolean isCallingApi;

    public ReactionsModel() {}

    @NotNull @Override public String toString() {
        return "ReactionsModel{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", total_count=" + total_count +
                ", plusOne=" + plusOne +
                ", minusOne=" + minusOne +
                ", laugh=" + laugh +
                ", hooray=" + hooray +
                ", confused=" + confused +
                ", heart=" + heart +
                ", rocket=" + rocket +
                ", eyes=" + eyes +
                '}';
    }

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
        dest.writeInt(this.rocket);
        dest.writeInt(this.eyes);
        dest.writeString(this.content);
        dest.writeParcelable(this.user, flags);
        dest.writeByte(this.isCallingApi ? (byte) 1 : (byte) 0);
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
        this.rocket = in.readInt();
        this.eyes = in.readInt();
        this.content = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.isCallingApi = in.readByte() != 0;
    }

    public static final Creator<ReactionsModel> CREATOR = new Creator<ReactionsModel>() {
        @Override public ReactionsModel createFromParcel(Parcel source) {return new ReactionsModel(source);}

        @Override public ReactionsModel[] newArray(int size) {return new ReactionsModel[size];}
    };
}
