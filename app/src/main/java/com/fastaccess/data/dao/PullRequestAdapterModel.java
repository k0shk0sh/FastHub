package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.annimon.stream.Stream;
import com.fastaccess.data.dao.model.IssueEvent;
import com.fastaccess.data.dao.model.PullRequest;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 10 Dec 2016, 3:34 PM
 */

@Getter @Setter
public class PullRequestAdapterModel implements Parcelable {

    public static final int HEADER = 1;
    public static final int ROW = 2;
    private int type;

    private IssueEvent issueEvent;
    private PullRequest pullRequest;

    private PullRequestAdapterModel(int type, IssueEvent model) {
        this.type = type;
        this.issueEvent = model;
    }

    public PullRequestAdapterModel(int type, PullRequest pullRequest) {
        this.type = type;
        this.pullRequest = pullRequest;
    }

    public static ArrayList<PullRequestAdapterModel> addEvents(@Nullable List<IssueEvent> modelList) {
        ArrayList<PullRequestAdapterModel> models = new ArrayList<>();
        if (modelList == null || modelList.isEmpty()) return models;
        Stream.of(modelList).forEach(issueEventModel -> models.add(new PullRequestAdapterModel(ROW, issueEventModel)));
        return models;
    }

    public PullRequestAdapterModel() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeParcelable(this.issueEvent, flags);
        dest.writeParcelable(this.pullRequest, flags);
    }

    private PullRequestAdapterModel(Parcel in) {
        this.type = in.readInt();
        this.issueEvent = in.readParcelable(IssueEvent.class.getClassLoader());
        this.pullRequest = in.readParcelable(PullRequest.class.getClassLoader());
    }

    public static final Creator<PullRequestAdapterModel> CREATOR = new Creator<PullRequestAdapterModel>() {
        @Override public PullRequestAdapterModel createFromParcel(Parcel source) {return new PullRequestAdapterModel(source);}

        @Override public PullRequestAdapterModel[] newArray(int size) {return new PullRequestAdapterModel[size];}
    };
}
