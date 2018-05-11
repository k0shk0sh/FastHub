package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.App;
import com.fastaccess.data.dao.LicenseModel;
import com.fastaccess.data.dao.RepoPermissionsModel;
import com.fastaccess.data.dao.TopicsModel;
import com.fastaccess.data.dao.converters.LicenseConverter;
import com.fastaccess.data.dao.converters.RepoConverter;
import com.fastaccess.data.dao.converters.RepoPermissionConverter;
import com.fastaccess.data.dao.converters.TopicsConverter;
import com.fastaccess.data.dao.converters.UserConverter;
import com.fastaccess.helper.RxHelper;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.requery.BlockingEntityStore;
import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Nullable;
import io.requery.Persistable;
import lombok.NoArgsConstructor;

import static com.fastaccess.data.dao.model.Repo.FULL_NAME;
import static com.fastaccess.data.dao.model.Repo.ID;
import static com.fastaccess.data.dao.model.Repo.REPOS_OWNER;
import static com.fastaccess.data.dao.model.Repo.STARRED_USER;
import static com.fastaccess.data.dao.model.Repo.STATUSES_URL;
import static com.fastaccess.data.dao.model.Repo.UPDATED_AT;

/**
 * Created by Kosh on 16 Mar 2017, 7:54 PM
 */

@Entity @NoArgsConstructor public abstract class AbstractRepo implements Parcelable {
    @Key long id;
    String name;
    String fullName;
    @SerializedName("private") boolean privateX;
    String htmlUrl;
    String description;
    boolean fork;
    String url;
    String forksUrl;
    String keysUrl;
    String collaboratorsUrl;
    String teamsUrl;
    String hooksUrl;
    String issueEventsUrl;
    String eventsUrl;
    String assigneesUrl;
    String branchesUrl;
    String tagsUrl;
    String blobsUrl;
    String gitTagsUrl;
    String gitRefsUrl;
    String treesUrl;
    String statusesUrl;
    String languagesUrl;
    String stargazersUrl;
    String contributorsUrl;
    String subscribersUrl;
    String subscriptionUrl;
    String commitsUrl;
    String gitCommitsUrl;
    String commentsUrl;
    String issueCommentUrl;
    String contentsUrl;
    String compareUrl;
    String mergesUrl;
    String archiveUrl;
    String downloadsUrl;
    String issuesUrl;
    String pullsUrl;
    String milestonesUrl;
    String notificationsUrl;
    String labelsUrl;
    String releasesUrl;
    Date createdAt;
    Date updatedAt;
    Date pushedAt;
    String gitUrl;
    String sshUrl;
    String cloneUrl;
    String svnUrl;
    String homepage;
    long size;
    long stargazersCount;
    long watchersCount;
    @Column(name = "language_column") String language;
    boolean hasIssues;
    boolean hasDownloads;
    boolean hasWiki;
    boolean hasPages;
    long forksCount;
    String mirrorUrl;
    long openIssuesCount;
    long forks;
    long openIssues;
    long watchers;
    String defaultBranch;
    @Nullable @Convert(TopicsConverter.class) TopicsModel topics;
    @Convert(UserConverter.class) User owner;
    @Convert(RepoPermissionConverter.class) RepoPermissionsModel permissions;
    @Convert(UserConverter.class) User organization;
    @Convert(RepoConverter.class) Repo parent;
    @Convert(RepoConverter.class) Repo source;
    @Convert(LicenseConverter.class) LicenseModel license;
    @SerializedName("subscribers_count") int subsCount;
    int networkCount;
    String starredUser;
    String reposOwner;
    @Nullable boolean hasProjects;

    public Disposable save(Repo entity) {
        return Single.create(e -> {
            BlockingEntityStore<Persistable> dataSource = App.getInstance().getDataStore().toBlocking();
            dataSource.delete(Repo.class).where(Repo.ID.eq(entity.getId())).get().value();
            dataSource.insert(entity);
        }).subscribe(o -> {/**/}, Throwable::printStackTrace);
    }

    public static Maybe<Repo> getRepo(@NonNull String name, @NonNull String login) {
        return App.getInstance().getDataStore()
                .select(Repo.class)
                .where(FULL_NAME.eq(login + "/" + name))
                .get()
                .maybe();
    }

    public static Repo getRepo(long id) {
        return App.getInstance().getDataStore()
                .select(Repo.class)
                .where(ID.eq(id))
                .get()
                .firstOrNull();
    }

