package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.App;
import com.fastaccess.data.dao.converters.ReactionsConverter;
import com.fastaccess.data.dao.converters.UserConverter;
import com.fastaccess.helper.RxHelper;

import java.util.Date;
import java.util.List;

import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import io.requery.rx.SingleEntityStore;
import lombok.NoArgsConstructor;
import rx.Observable;
import rx.Single;

import static com.fastaccess.data.dao.model.Comment.COMMIT_ID;
import static com.fastaccess.data.dao.model.Comment.GIST_ID;
import static com.fastaccess.data.dao.model.Comment.ID;
import static com.fastaccess.data.dao.model.Comment.ISSUE_ID;
import static com.fastaccess.data.dao.model.Comment.LOGIN;
import static com.fastaccess.data.dao.model.Comment.PULL_REQUEST_ID;
import static com.fastaccess.data.dao.model.Comment.REPO_ID;
import static com.fastaccess.data.dao.model.Comment.UPDATED_AT;

/**
 * Created by Kosh on 16 Mar 2017, 7:24 PM
 */
@Entity @NoArgsConstructor public abstract class AbstractComment implements Parcelable {
    @Key long id;
    @Column(name = "user_column") @Convert(UserConverter.class) User user;
    String url;
    String body;
    String bodyHtml;
    String htmlUrl;
    Date createdAt;
    Date updatedAt;
    int position;
    int line;
    String path;
    String commitId;
    String repoId;
    String login;
    String gistId;
    String issueId;
    String pullRequestId;
    @Convert(ReactionsConverter.class) ReactionsModel reactions;

    public Single save(Comment modelEntity) {
        return App.getInstance().getDataStore()
                .delete(Comment.class)
                .where(ID.eq(modelEntity.getId()))
                .get()
                .toSingle()
                .flatMap(integer -> App.getInstance().getDataStore().insert(modelEntity));
    }

    public static Observable saveForGist(@NonNull List<Comment> models, @NonNull String gistId) {
        SingleEntityStore<Persistable> singleEntityStore = App.getInstance().getDataStore();
        return RxHelper.safeObservable(singleEntityStore.delete(Comment.class)
                .where(GIST_ID.equal(gistId))
                .get()
                .toSingle()
                .toObservable()
                .flatMap(integer -> Observable.from(models))
                .flatMap(comment -> {
                    comment.setGistId(gistId);
                    return singleEntityStore.insert(comment).toObservable();
                }));
    }

    public static Observable saveForCommits(@NonNull List<Comment> models, @NonNull String repoId,
                                            @NonNull String login, @NonNull String commitId) {
        SingleEntityStore<Persistable> singleEntityStore = App.getInstance().getDataStore();
        return RxHelper.safeObservable(singleEntityStore.delete(Comment.class)
                .where(COMMIT_ID.equal(commitId)
                        .and(REPO_ID.equal(repoId))
                        .and(LOGIN.equal(login)))
                .get()
                .toSingle()
                .toObservable()
                .flatMap(integer -> Observable.from(models))
                .flatMap(model -> {
                    model.setLogin(login);
                    model.setRepoId(repoId);
                    model.setCommitId(commitId);
                    return singleEntityStore.insert(model).toObservable();
                }));
    }

    public static Observable<List<Comment>> getGistComments(@NonNull String gistId) {
        return App.getInstance().getDataStore()
                .select(Comment.class)
                .where(GIST_ID.equal(gistId))
                .orderBy(UPDATED_AT.desc())
                .get()
                .toObservable()
                .toList();
    }

    public static Observable<List<Comment>> getCommitComments(@NonNull String repoId, @NonNull String login,
                                                              @NonNull String commitId) {
        return App.getInstance().getDataStore()
                .select(Comment.class)
                .where(REPO_ID.equal(repoId)
                        .and(LOGIN.equal(login))
                        .and(COMMIT_ID.equal(commitId)))
                .orderBy(UPDATED_AT.desc())
                .get()
                .toObservable()
                .toList();
    }

    public static Observable<List<Comment>> getIssueComments(@NonNull String repoId, @NonNull String login,
                                                             @NonNull String issueId) {
        return App.getInstance().getDataStore()
                .select(Comment.class)
                .where(REPO_ID.equal(repoId)
                        .and(LOGIN.equal(login))
                        .and(ISSUE_ID.equal(issueId)))
                .orderBy(UPDATED_AT.desc())
                .get()
                .toObservable()
                .toList();
    }

    public static Observable<List<Comment>> getPullRequestComments(@NonNull String repoId, @NonNull String login,
                                                                   @NonNull String pullRequestId) {
        return App.getInstance().getDataStore()
                .select(Comment.class)
                .where(REPO_ID.equal(repoId)
                        .and(LOGIN.equal(login))
                        .and(PULL_REQUEST_ID.equal(pullRequestId)))
                .orderBy(UPDATED_AT.desc())
                .get()
                .toObservable()
                .toList();
    }


    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment that = (Comment) o;
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
        dest.writeString(this.login);
        dest.writeString(this.gistId);
        dest.writeString(this.issueId);
        dest.writeString(this.pullRequestId);
        dest.writeParcelable(this.reactions, flags);
    }

    protected AbstractComment(Parcel in) {
        this.id = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
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
        this.login = in.readString();
        this.gistId = in.readString();
        this.issueId = in.readString();
        this.pullRequestId = in.readString();
        this.reactions = in.readParcelable(ReactionsModel.class.getClassLoader());
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override public Comment createFromParcel(Parcel source) {return new Comment(source);}

        @Override public Comment[] newArray(int size) {return new Comment[size];}
    };
}
