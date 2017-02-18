package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.siimkinks.sqlitemagic.CommentsModelTable;
import com.siimkinks.sqlitemagic.Delete;
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
 * Created by Kosh on 20 Nov 2016, 10:34 AM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class CommentsModel implements Parcelable {

    @Id(autoIncrement = false) @Column long id;
    @Column(handleRecursively = false, onDeleteCascade = true) UserModel user;
    @Column String url;
    @Column String body;
    @Column String bodyHtml;
    @Column String htmlUrl;
    @Column Date createdAt;
    @Column Date updatedAt;
    @Column int position;
    @Column int line;
    @Column String path;
    @Column String commitId;
    @Column String repoId;
    @Column String login;
    @Column String gistId;
    @Column String issueId;
    @Column String pullRequestId;

    public Completable save() {
        return persist().observe().toCompletable();
    }

    public static Completable saveForGist(@NonNull List<CommentsModel> models, @NonNull String gistId) {
        return Delete.from(CommentsModelTable.COMMENTS_MODEL)
                .where(CommentsModelTable.COMMENTS_MODEL.GIST_ID.is(gistId))
                .observe()
                .toCompletable()
                .andThen(Observable.from(models)
                        .map(model -> {
                            model.setGistId(gistId);
                            return model.save();
                        }))
                .toCompletable();
    }

    public static Completable saveForCommits(@NonNull List<CommentsModel> models, @NonNull String repoId, @NonNull String login, @NonNull String
            commitId) {
        return Delete.from(CommentsModelTable.COMMENTS_MODEL)
                .where(CommentsModelTable.COMMENTS_MODEL.COMMIT_ID.is(commitId)
                        .and(CommentsModelTable.COMMENTS_MODEL.REPO_ID.is(repoId))
                        .and(CommentsModelTable.COMMENTS_MODEL.LOGIN.is(login)))
                .observe()
                .toCompletable()
                .andThen(Observable.from(models)
                        .map(model -> {
                            model.setLogin(login);
                            model.setRepoId(repoId);
                            model.setCommitId(commitId);
                            return model.save();
                        }))
                .toCompletable();
    }

    public static Completable saveForIssues(@NonNull List<CommentsModel> models, @NonNull String repoId, @NonNull String login, @NonNull String
            issueId) {
        return Delete.from(CommentsModelTable.COMMENTS_MODEL)
                .where(CommentsModelTable.COMMENTS_MODEL.ISSUE_ID.is(issueId)
                        .and(CommentsModelTable.COMMENTS_MODEL.REPO_ID.is(repoId))
                        .and(CommentsModelTable.COMMENTS_MODEL.LOGIN.is(login)))
                .observe()
                .toCompletable()
                .andThen(Observable.from(models)
                        .map(model -> {
                            model.setLogin(login);
                            model.setRepoId(repoId);
                            model.setIssueId(issueId);
                            return model.save();
                        }))
                .toCompletable();
    }

    public static Completable saveForPullRequest(@NonNull List<CommentsModel> models, @NonNull String repoId, @NonNull String login, @NonNull String
            pullRequestId) {
        return Delete.from(CommentsModelTable.COMMENTS_MODEL)
                .where(CommentsModelTable.COMMENTS_MODEL.PULL_REQUEST_ID.is(pullRequestId)
                        .and(CommentsModelTable.COMMENTS_MODEL.REPO_ID.is(repoId))
                        .and(CommentsModelTable.COMMENTS_MODEL.LOGIN.is(login)))
                .observe()
                .toCompletable()
                .andThen(Observable.from(models)
                        .map(model -> {
                            model.setLogin(login);
                            model.setRepoId(repoId);
                            model.setPullRequestId(pullRequestId);
                            return model.save();
                        }))
                .toCompletable();
    }

    public static Observable<List<CommentsModel>> getGistComments(@NonNull String gistId) {
        return Select.from(CommentsModelTable.COMMENTS_MODEL)
                .where(CommentsModelTable.COMMENTS_MODEL.GIST_ID.is(gistId))
                .orderBy(CommentsModelTable.COMMENTS_MODEL.UPDATED_AT.desc())
                .observe()
                .runQuery();
    }

    public static Observable<List<CommentsModel>> getCommitComments(@NonNull String repoId, @NonNull String login, @NonNull String commitId) {
        return Select.from(CommentsModelTable.COMMENTS_MODEL)
                .where(CommentsModelTable.COMMENTS_MODEL.COMMIT_ID.is(commitId)
                        .and(CommentsModelTable.COMMENTS_MODEL.REPO_ID.is(repoId))
                        .and(CommentsModelTable.COMMENTS_MODEL.LOGIN.is(login)))
                .orderBy(CommentsModelTable.COMMENTS_MODEL.UPDATED_AT.desc())
                .observe()
                .runQuery();
    }

    public static Observable<List<CommentsModel>> getIssueComments(@NonNull String repoId, @NonNull String login, @NonNull String issueId) {
        return Select.from(CommentsModelTable.COMMENTS_MODEL)
                .where(CommentsModelTable.COMMENTS_MODEL.ISSUE_ID.is(issueId)
                        .and(CommentsModelTable.COMMENTS_MODEL.REPO_ID.is(repoId))
                        .and(CommentsModelTable.COMMENTS_MODEL.LOGIN.is(login)))
                .orderBy(CommentsModelTable.COMMENTS_MODEL.UPDATED_AT.desc())
                .observe()
                .runQuery();
    }

    public static Observable<List<CommentsModel>> getPullRequestComments(@NonNull String repoId, @NonNull String login, @NonNull String
            pullRequestId) {
        return Select.from(CommentsModelTable.COMMENTS_MODEL)
                .where(CommentsModelTable.COMMENTS_MODEL.PULL_REQUEST_ID.is(pullRequestId)
                        .and(CommentsModelTable.COMMENTS_MODEL.REPO_ID.is(repoId))
                        .and(CommentsModelTable.COMMENTS_MODEL.LOGIN.is(login)))
                .orderBy(CommentsModelTable.COMMENTS_MODEL.UPDATED_AT.desc())
                .observe()
                .runQuery();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommentsModel that = (CommentsModel) o;

        return id == that.id;

    }

    @Override public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.url);
        dest.writeString(this.body);
        dest.writeString(this.bodyHtml);
        dest.writeString(this.htmlUrl);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeInt(this.position);
        dest.writeInt(this.line);
        dest.writeString(this.path);
        dest.writeString(this.commitId);
        dest.writeString(this.repoId);
        dest.writeString(this.gistId);
        dest.writeString(this.issueId);
        dest.writeString(this.pullRequestId);
    }

    protected CommentsModel(Parcel in) {
        this.id = in.readLong();
        this.user = in.readParcelable(UserModel.class.getClassLoader());
        this.url = in.readString();
        this.body = in.readString();
        this.bodyHtml = in.readString();
        this.htmlUrl = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.position = in.readInt();
        this.line = in.readInt();
        this.path = in.readString();
        this.commitId = in.readString();
        this.repoId = in.readString();
        this.gistId = in.readString();
        this.issueId = in.readString();
        this.pullRequestId = in.readString();
    }

    public static final Creator<CommentsModel> CREATOR = new Creator<CommentsModel>() {
        @Override public CommentsModel createFromParcel(Parcel source) {return new CommentsModel(source);}

        @Override public CommentsModel[] newArray(int size) {return new CommentsModel[size];}
    };
}