    public static Disposable saveStarred(@NonNull List<Repo> models, @NonNull String starredUser) {
        return RxHelper.getSingle(Single.fromPublisher(s -> {
            try {
                Login login = Login.getUser();
                if (login != null) {
                    BlockingEntityStore<Persistable> dataSource = App.getInstance().getDataStore().toBlocking();
                    if (login.getLogin().equalsIgnoreCase(starredUser)) {
                        dataSource.delete(Repo.class)
                                .where(STARRED_USER.eq(starredUser))
                                .get()
                                .value();
                        if (!models.isEmpty()) {
                            for (Repo repo : models) {
                                dataSource.delete(Repo.class).where(Repo.ID.eq(repo.getId())).get().value();
                                repo.setStarredUser(starredUser);
                                dataSource.insert(repo);
                            }
                        }
                    } else {
                        dataSource.delete(Repo.class)
                                .where(STARRED_USER.notEqual(login.getLogin())
                                        .or(STATUSES_URL.isNull()))
                                .get()
                                .value();
                    }
                }
                s.onNext("");
            } catch (Exception ignored) {}
            s.onComplete();
        })).subscribe(o -> {/*donothing*/}, Throwable::printStackTrace);
    }

    public static Disposable saveMyRepos(@NonNull List<Repo> models, @NonNull String reposOwner) {
        return RxHelper.getSingle(Single.fromPublisher(s -> {
            try {
                Login login = Login.getUser();
                if (login != null) {
                    BlockingEntityStore<Persistable> dataSource = App.getInstance().getDataStore().toBlocking();
                    if (login.getLogin().equalsIgnoreCase(reposOwner)) {
                        dataSource.delete(Repo.class)
                                .where(REPOS_OWNER.eq(reposOwner))
                                .get()
                                .value();
                        if (!models.isEmpty()) {
                            for (Repo repo : models) {
                                dataSource.delete(Repo.class).where(Repo.ID.eq(repo.getId())).get().value();
                                repo.setReposOwner(reposOwner);
                                dataSource.insert(repo);
                            }
                        }
                    } else {
                        dataSource.delete(Repo.class)
                                .where(REPOS_OWNER.notEqual(login.getLogin())
                                        .or(REPOS_OWNER.isNull()))
                                .get()
                                .value();
                    }
                }
                s.onNext("");
            } catch (Exception e) {
                s.onError(e);
            }
            s.onComplete();
        })).subscribe(o -> {/*donothing*/}, Throwable::printStackTrace);
    }

    public static Single<List<Repo>> getStarred(@NonNull String starredUser) {
        return App.getInstance().getDataStore()
                .select(Repo.class)
                .where(STARRED_USER.eq(starredUser))
                .orderBy(UPDATED_AT.desc())
                .get()
                .observable()
                .toList();
    }

    public static Single<List<Repo>> getMyRepos(@NonNull String reposOwner) {
        return App.getInstance().getDataStore()
                .select(Repo.class)
                .where(REPOS_OWNER.eq(reposOwner))
                .orderBy(UPDATED_AT.desc())
                .get()
                .observable()
                .toList();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractRepo that = (AbstractRepo) o;
        return id == that.id;
    }

