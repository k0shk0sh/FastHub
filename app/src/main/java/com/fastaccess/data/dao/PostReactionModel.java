package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 29 Mar 2017, 9:50 PM
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor public class PostReactionModel implements Parcelable {

    private String content;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {dest.writeString(this.content);}

    private PostReactionModel(Parcel in) {this.content = in.readString();}

    public static final Parcelable.Creator<PostReactionModel> CREATOR = new Parcelable.Creator<PostReactionModel>() {
        @Override public PostReactionModel createFromParcel(Parcel source) {return new PostReactionModel(source);}

        @Override public PostReactionModel[] newArray(int size) {return new PostReactionModel[size];}
    };
}
