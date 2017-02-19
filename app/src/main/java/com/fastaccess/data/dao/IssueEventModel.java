package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.data.dao.types.IssueEventType;
import com.siimkinks.sqlitemagic.Delete;
import com.siimkinks.sqlitemagic.IssueEventModelTable;
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
 * Created by Kosh on 10 Dec 2016, 3:34 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class IssueEventModel implements Parcelable {

    @Column @Id(autoIncrement = false) long id;
    @Column String url;
    @Column UserModel actor;
    @Column UserModel assigner;
    @Column UserModel assignee;
    @Column IssueEventType event;
    @Column MilestoneModel milestone;
    @Column RenameModel rename;
    @Column IssueModel source;
    @Column LabelModel label;
    @Column String commitId;
    @Column String commitUrl;
    @Column Date createdAt;
    @Column String issueId;
    @Column String repoId;
    @Column String login;

    public Completable save() {
        return this.persist().observe().toCompletable();
    }

    public static Completable save(@NonNull List<IssueEventModel> models, @NonNull String repoId, @NonNull String login, @NonNull String issueId) {
        return Delete.from(IssueEventModelTable.ISSUE_EVENT_MODEL)
                .where(IssueEventModelTable.ISSUE_EVENT_MODEL.LOGIN.is(login)
                        .and(IssueEventModelTable.ISSUE_EVENT_MODEL.REPO_ID.is(repoId))
                        .and(IssueEventModelTable.ISSUE_EVENT_MODEL.ISSUE_ID.is(issueId)))
                .observe()
                .toCompletable()
                .andThen(Observable.from(models)
                        .map(issueEventModel -> {
                            issueEventModel.setIssueId(issueId);
                            issueEventModel.setLogin(login);
                            issueEventModel.setRepoId(repoId);
                            return issueEventModel.save();
                        }))
                .toCompletable();
    }

    public static Observable<List<IssueEventModel>> get(@NonNull String repoId, @NonNull String login, @NonNull String issueId) {
        return Select.from(IssueEventModelTable.ISSUE_EVENT_MODEL)
                .where(IssueEventModelTable.ISSUE_EVENT_MODEL.LOGIN.is(login)
                        .and(IssueEventModelTable.ISSUE_EVENT_MODEL.REPO_ID.is(repoId))
                        .and(IssueEventModelTable.ISSUE_EVENT_MODEL.ISSUE_ID.is(issueId)))
                .orderBy(IssueEventModelTable.ISSUE_EVENT_MODEL.CREATED_AT.desc())
                .queryDeep()
                .observe()
                .runQuery();
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.url);
        dest.writeParcelable(this.actor, flags);
        dest.writeParcelable(this.assigner, flags);
        dest.writeParcelable(this.assignee, flags);
        dest.writeInt(this.event == null ? -1 : this.event.ordinal());
        dest.writeParcelable(this.milestone, flags);
        dest.writeParcelable(this.rename, flags);
        dest.writeParcelable(this.source, flags);
        dest.writeParcelable(this.label, flags);
        dest.writeString(this.commitId);
        dest.writeString(this.commitUrl);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
    }

    protected IssueEventModel(Parcel in) {
        this.id = in.readLong();
        this.url = in.readString();
        this.actor = in.readParcelable(UserModel.class.getClassLoader());
        this.assigner = in.readParcelable(UserModel.class.getClassLoader());
        this.assignee = in.readParcelable(UserModel.class.getClassLoader());
        int tmpEvent = in.readInt();
        this.event = tmpEvent == -1 ? null : IssueEventType.values()[tmpEvent];
        this.milestone = in.readParcelable(MilestoneModel.class.getClassLoader());
        this.rename = in.readParcelable(RenameModel.class.getClassLoader());
        this.source = in.readParcelable(IssueModel.class.getClassLoader());
        this.label = in.readParcelable(LabelModel.class.getClassLoader());
        this.commitId = in.readString();
        this.commitUrl = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
    }

    public static final Creator<IssueEventModel> CREATOR = new Creator<IssueEventModel>() {
        @Override public IssueEventModel createFromParcel(Parcel source) {return new IssueEventModel(source);}

        @Override public IssueEventModel[] newArray(int size) {return new IssueEventModel[size];}
    };
}