    @Override public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.fullName);
        dest.writeByte(this.privateX ? (byte) 1 : (byte) 0);
        dest.writeString(this.htmlUrl);
        dest.writeString(this.description);
        dest.writeByte(this.fork ? (byte) 1 : (byte) 0);
        dest.writeString(this.url);
        dest.writeString(this.forksUrl);
        dest.writeString(this.keysUrl);
        dest.writeString(this.collaboratorsUrl);
        dest.writeString(this.teamsUrl);
        dest.writeString(this.hooksUrl);
        dest.writeString(this.issueEventsUrl);
        dest.writeString(this.eventsUrl);
        dest.writeString(this.assigneesUrl);
        dest.writeString(this.branchesUrl);
        dest.writeString(this.tagsUrl);
        dest.writeString(this.blobsUrl);
        dest.writeString(this.gitTagsUrl);
        dest.writeString(this.gitRefsUrl);
        dest.writeString(this.treesUrl);
        dest.writeString(this.statusesUrl);
        dest.writeString(this.languagesUrl);
        dest.writeString(this.stargazersUrl);
        dest.writeString(this.contributorsUrl);
        dest.writeString(this.subscribersUrl);
        dest.writeString(this.subscriptionUrl);
        dest.writeString(this.commitsUrl);
        dest.writeString(this.gitCommitsUrl);
        dest.writeString(this.commentsUrl);
        dest.writeString(this.issueCommentUrl);
        dest.writeString(this.contentsUrl);
        dest.writeString(this.compareUrl);
        dest.writeString(this.mergesUrl);
        dest.writeString(this.archiveUrl);
        dest.writeString(this.downloadsUrl);
        dest.writeString(this.issuesUrl);
        dest.writeString(this.pullsUrl);
        dest.writeString(this.milestonesUrl);
        dest.writeString(this.notificationsUrl);
        dest.writeString(this.labelsUrl);
        dest.writeString(this.releasesUrl);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeLong(this.pushedAt != null ? this.pushedAt.getTime() : -1);
        dest.writeString(this.gitUrl);
        dest.writeString(this.sshUrl);
        dest.writeString(this.cloneUrl);
        dest.writeString(this.svnUrl);
        dest.writeString(this.homepage);
        dest.writeLong(this.size);
        dest.writeLong(this.stargazersCount);
        dest.writeLong(this.watchersCount);
        dest.writeString(this.language);
        dest.writeByte(this.hasIssues ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasDownloads ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasWiki ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasPages ? (byte) 1 : (byte) 0);
        dest.writeLong(this.forksCount);
        dest.writeString(this.mirrorUrl);
        dest.writeLong(this.openIssuesCount);
        dest.writeLong(this.forks);
        dest.writeLong(this.openIssues);
        dest.writeLong(this.watchers);
        dest.writeString(this.defaultBranch);
        dest.writeList(this.topics);
        dest.writeParcelable(this.owner, flags);
        dest.writeParcelable(this.permissions, flags);
        dest.writeParcelable(this.organization, flags);
        dest.writeParcelable(this.parent, flags);
        dest.writeParcelable(this.source, flags);
        dest.writeParcelable(this.license, flags);
        dest.writeInt(this.subsCount);
        dest.writeInt(this.networkCount);
        dest.writeString(this.starredUser);
        dest.writeString(this.reposOwner);
        dest.writeByte(this.hasProjects ? (byte) 1 : (byte) 0);
    }

    protected AbstractRepo(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.fullName = in.readString();
        this.privateX = in.readByte() != 0;
        this.htmlUrl = in.readString();
        this.description = in.readString();
        this.fork = in.readByte() != 0;
        this.url = in.readString();
        this.forksUrl = in.readString();
        this.keysUrl = in.readString();
        this.collaboratorsUrl = in.readString();
        this.teamsUrl = in.readString();
        this.hooksUrl = in.readString();
        this.issueEventsUrl = in.readString();
        this.eventsUrl = in.readString();
        this.assigneesUrl = in.readString();
        this.branchesUrl = in.readString();
        this.tagsUrl = in.readString();
        this.blobsUrl = in.readString();
        this.gitTagsUrl = in.readString();
        this.gitRefsUrl = in.readString();
        this.treesUrl = in.readString();
        this.statusesUrl = in.readString();
        this.languagesUrl = in.readString();
        this.stargazersUrl = in.readString();
        this.contributorsUrl = in.readString();
        this.subscribersUrl = in.readString();
        this.subscriptionUrl = in.readString();
        this.commitsUrl = in.readString();
        this.gitCommitsUrl = in.readString();
        this.commentsUrl = in.readString();
        this.issueCommentUrl = in.readString();
        this.contentsUrl = in.readString();
        this.compareUrl = in.readString();
        this.mergesUrl = in.readString();
        this.archiveUrl = in.readString();
        this.downloadsUrl = in.readString();
        this.issuesUrl = in.readString();
        this.pullsUrl = in.readString();
        this.milestonesUrl = in.readString();
        this.notificationsUrl = in.readString();
        this.labelsUrl = in.readString();
        this.releasesUrl = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        long tmpPushedAt = in.readLong();
        this.pushedAt = tmpPushedAt == -1 ? null : new Date(tmpPushedAt);
        this.gitUrl = in.readString();
        this.sshUrl = in.readString();
        this.cloneUrl = in.readString();
        this.svnUrl = in.readString();
        this.homepage = in.readString();
        this.size = in.readLong();
        this.stargazersCount = in.readLong();
        this.watchersCount = in.readLong();
        this.language = in.readString();
        this.hasIssues = in.readByte() != 0;
        this.hasDownloads = in.readByte() != 0;
        this.hasWiki = in.readByte() != 0;
        this.hasPages = in.readByte() != 0;
        this.forksCount = in.readLong();
        this.mirrorUrl = in.readString();
        this.openIssuesCount = in.readLong();
        this.forks = in.readLong();
        this.openIssues = in.readLong();
        this.watchers = in.readLong();
        this.defaultBranch = in.readString();
        this.topics = new TopicsModel();
        in.readList(this.topics, this.topics.getClass().getClassLoader());
        this.owner = in.readParcelable(User.class.getClassLoader());
        this.permissions = in.readParcelable(RepoPermissionsModel.class.getClassLoader());
        this.organization = in.readParcelable(User.class.getClassLoader());
        this.parent = in.readParcelable(Repo.class.getClassLoader());
        this.source = in.readParcelable(Repo.class.getClassLoader());
        this.license = in.readParcelable(LicenseModel.class.getClassLoader());
        this.subsCount = in.readInt();
        this.networkCount = in.readInt();
        this.starredUser = in.readString();
        this.reposOwner = in.readString();
        this.hasProjects = in.readByte() != 0;
    }

    public static final Creator<Repo> CREATOR = new Creator<Repo>() {
        @Override public Repo createFromParcel(Parcel source) {return new Repo(source);}

        @Override public Repo[] newArray(int size) {return new Repo[size];}
    };
}
