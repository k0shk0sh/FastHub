package com.fastaccess.data.dao.timeline;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.RenameModel;
import com.fastaccess.data.dao.TeamsModel;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.IssueEventType;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kosh on 25/07/2017.
 */

@NoArgsConstructor @Getter @Setter public class GenericEvent implements Parcelable {

    private long id;
    private String url;
    private String commitId;
    private String commitUrl;
    private String message;
    private String sha;
    private String htmlUrl;
    private Date createdAt;
    private User actor;
    private User requestedReviewer;
    private User reviewRequester;
    private User assigner;
    private User assignee;
    private User author;
    private User committer;
    private LabelModel label;
    private TeamsModel requestedTeam;
    private MilestoneModel milestone;
    private RenameModel rename;
    private SourceModel source;
    private Issue issue;
    private PullRequest pullRequest;
    private ParentsModel tree;
    private List<ParentsModel> parents;
    private IssueEventType event;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.url);
        dest.writeString(this.commitId);
        dest.writeString(this.commitUrl);
        dest.writeString(this.message);
        dest.writeString(this.sha);
        dest.writeString(this.htmlUrl);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeParcelable(this.actor, flags);
        dest.writeParcelable(this.requestedReviewer, flags);
        dest.writeParcelable(this.reviewRequester, flags);
        dest.writeParcelable(this.assigner, flags);
        dest.writeParcelable(this.assignee, flags);
        dest.writeParcelable(this.author, flags);
        dest.writeParcelable(this.committer, flags);
        dest.writeParcelable(this.label, flags);
        dest.writeParcelable(this.requestedTeam, flags);
        dest.writeParcelable(this.milestone, flags);
        dest.writeParcelable(this.rename, flags);
        dest.writeParcelable(this.source, flags);
        dest.writeParcelable(this.issue, flags);
        dest.writeParcelable(this.pullRequest, flags);
        dest.writeParcelable(this.tree, flags);
        dest.writeTypedList(this.parents);
        dest.writeInt(this.event == null ? -1 : this.event.ordinal());
    }

    private GenericEvent(Parcel in) {
        this.id = in.readLong();
        this.url = in.readString();
        this.commitId = in.readString();
        this.commitUrl = in.readString();
        this.message = in.readString();
        this.sha = in.readString();
        this.htmlUrl = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.actor = in.readParcelable(User.class.getClassLoader());
        this.requestedReviewer = in.readParcelable(User.class.getClassLoader());
        this.reviewRequester = in.readParcelable(User.class.getClassLoader());
        this.assigner = in.readParcelable(User.class.getClassLoader());
        this.assignee = in.readParcelable(User.class.getClassLoader());
        this.author = in.readParcelable(User.class.getClassLoader());
        this.committer = in.readParcelable(User.class.getClassLoader());
        this.label = in.readParcelable(LabelModel.class.getClassLoader());
        this.requestedTeam = in.readParcelable(TeamsModel.class.getClassLoader());
        this.milestone = in.readParcelable(MilestoneModel.class.getClassLoader());
        this.rename = in.readParcelable(RenameModel.class.getClassLoader());
        this.source = in.readParcelable(SourceModel.class.getClassLoader());
        this.issue = in.readParcelable(Issue.class.getClassLoader());
        this.pullRequest = in.readParcelable(PullRequest.class.getClassLoader());
        this.tree = in.readParcelable(ParentsModel.class.getClassLoader());
        this.parents = in.createTypedArrayList(ParentsModel.CREATOR);
        int tmpEvent = in.readInt();
        this.event = tmpEvent == -1 ? null : IssueEventType.values()[tmpEvent];
    }

    public static final Creator<GenericEvent> CREATOR = new Creator<GenericEvent>() {
        @Override public GenericEvent createFromParcel(Parcel source) {return new GenericEvent(source);}

        @Override public GenericEvent[] newArray(int size) {return new GenericEvent[size];}
    };
}
