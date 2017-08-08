package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.User;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 10 Apr 2017, 4:26 PM
 */

@Getter @Setter public class ReviewModel implements Parcelable {

    private long id;
    private User user;
    private String bodyHtml;
    private String state;
    private Date submittedAt;
    private String commitId;
    private String diffText;
    private List<ReviewCommentModel> comments;
    private ReactionsModel reactions;

    public ReviewModel() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.bodyHtml);
        dest.writeString(this.state);
        dest.writeLong(this.submittedAt != null ? this.submittedAt.getTime() : -1);
        dest.writeString(this.commitId);
        dest.writeString(this.diffText);
        dest.writeTypedList(this.comments);
        dest.writeParcelable(this.reactions, flags);
    }

    private ReviewModel(Parcel in) {
        this.id = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.bodyHtml = in.readString();
        this.state = in.readString();
        long tmpSubmittedAt = in.readLong();
        this.submittedAt = tmpSubmittedAt == -1 ? null : new Date(tmpSubmittedAt);
        this.commitId = in.readString();
        this.diffText = in.readString();
        this.comments = in.createTypedArrayList(ReviewCommentModel.CREATOR);
        this.reactions = in.readParcelable(ReactionsModel.class.getClassLoader());
    }

    public static final Creator<ReviewModel> CREATOR = new Creator<ReviewModel>() {
        @Override public ReviewModel createFromParcel(Parcel source) {return new ReviewModel(source);}

        @Override public ReviewModel[] newArray(int size) {return new ReviewModel[size];}
    };

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewModel that = (ReviewModel) o;
        return id == that.id;
    }

    @Override public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
