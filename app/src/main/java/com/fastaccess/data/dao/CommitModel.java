package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.siimkinks.sqlitemagic.CommitModelTable;
import com.siimkinks.sqlitemagic.Delete;
import com.siimkinks.sqlitemagic.Select;
import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Table;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rx.Completable;
import rx.Observable;

/**
 * Created by Kosh on 08 Dec 2016, 8:55 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class CommitModel implements Parcelable {
    @Column String url;
    @Column String ref;
    @Column RepoModel repo;
    @Column String sha;
    @Column @SerializedName("distincted") boolean distincted;
    @Column @SerializedName("commit") GitCommitModel gitCommit;
    @Column UserModel author;
    @Column UserModel committer;
    @Column UserModel user;
    @Column CommitListModel parents;
    @Column GithubState stats;
    @Column CommitFileListModel files;
    @Column String htmlUrl;
    @Column String login;
    @Column String repoId;
    @Column long pullRequestNumber;


    public Completable save() {
        return this.persist().observe()
                .toCompletable();
    }

    public static Completable save(@NonNull List<CommitModel> models, @NonNull String repoId, @NonNull String login) {
        return Delete.from(CommitModelTable.COMMIT_MODEL)
                .where(CommitModelTable.COMMIT_MODEL.REPO_ID.is(repoId))
                .observe()
                .toCompletable()
                .andThen(Observable.from(models)
                        .map(commitModel -> {
                            commitModel.setRepoId(repoId);
                            commitModel.setLogin(login);
                            return commitModel.save();
                        }))
                .toCompletable();
    }

    public static Completable save(@NonNull List<CommitModel> models, @NonNull String repoId, @NonNull String login, long number) {
        return Delete.from(CommitModelTable.COMMIT_MODEL)
                .where(CommitModelTable.COMMIT_MODEL.REPO_ID.is(repoId))
                .observe()
                .toCompletable()
                .andThen(Observable.from(models)
                        .map(commitModel -> {
                            commitModel.setRepoId(repoId);
                            commitModel.setLogin(login);
                            commitModel.setPullRequestNumber(number);
                            return commitModel.save();
                        }))
                .toCompletable();
    }

    public static Observable<List<CommitModel>> getCommits(@NonNull String repoId, @NonNull String login) {
        return Select.from(CommitModelTable.COMMIT_MODEL)
                .where(CommitModelTable.COMMIT_MODEL.REPO_ID.is(repoId)
                        .and(CommitModelTable.COMMIT_MODEL.LOGIN.is(login))
                        .and(CommitModelTable.COMMIT_MODEL.PULL_REQUEST_NUMBER.is(0L)))
                .queryDeep()
                .observe()
                .runQuery();
    }

    public static Observable<List<CommitModel>> getCommits(@NonNull String repoId, @NonNull String login, long pullRequestNumber) {
        return Select.from(CommitModelTable.COMMIT_MODEL)
                .where(CommitModelTable.COMMIT_MODEL.REPO_ID.is(repoId)
                        .and(CommitModelTable.COMMIT_MODEL.LOGIN.is(login))
                        .and(CommitModelTable.COMMIT_MODEL.PULL_REQUEST_NUMBER.is(pullRequestNumber)))
                .queryDeep()
                .observe()
                .runQuery();
    }

    public static Observable<CommitModel> getCommit(@NonNull String sha, @NonNull String repoId, @NonNull String login) {
        return Select.from(CommitModelTable.COMMIT_MODEL)
                .where(CommitModelTable.COMMIT_MODEL.REPO_ID.is(repoId)
                        .and(CommitModelTable.COMMIT_MODEL.LOGIN.is(login))
                        .and(CommitModelTable.COMMIT_MODEL.SHA.is(sha)))
                .queryDeep()
                .takeFirst()
                .observe()
                .runQuery();
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.ref);
        dest.writeParcelable(this.repo, flags);
        dest.writeString(this.sha);
        dest.writeByte(this.distincted ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.gitCommit, flags);
        dest.writeParcelable(this.author, flags);
        dest.writeParcelable(this.committer, flags);
        dest.writeParcelable(this.user, flags);
        dest.writeList(this.parents);
        dest.writeParcelable(this.stats, flags);
        dest.writeList(this.files);
        dest.writeString(this.htmlUrl);
        dest.writeString(this.login);
        dest.writeString(this.repoId);
        dest.writeLong(this.id);
    }

    protected CommitModel(Parcel in) {
        this.url = in.readString();
        this.ref = in.readString();
        this.repo = in.readParcelable(RepoModel.class.getClassLoader());
        this.sha = in.readString();
        this.distincted = in.readByte() != 0;
        this.gitCommit = in.readParcelable(GitCommitModel.class.getClassLoader());
        this.author = in.readParcelable(UserModel.class.getClassLoader());
        this.committer = in.readParcelable(UserModel.class.getClassLoader());
        this.user = in.readParcelable(UserModel.class.getClassLoader());
        in.readList(this.parents, this.parents.getClass().getClassLoader());
        this.stats = in.readParcelable(GithubState.class.getClassLoader());
        in.readList(this.files, this.files.getClass().getClassLoader());
        this.htmlUrl = in.readString();
        this.login = in.readString();
        this.repoId = in.readString();
        this.id = in.readLong();
    }

    public static final Creator<CommitModel> CREATOR = new Creator<CommitModel>() {
        @Override public CommitModel createFromParcel(Parcel source) {return new CommitModel(source);}

        @Override public CommitModel[] newArray(int size) {return new CommitModel[size];}
    };
}


