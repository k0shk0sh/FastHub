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

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.requery.BlockingEntityStore;
import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Nullable;
import io.requery.Persistable;
import io.requery.Table;
import lombok.NoArgsConstructor;

import static com.fastaccess.data.dao.model.Commit.ID;
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

    public Single<Commit> save(Commit entity) {
        return RxHelper.getSingle(App.getInstance().getDataStore().upsert(entity));
    }

    public static Disposable save(@NonNull List<Commit> models, @NonNull String repoId, @NonNull String login) {
        return RxHelper.getSingle(Single.fromPublisher(s -> {
            try {
                BlockingEntityStore<Persistable> dataSource = App.getInstance().getDataStore().toBlocking();
                dataSource.delete(Commit.class)
                        .where(REPO_ID.eq(repoId).and(LOGIN.eq(login)))
                        .get()
                        .value();
                if (!models.isEmpty()) {
                    for (Commit commitModel : models) {
                        dataSource.delete(Commit.class).where(ID.eq(commitModel.getId())).get().value();
                        commitModel.setRepoId(repoId);
                        commitModel.setLogin(login);
                        dataSource.insert(commitModel);
                    }
                }
                s.onNext("");
            } catch (Exception e) {
                s.onError(e);
            }
            s.onComplete();
        })).subscribe(o -> {/*donothing*/}, Throwable::printStackTrace);
    }

    public static Disposable save(@NonNull List<Commit> models, @NonNull String repoId, @NonNull String login, long number) {
        return RxHelper.getSingle(Single.fromPublisher(s -> {
            try {
                BlockingEntityStore<Persistable> dataSource = App.getInstance().getDataStore().toBlocking();
                dataSource.delete(Commit.class)
                        .where(REPO_ID.eq(repoId)
                                .and(LOGIN.eq(login))
                                .and(PULL_REQUEST_NUMBER.eq(number)))
                        .get()
                        .value();
                if (!models.isEmpty()) {
                    for (Commit commitModel : models) {
                        dataSource.delete(Commit.class).where(ID.eq(commitModel.getId())).get().value();
                        commitModel.setRepoId(repoId);
                        commitModel.setLogin(login);
                        commitModel.setPullRequestNumber(number);
                        dataSource.insert(commitModel);
                    }
                }
                s.onNext("");
            } catch (Exception e) {
                s.onError(e);
            }
            s.onComplete();
        })).subscribe(o -> {/*donothing*/}, Throwable::printStackTrace);
    }

    public static Single<List<Commit>> getCommits(@NonNull String repoId, @NonNull String login) {
        return App.getInstance().getDataStore()
                .select(Commit.class)
                .where(REPO_ID.eq(repoId)
                        .and(LOGIN.eq(login))
                        .and(PULL_REQUEST_NUMBER.eq(0L)))
                .get()
                .observable()
                .toList();
    }

    public static Single<List<Commit>> getCommits(@NonNull String repoId, @NonNull String login, long pullRequestNumber) {
        return App.getInstance().getDataStore()
                .select(Commit.class)
                .where(REPO_ID.eq(repoId)
                        .and(LOGIN.eq(login))
                        .and(PULL_REQUEST_NUMBER.eq(pullRequestNumber)))
                .get()
                .observable()
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
                .observable();
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
