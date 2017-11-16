package com.fastaccess.data.dao.timeline;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.Comment;

import java.util.List;

/**
 * Created by kosh on 15/08/2017.
 */

public class PullRequestCommitModel implements Parcelable {
    private String login;
    private String path;
    private int position;
    private String commitId;
    private List<Comment> comments;
    private int line;


    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public PullRequestCommitModel() {}

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.login);
        dest.writeString(this.path);
        dest.writeInt(this.position);
        dest.writeString(this.commitId);
        dest.writeTypedList(this.comments);
        dest.writeInt(this.line);
    }

    protected PullRequestCommitModel(Parcel in) {
        this.login = in.readString();
        this.path = in.readString();
        this.position = in.readInt();
        this.commitId = in.readString();
        this.comments = in.createTypedArrayList(Comment.CREATOR);
        this.line = in.readInt();
    }

    public static final Creator<PullRequestCommitModel> CREATOR = new Creator<PullRequestCommitModel>() {
        @Override public PullRequestCommitModel createFromParcel(Parcel source) {return new PullRequestCommitModel(source);}

        @Override public PullRequestCommitModel[] newArray(int size) {return new PullRequestCommitModel[size];}
    };
}
