package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.data.dao.types.IssueState;
import com.siimkinks.sqlitemagic.Delete;
import com.siimkinks.sqlitemagic.IssueModelTable;
import com.siimkinks.sqlitemagic.Select;
import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Id;
import com.siimkinks.sqlitemagic.annotation.Table;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rx.Completable;
import rx.Observable;

/**
 * Created by Kosh on 08 Dec 2016, 9:02 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class IssueModel implements Parcelable {
    @Column String url;
    @Column String body;
    @Column String title;
    @Column @Id(autoIncrement = false) long id;
    @Column int comments;
    @Column int number;
    @Column boolean locked;
    @Column IssueState state;
    @Column UserModel user;
    @Column UserModel assignee;
    @Column UsersListModel assignees;
    @Column LabelListModel labels;
    @Column MilestoneModel milestone;
    @Column(handleRecursively = false) RepoModel repository;
    @Column String repoUrl;
    @Column String bodyHtml;
    @Column String htmlUrl;
    @Column(handleRecursively = false) PullRequestModel pullRequest;
    @Column Date closedAt;
    @Column Date createdAt;
    @Column Date updatedAt;
    @Column UserModel closedBy;
    @Column String repoId;
    @Column String login;

    public Completable save() {
        return persist().observe().toCompletable();
    }

    public static Completable save(@NonNull List<IssueModel> models, @NonNull String repoId, @NonNull String login) {
        return Delete.from(IssueModelTable.ISSUE_MODEL)
                .where(IssueModelTable.ISSUE_MODEL.REPO_ID.is(repoId))
                .observe()
                .toCompletable()
                .andThen(Observable.from(models)
                        .map(issueModel -> {
                            issueModel.setRepoId(repoId);
                            issueModel.setLogin(login);
                            return issueModel.save();
                        }))
                .toCompletable();
    }

    public static Observable<List<IssueModel>> getIssues(@NonNull String repoId, @NonNull String login, @NonNull IssueState issueState) {
        return Select.from(IssueModelTable.ISSUE_MODEL)
                .where(IssueModelTable.ISSUE_MODEL.REPO_ID.is(repoId)
                        .and(IssueModelTable.ISSUE_MODEL.LOGIN.is(login))
                        .and(IssueModelTable.ISSUE_MODEL.STATE.is(issueState)))
                .orderBy(IssueModelTable.ISSUE_MODEL.UPDATED_AT.desc())
                .queryDeep()
                .observe()
                .runQuery();
    }

    public static Observable<IssueModel> getIssue(long id) {
        return Select.from(IssueModelTable.ISSUE_MODEL)
                .where(IssueModelTable.ISSUE_MODEL.ID.is(id))
                .queryDeep()
                .takeFirst()
                .observe()
                .runQuery();
    }

    public static Observable<IssueModel> getIssueByNumber(int number) {
        return Select.from(IssueModelTable.ISSUE_MODEL)
                .where(IssueModelTable.ISSUE_MODEL.NUMBER.is(number))
                .queryDeep()
                .takeFirst()
                .observe()
                .runQuery();
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.body);
        dest.writeString(this.title);
        dest.writeLong(this.id);
        dest.writeInt(this.comments);
        dest.writeInt(this.number);
        dest.writeByte(this.locked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.assignee, flags);
        dest.writeList(this.assignees);
        dest.writeList(this.labels);
        dest.writeParcelable(this.milestone, flags);
        dest.writeParcelable(this.repository, flags);
        dest.writeString(this.repoUrl);
        dest.writeString(this.bodyHtml);
        dest.writeString(this.htmlUrl);
        dest.writeParcelable(this.pullRequest, flags);
        dest.writeLong(this.closedAt != null ? this.closedAt.getTime() : -1);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeParcelable(this.closedBy, flags);
        dest.writeString(this.repoId);
        dest.writeString(this.login);
    }

    protected IssueModel(Parcel in) {
        this.url = in.readString();
        this.body = in.readString();
        this.title = in.readString();
        this.id = in.readLong();
        this.comments = in.readInt();
        this.number = in.readInt();
        this.locked = in.readByte() != 0;
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : IssueState.values()[tmpState];
        this.user = in.readParcelable(UserModel.class.getClassLoader());
        this.assignee = in.readParcelable(UserModel.class.getClassLoader());
        this.assignees = new UsersListModel();
        in.readList(this.assignees, this.assignee.getClass().getClassLoader());
        this.labels = new LabelListModel();
        in.readList(this.labels, this.labels.getClass().getClassLoader());
        this.milestone = in.readParcelable(MilestoneModel.class.getClassLoader());
        this.repository = in.readParcelable(RepoModel.class.getClassLoader());
        this.repoUrl = in.readString();
        this.bodyHtml = in.readString();
        this.htmlUrl = in.readString();
        this.pullRequest = in.readParcelable(PullRequestModel.class.getClassLoader());
        long tmpClosedAt = in.readLong();
        this.closedAt = tmpClosedAt == -1 ? null : new Date(tmpClosedAt);
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.closedBy = in.readParcelable(UserModel.class.getClassLoader());
        this.repoId = in.readString();
        this.login = in.readString();
    }

    public static final Creator<IssueModel> CREATOR = new Creator<IssueModel>() {
        @Override public IssueModel createFromParcel(Parcel source) {return new IssueModel(source);}

        @Override public IssueModel[] newArray(int size) {return new IssueModel[size];}
    };

}
