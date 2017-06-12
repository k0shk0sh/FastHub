package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Commit;
import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.Release;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.data.dao.model.User;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Created by Kosh on 08 Feb 2017, 10:03 PM
 */


@Getter @Setter @NoArgsConstructor
public class PayloadModel implements Parcelable {

    public String action;
    public Repo forkee;
    public Issue issue;
    public PullRequest pullRequest;
    public String refType;
    public Comment comment;
    public User target;
    public User member;
    public TeamsModel team;
    public Comment commitComment;
    public String description;
    public ReleasesAssetsModel download;
    public Gist gist;
    public List<WikiModel> pages;
    public String before;
    public String head;
    public String ref;
    public int size;
    public List<GitCommitModel> commits;
    public User user;
    public Release release;
    public User blockedUser;
    public User organization;
    public User invitation;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.action);
        dest.writeParcelable(this.forkee, flags);
        dest.writeParcelable(this.issue, flags);
        dest.writeParcelable(this.pullRequest, flags);
        dest.writeString(this.refType);
        dest.writeParcelable(this.comment, flags);
        dest.writeParcelable(this.target, flags);
        dest.writeParcelable(this.member, flags);
        dest.writeParcelable(this.team, flags);
        dest.writeParcelable(this.commitComment, flags);
        dest.writeString(this.description);
        dest.writeParcelable(this.download, flags);
        dest.writeParcelable(this.gist, flags);
        dest.writeTypedList(this.pages);
        dest.writeString(this.before);
        dest.writeString(this.head);
        dest.writeString(this.ref);
        dest.writeInt(this.size);
        dest.writeTypedList(this.commits);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.release, flags);
        dest.writeParcelable(this.blockedUser, flags);
        dest.writeParcelable(this.organization, flags);
        dest.writeParcelable(this.invitation, flags);
    }

    protected PayloadModel(Parcel in) {
        this.action = in.readString();
        this.forkee = in.readParcelable(Repo.class.getClassLoader());
        this.issue = in.readParcelable(Issue.class.getClassLoader());
        this.pullRequest = in.readParcelable(PullRequest.class.getClassLoader());
        this.refType = in.readString();
        this.comment = in.readParcelable(Comment.class.getClassLoader());
        this.target = in.readParcelable(User.class.getClassLoader());
        this.member = in.readParcelable(User.class.getClassLoader());
        this.team = in.readParcelable(TeamsModel.class.getClassLoader());
        this.commitComment = in.readParcelable(Comment.class.getClassLoader());
        this.description = in.readString();
        this.download = in.readParcelable(ReleasesAssetsModel.class.getClassLoader());
        this.gist = in.readParcelable(Gist.class.getClassLoader());
        this.pages = in.createTypedArrayList(WikiModel.CREATOR);
        this.before = in.readString();
        this.head = in.readString();
        this.ref = in.readString();
        this.size = in.readInt();
        this.commits = in.createTypedArrayList(GitCommitModel.CREATOR);
        this.user = in.readParcelable(User.class.getClassLoader());
        this.release = in.readParcelable(Release.class.getClassLoader());
        this.blockedUser = in.readParcelable(User.class.getClassLoader());
        this.organization = in.readParcelable(User.class.getClassLoader());
        this.invitation = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<PayloadModel> CREATOR = new Creator<PayloadModel>() {
        @Override public PayloadModel createFromParcel(Parcel source) {return new PayloadModel(source);}

        @Override public PayloadModel[] newArray(int size) {return new PayloadModel[size];}
    };
}
