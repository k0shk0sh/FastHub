package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.types.IssueState;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 10 Dec 2016, 8:53 AM
 */

@Getter @Setter @NoArgsConstructor
public class IssueRequestModel implements Parcelable {

    private IssueState state;
    private String title;
    private String body;
    private Integer milestone;
    private String assignee;
    private List<String> labels;
    private String base;

    public static IssueRequestModel clone(@NonNull Issue issue, boolean toClose) {
        IssueRequestModel model = new IssueRequestModel();
        if (issue.getLabels() != null) {
            model.setLabels(Stream.of(issue.getLabels()).filter(value -> value.getName() != null)
                    .map(LabelModel::getName).collect(Collectors.toList()));
        }
        model.setAssignee(issue.getAssignee() != null ? issue.getAssignee().getLogin() : null);
        model.setBody(issue.getBody());
        model.setMilestone(issue.getMilestone() != null ? issue.getMilestone().getNumber() : null);
        model.setState(toClose ? issue.getState() == IssueState.closed ? IssueState.open : IssueState.closed : issue.getState());
        model.setTitle(issue.getTitle());
        return model;
    }

    public static IssueRequestModel clone(@NonNull PullRequest issue, boolean toClose) {
        IssueRequestModel model = new IssueRequestModel();
        if (issue.getLabels() != null) {
            model.setLabels(Stream.of(issue.getLabels()).filter(value -> value.getName() != null)
                    .map(LabelModel::getName).collect(Collectors.toList()));
        }
        model.setBase(issue.getBase() != null ? issue.getBase().getRef() : "master");
        model.setAssignee(issue.getAssignee() != null ? issue.getAssignee().getLogin() : null);
        model.setBody(issue.getBody());
        model.setMilestone(issue.getMilestone() != null ? issue.getMilestone().getNumber() : null);
        model.setState(toClose ? issue.getState() == IssueState.closed ? IssueState.open : IssueState.closed : issue.getState());
        model.setTitle(issue.getTitle());
        return model;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeString(this.title);
        dest.writeString(this.body);
        dest.writeValue(this.milestone);
        dest.writeString(this.assignee);
        dest.writeStringList(this.labels);
    }

    private IssueRequestModel(Parcel in) {
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : IssueState.values()[tmpState];
        this.title = in.readString();
        this.body = in.readString();
        this.milestone = (Integer) in.readValue(Integer.class.getClassLoader());
        this.assignee = in.readString();
        this.labels = in.createStringArrayList();
    }

    public static final Creator<IssueRequestModel> CREATOR = new Creator<IssueRequestModel>() {
        @Override public IssueRequestModel createFromParcel(Parcel source) {return new IssueRequestModel(source);}

        @Override public IssueRequestModel[] newArray(int size) {return new IssueRequestModel[size];}
    };
}
