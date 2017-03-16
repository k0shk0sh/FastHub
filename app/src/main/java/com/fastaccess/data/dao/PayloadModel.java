package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.Repo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Created by Kosh on 08 Feb 2017, 10:03 PM
 */


@Getter @Setter @NoArgsConstructor
public class PayloadModel implements Parcelable {

    String action;
    Repo forkee;
    Issue issue;
    PullRequest pullRequest;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.action);
        dest.writeParcelable(this.forkee, flags);
        dest.writeParcelable(this.issue, flags);
        dest.writeParcelable(this.pullRequest, flags);
    }

    protected PayloadModel(Parcel in) {
        this.action = in.readString();
        this.forkee = in.readParcelable(Repo.class.getClassLoader());
        this.issue = in.readParcelable(Issue.class.getClassLoader());
        this.pullRequest = in.readParcelable(PullRequest.class.getClassLoader());
    }

    public static final Creator<PayloadModel> CREATOR = new Creator<PayloadModel>() {
        @Override public PayloadModel createFromParcel(Parcel source) {return new PayloadModel(source);}

        @Override public PayloadModel[] newArray(int size) {return new PayloadModel[size];}
    };
}
