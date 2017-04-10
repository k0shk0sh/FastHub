package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.ReviewStateType;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 10 Apr 2017, 4:26 PM
 */

@Getter @Setter public class ReviewModel implements Parcelable {

    private long id;
    private User user;
    private String body;
    private ReviewStateType state;
    private Date submittedAt;
    private String commitId;


    public ReviewModel() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.body);
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeLong(this.submittedAt != null ? this.submittedAt.getTime() : -1);
        dest.writeString(this.commitId);
    }

    protected ReviewModel(Parcel in) {
        this.id = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.body = in.readString();
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : ReviewStateType.values()[tmpState];
        long tmpSubmittedAt = in.readLong();
        this.submittedAt = tmpSubmittedAt == -1 ? null : new Date(tmpSubmittedAt);
        this.commitId = in.readString();
    }

    public static final Creator<ReviewModel> CREATOR = new Creator<ReviewModel>() {
        @Override public ReviewModel createFromParcel(Parcel source) {return new ReviewModel(source);}

        @Override public ReviewModel[] newArray(int size) {return new ReviewModel[size];}
    };
}
