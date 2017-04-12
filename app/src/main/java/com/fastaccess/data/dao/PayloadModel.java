package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.data.dao.model.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Created by Kosh on 08 Feb 2017, 10:03 PM
 */


@Getter @Setter @NoArgsConstructor
public class PayloadModel implements Parcelable {

    private String action;
    private Repo forkee;
    private Issue issue;
    private PullRequest pullRequest;
    private String ref;
    private String refType;
    private Comment comment;
    private User target;
    private User member;
    private TeamsModel team;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.action);
        dest.writeParcelable(this.forkee, flags);
        dest.writeParcelable(this.issue, flags);
        dest.writeParcelable(this.pullRequest, flags);
        dest.writeString(this.ref);
        dest.writeString(this.refType);
        dest.writeParcelable(this.comment, flags);
        dest.writeParcelable(this.target, flags);
        dest.writeParcelable(this.member, flags);
        dest.writeParcelable(this.team, flags);
    }

    protected PayloadModel(Parcel in) {
        this.action = in.readString();
        this.forkee = in.readParcelable(Repo.class.getClassLoader());
        this.issue = in.readParcelable(Issue.class.getClassLoader());
        this.pullRequest = in.readParcelable(PullRequest.class.getClassLoader());
        this.ref = in.readString();
        this.refType = in.readString();
        this.comment = in.readParcelable(Comment.class.getClassLoader());
        this.target = in.readParcelable(User.class.getClassLoader());
        this.member = in.readParcelable(User.class.getClassLoader());
        this.team = in.readParcelable(TeamsModel.class.getClassLoader());
    }

    public static final Creator<PayloadModel> CREATOR = new Creator<PayloadModel>() {
        @Override public PayloadModel createFromParcel(Parcel source) {return new PayloadModel(source);}

        @Override public PayloadModel[] newArray(int size) {return new PayloadModel[size];}
    };
}
