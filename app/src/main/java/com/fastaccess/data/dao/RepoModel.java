package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.siimkinks.sqlitemagic.Delete;
import com.siimkinks.sqlitemagic.RepoModelTable;
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
 * Created by Kosh on 08 Feb 2017, 10:07 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class RepoModel implements Parcelable {

    @Id(autoIncrement = false) @Column long id;
    @Column String name;
    @Column String fullName;
    @Column boolean privateX;
    @Column String htmlUrl;
    @Column String description;
    @Column boolean fork;
    @Column String url;
    @Column String forksUrl;
    @Column String keysUrl;
    @Column String collaboratorsUrl;
    @Column String teamsUrl;
    @Column String hooksUrl;
    @Column String issueEventsUrl;
    @Column String eventsUrl;
    @Column String assigneesUrl;
    @Column String branchesUrl;
    @Column String tagsUrl;
    @Column String blobsUrl;
    @Column String gitTagsUrl;
    @Column String gitRefsUrl;
    @Column String treesUrl;
    @Column String statusesUrl;
    @Column String languagesUrl;
    @Column String stargazersUrl;
    @Column String contributorsUrl;
    @Column String subscribersUrl;
    @Column String subscriptionUrl;
    @Column String commitsUrl;
    @Column String gitCommitsUrl;
    @Column String commentsUrl;
    @Column String issueCommentUrl;
    @Column String contentsUrl;
    @Column String compareUrl;
    @Column String mergesUrl;
    @Column String archiveUrl;
    @Column String downloadsUrl;
    @Column String issuesUrl;
    @Column String pullsUrl;
    @Column String milestonesUrl;
    @Column String notificationsUrl;
    @Column String labelsUrl;
    @Column String releasesUrl;
    @Column Date createdAt;
    @Column Date updatedAt;
    @Column Date pushedAt;
    @Column String gitUrl;
    @Column String sshUrl;
    @Column String cloneUrl;
    @Column String svnUrl;
    @Column String homepage;
    @Column long size;
    @Column long stargazersCount;
    @Column long watchersCount;
    @Column String language;
    @Column boolean hasIssues;
    @Column boolean hasDownloads;
    @Column boolean hasWiki;
    @Column boolean hasPages;
    @Column long forksCount;
    @Column String mirrorUrl;
    @Column long openIssuesCount;
    @Column long forks;
    @Column long openIssues;
    @Column long watchers;
    @Column String defaultBranch;
    @Column(onDeleteCascade = true) UserModel owner;
    @Column(onDeleteCascade = true) RepoPermissionsModel permissions;
    @Column(onDeleteCascade = true) UserModel organization;
    @Column(onDeleteCascade = true, handleRecursively = false) RepoModel parent;
    @Column(onDeleteCascade = true, handleRecursively = false) RepoModel source;
    @Column(onDeleteCascade = true) LicenseModel license;
    @Column int networkCount;
    @Column int subsCount;
    @Column String starredUser;
    @Column String reposOwner;

    public static Observable<RepoModel> getRepo(@NonNull String name) {
        return Select.from(RepoModelTable.REPO_MODEL)
                .where(RepoModelTable.REPO_MODEL.NAME.is(name))
                .takeFirst()
                .observe()
                .runQueryOnceOrDefault(null);
    }

    public static RepoModel getRepo(long id) {
        return Select.from(RepoModelTable.REPO_MODEL)
                .where(RepoModelTable.REPO_MODEL.ID.is(id))
                .takeFirst()
                .execute();
    }

    public static Completable saveStarred(@NonNull List<RepoModel> models, @NonNull String starredUser) {
        return Observable.create(subscriber -> Stream.of(models).forEach(repoModel -> repoModel.setStarredUser(starredUser)))
                .toCompletable()
                .andThen(Delete.from(RepoModelTable.REPO_MODEL)
                        .where(RepoModelTable.REPO_MODEL.STARRED_USER.is(starredUser))
                        .observe()
                        .toCompletable())
                .andThen(persist(models).observe());
    }

    public static Completable saveMyRepos(@NonNull List<RepoModel> models, @NonNull String reposOwner) {
        return Observable.create(subscriber -> Stream.of(models).forEach(repoModel -> repoModel.setReposOwner(reposOwner)))
                .toCompletable()
                .andThen(Delete.from(RepoModelTable.REPO_MODEL)
                        .where(RepoModelTable.REPO_MODEL.REPOS_OWNER.is(reposOwner))
                        .observe()
                        .toCompletable())
                .andThen(persist(models).observe());
    }

    public static Observable<List<RepoModel>> getStarred(@NonNull String starredUser) {
        return Select.from(RepoModelTable.REPO_MODEL)
                .where(RepoModelTable.REPO_MODEL.STARRED_USER.is(starredUser))
                .orderBy(RepoModelTable.REPO_MODEL.UPDATED_AT.desc())
                .observe()
                .runQuery();
    }

    public static Observable<List<RepoModel>> getMyRepos(@NonNull String reposOwner) {
        return Select.from(RepoModelTable.REPO_MODEL)
                .where(RepoModelTable.REPO_MODEL.REPOS_OWNER.is(reposOwner))
                .orderBy(RepoModelTable.REPO_MODEL.UPDATED_AT.desc())
                .observe()
                .runQuery();
    }

    public UserModel getOwner() {
        if (owner != null && owner.getLogin() == null) {
            UserModel model = UserModel.getUser(owner.getId());
            if (model != null) owner = model;
        }
        return owner;
    }

    public UserModel getOrganization() {
        if (organization != null && organization.getLogin() == null) {
            UserModel model = UserModel.getUser(organization.getId());
            if (model != null) organization = model;
        }
        return organization;
    }

    public void setOrganization(UserModel organization) {
        this.organization = organization;
    }

    public RepoModel getParent() {
        if (parent != null && parent.getFullName() == null) {
            RepoModel model = RepoModel.getRepo(parent.getId());
            if (model != null) parent = model;
        }
        return parent;
    }

    @Override public String toString() {
        return "RepoModel{" +
                "name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", owner=" + owner +
                '}';
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
        dest.writeParcelable(this.owner, flags);
        dest.writeParcelable(this.permissions, flags);
        dest.writeParcelable(this.organization, flags);
        dest.writeParcelable(this.parent, flags);
        dest.writeParcelable(this.source, flags);
        dest.writeParcelable(this.license, flags);
        dest.writeInt(this.networkCount);
        dest.writeInt(this.subsCount);
        dest.writeString(this.starredUser);
        dest.writeString(this.reposOwner);
    }

    protected RepoModel(Parcel in) {
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
        this.owner = in.readParcelable(UserModel.class.getClassLoader());
        this.permissions = in.readParcelable(RepoPermissionsModel.class.getClassLoader());
        this.organization = in.readParcelable(UserModel.class.getClassLoader());
        this.parent = in.readParcelable(RepoModel.class.getClassLoader());
        this.source = in.readParcelable(RepoModel.class.getClassLoader());
        this.license = in.readParcelable(LicenseModel.class.getClassLoader());
        this.networkCount = in.readInt();
        this.subsCount = in.readInt();
        this.starredUser = in.readString();
        this.reposOwner = in.readString();
    }

    public static final Creator<RepoModel> CREATOR = new Creator<RepoModel>() {
        @Override public RepoModel createFromParcel(Parcel source) {return new RepoModel(source);}

        @Override public RepoModel[] newArray(int size) {return new RepoModel[size];}
    };
}
