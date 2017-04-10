package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.types.StatusStateType;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 10 Apr 2017, 3:15 AM
 */

@Getter @Setter public class PullRequestStatusModel implements Parcelable {

    private StatusStateType state;
    private String sha;
    private int totalCount;
    private List<StatusesModel> statuses;
    private String commitUrl;
    private String url;

    public PullRequestStatusModel() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeString(this.sha);
        dest.writeInt(this.totalCount);
        dest.writeTypedList(this.statuses);
        dest.writeString(this.commitUrl);
        dest.writeString(this.url);
    }

    protected PullRequestStatusModel(Parcel in) {
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : StatusStateType.values()[tmpState];
        this.sha = in.readString();
        this.totalCount = in.readInt();
        this.statuses = in.createTypedArrayList(StatusesModel.CREATOR);
        this.commitUrl = in.readString();
        this.url = in.readString();
    }

    public static final Creator<PullRequestStatusModel> CREATOR = new Creator<PullRequestStatusModel>() {
        @Override public PullRequestStatusModel createFromParcel(Parcel source) {return new PullRequestStatusModel(source);}

        @Override public PullRequestStatusModel[] newArray(int size) {return new PullRequestStatusModel[size];}
    };
}
