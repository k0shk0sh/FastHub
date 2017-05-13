package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.App;
import com.fastaccess.data.dao.CommitFileListModel;
import com.fastaccess.data.dao.CommitListModel;
import com.fastaccess.data.dao.GitCommitModel;
import com.fastaccess.data.dao.GithubState;
import com.fastaccess.data.dao.converters.CommitFilesConverter;
import com.fastaccess.data.dao.converters.CommitsConverter;
import com.fastaccess.data.dao.converters.GitCommitConverter;
import com.fastaccess.data.dao.converters.GitHubStateConverter;
import com.fastaccess.data.dao.converters.RepoConverter;
import com.fastaccess.data.dao.converters.UserConverter;
import com.fastaccess.helper.RxHelper;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Nullable;
import io.requery.Persistable;
import io.requery.Table;
import io.requery.rx.SingleEntityStore;
import lombok.NoArgsConstructor;
import rx.Observable;
import rx.Single;

import static com.fastaccess.data.dao.model.Commit.LOGIN;
import static com.fastaccess.data.dao.model.Commit.PULL_REQUEST_NUMBER;
import static com.fastaccess.data.dao.model.Commit.REPO_ID;
import static com.fastaccess.data.dao.model.Commit.SHA;

@Entity @NoArgsConstructor @Table(name = "commit_table")
public abstract class AbstractCommit implements Parcelable {
    @Key @Generated long id;
    String url;
    String sha;
    String htmlUrl;
    String login;
    String repoId;
    long pullRequestNumber;
    @Convert(GitHubStateConverter.class) GithubState stats;
    @Convert(CommitFilesConverter.class) CommitFileListModel files;
    @Convert(CommitsConverter.class) CommitListModel parents;
    @Column(name = "ref_column") String ref;
    @SerializedName("distincted") boolean distincted;
    @SerializedName("commit") @Convert(GitCommitConverter.class) GitCommitModel gitCommit;
    @Convert(UserConverter.class) User author;
    @Convert(UserConverter.class) User committer;
    @Convert(RepoConverter.class) Repo repo;
    @Column(name = "user_column") @Convert(UserConverter.class) User user;
    @Nullable int commentCount;

    public Single save(Commit modelEntity) {
        return App.getInstance()
                .getDataStore()
                .insert(modelEntity);
    }

    public static Observable save(@NonNull List<Commit> models, @NonNull String repoId, @NonNull String login) {
        SingleEntityStore<Persistable> singleEntityStore = App.getInstance().getDataStore();
        return RxHelper.safeObservable(singleEntityStore.delete(Commit.class)
                .where(REPO_ID.eq(repoId)
                        .and(LOGIN.eq(login)))
                .get()
                .toSingle()
                .toObservable()
                .flatMap(integer -> Observable.from(models))
                .flatMap(commitModel -> {
                    commitModel.setRepoId(repoId);
                    commitModel.setLogin(login);
                    return commitModel.save(commitModel).toObservable();
                }));
    }

    public static Observable save(@NonNull List<Commit> models, @NonNull String repoId, @NonNull String login, long number) {
        SingleEntityStore<Persistable> singleEntityStore = App.getInstance().getDataStore();
        return RxHelper.safeObservable(singleEntityStore.delete(Commit.class)
                .where(REPO_ID.eq(repoId)
                        .and(LOGIN.eq(login))
                        .and(PULL_REQUEST_NUMBER.eq(number)))
                .get()
                .toSingle()
                .toObservable()
                .flatMap(integer -> Observable.from(models))
                .flatMap(commitModel -> {
                    commitModel.setRepoId(repoId);
                    commitModel.setLogin(login);
                    commitModel.setPullRequestNumber(number);
                    return commitModel.save(commitModel).toObservable();
                }));
    }

    public static Observable<List<Commit>> getCommits(@NonNull String repoId, @NonNull String login) {
        return App.getInstance().getDataStore()
                .select(Commit.class)
                .where(REPO_ID.eq(repoId)
                        .and(LOGIN.eq(login))
                        .and(PULL_REQUEST_NUMBER.eq(0L)))
                .get()
                .toObservable()
                .toList();
    }

    public static Observable<List<Commit>> getCommits(@NonNull String repoId, @NonNull String login, long pullRequestNumber) {
        return App.getInstance().getDataStore()
                .select(Commit.class)
                .where(REPO_ID.eq(repoId)
                        .and(LOGIN.eq(login))
                        .and(PULL_REQUEST_NUMBER.eq(pullRequestNumber)))
                .get()
                .toObservable()
                .toList();
    }

    public static Observable<Commit> getCommit(@NonNull String sha, @NonNull String repoId, @NonNull String login) {
        return App.getInstance().getDataStore()
                .select(Commit.class)
                .where(REPO_ID.eq(repoId)
                        .and(LOGIN.eq(login))
                        .and(SHA.eq(sha)))
                .limit(1)
                .get()
                .toObservable();
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.url);
        dest.writeString(this.sha);
        dest.writeString(this.htmlUrl);
        dest.writeString(this.login);
        dest.writeString(this.repoId);
        dest.writeLong(this.pullRequestNumber);
        dest.writeParcelable(this.stats, flags);
        dest.writeList(this.files);
        dest.writeList(this.parents);
        dest.writeString(this.ref);
        dest.writeByte(this.distincted ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.gitCommit, flags);
        dest.writeParcelable(this.author, flags);
        dest.writeParcelable(this.committer, flags);
        dest.writeParcelable(this.repo, flags);
        dest.writeParcelable(this.user, flags);
        dest.writeInt(this.commentCount);
    }

    protected AbstractCommit(Parcel in) {
        this.id = in.readLong();
        this.url = in.readString();
        this.sha = in.readString();
        this.htmlUrl = in.readString();
        this.login = in.readString();
        this.repoId = in.readString();
        this.pullRequestNumber = in.readLong();
        this.stats = in.readParcelable(GithubState.class.getClassLoader());
        this.files = new CommitFileListModel();
        in.readList(this.files, this.files.getClass().getClassLoader());
        this.parents = new CommitListModel();
        in.readList(this.parents, this.parents.getClass().getClassLoader());
        this.ref = in.readString();
        this.distincted = in.readByte() != 0;
        this.gitCommit = in.readParcelable(GitCommitModel.class.getClassLoader());
        this.author = in.readParcelable(User.class.getClassLoader());
        this.committer = in.readParcelable(User.class.getClassLoader());
        this.repo = in.readParcelable(Repo.class.getClassLoader());
        this.user = in.readParcelable(User.class.getClassLoader());
        this.commentCount = in.readInt();
    }

    public static final Creator<Commit> CREATOR = new Creator<Commit>() {
        @Override public Commit createFromParcel(Parcel source) {return new Commit(source);}

        @Override public Commit[] newArray(int size) {return new Commit[size];}
    };
}
