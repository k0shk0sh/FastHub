package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.siimkinks.sqlitemagic.Delete;
import com.siimkinks.sqlitemagic.Select;
import com.siimkinks.sqlitemagic.UserModelTable;
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
 * Created by Kosh on 08 Feb 2017, 8:51 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class UserModel implements Parcelable {

    @Id(autoIncrement = false) @Column long id;
    @Column String login;
    @Column String avatarUrl;
    @Column String gravatarId;
    @Column String url;
    @Column String htmlUrl;
    @Column String followersUrl;
    @Column String followingUrl;
    @Column String gistsUrl;
    @Column String starredUrl;
    @Column String subscriptionsUrl;
    @Column String organizationsUrl;
    @Column String reposUrl;
    @Column String eventsUrl;
    @Column String receivedEventsUrl;
    @Column String type;
    @Column boolean siteAdmin;
    @Column String name;
    @Column String company;
    @Column String blog;
    @Column String location;
    @Column String email;
    @Column boolean hireable;
    @Column String bio;
    @Column long publicRepos;
    @Column long publicGists;
    @Column long followers;
    @Column long following;
    @Column Date createdAt;
    @Column Date updatedAt;
    @Column int contributions;
    @Column String followingName;
    @Column String followerName;
    @Column Date date;
    @Column String repoId;

    public void save() {
        this.persist().execute();
    }

    @Nullable public static UserModel getUser(String login) {
        return Select.from(UserModelTable.USER_MODEL)
                .where(UserModelTable.USER_MODEL.LOGIN.is(login))
                .takeFirst()
                .execute();
    }

    @Nullable public static UserModel getUser(long id) {
        return Select.from(UserModelTable.USER_MODEL)
                .where(UserModelTable.USER_MODEL.ID.is(id))
                .takeFirst()
                .execute();
    }

    public static Completable saveFollowers(@NonNull List<UserModel> models, @NonNull String followingName) {
        return Delete.from(UserModelTable.USER_MODEL)
                .where(UserModelTable.USER_MODEL.FOLLOWING_NAME.is(followingName))
                .observe()
                .toCompletable()
                .andThen(Observable.from(models)
                        .map(userModel -> {
                            userModel.setFollowingName(followingName);
                            return userModel.persist().observe();
                        }))
                .toCompletable();
    }

    public static Completable saveFollowings(@NonNull List<UserModel> models, @NonNull String followerName) {
        return Delete.from(UserModelTable.USER_MODEL)
                .where(UserModelTable.USER_MODEL.FOLLOWER_NAME.is(followerName))
                .observe()
                .toCompletable()
                .andThen(Observable.from(models)
                        .map(userModel -> {
                            userModel.setFollowerName(followerName);
                            return userModel.persist().observe();
                        }))
                .toCompletable();

    }

    public static Completable saveContributors(@NonNull List<UserModel> models, @NonNull String repoId) {
        return Delete.from(UserModelTable.USER_MODEL)
                .where(UserModelTable.USER_MODEL.REPO_ID.is(repoId))
                .observe()
                .toCompletable()
                .andThen(Observable.from(models)
                        .map(userModel -> {
                            userModel.setRepoId(repoId);
                            return userModel.persist().observe();
                        }))
                .toCompletable();

    }

    @NonNull public static Observable<List<UserModel>> getFollowers(@NonNull String following) {
        return Select.from(UserModelTable.USER_MODEL)
                .where(UserModelTable.USER_MODEL.FOLLOWING_NAME.is(following))
                .orderBy(UserModelTable.USER_MODEL.FOLLOWERS.desc())
                .observe()
                .runQuery();
    }

    @NonNull public static Observable<List<UserModel>> getFollowing(@NonNull String follower) {
        return Select.from(UserModelTable.USER_MODEL)
                .where(UserModelTable.USER_MODEL.FOLLOWER_NAME.is(follower))
                .orderBy(UserModelTable.USER_MODEL.FOLLOWERS.desc())
                .observe()
                .runQuery();
    }

    @NonNull public static Observable<List<UserModel>> getContributors(@NonNull String repoId) {
        return Select.from(UserModelTable.USER_MODEL)
                .where(UserModelTable.USER_MODEL.REPO_ID.is(repoId))
                .orderBy(UserModelTable.USER_MODEL.CONTRIBUTIONS.desc())
                .observe()
                .runQuery();
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.login);
        dest.writeString(this.avatarUrl);
        dest.writeString(this.gravatarId);
        dest.writeString(this.url);
        dest.writeString(this.htmlUrl);
        dest.writeString(this.followersUrl);
        dest.writeString(this.followingUrl);
        dest.writeString(this.gistsUrl);
        dest.writeString(this.starredUrl);
        dest.writeString(this.subscriptionsUrl);
        dest.writeString(this.organizationsUrl);
        dest.writeString(this.reposUrl);
        dest.writeString(this.eventsUrl);
        dest.writeString(this.receivedEventsUrl);
        dest.writeString(this.type);
        dest.writeByte(this.siteAdmin ? (byte) 1 : (byte) 0);
        dest.writeString(this.name);
        dest.writeString(this.company);
        dest.writeString(this.blog);
        dest.writeString(this.location);
        dest.writeString(this.email);
        dest.writeByte(this.hireable ? (byte) 1 : (byte) 0);
        dest.writeString(this.bio);
        dest.writeLong(this.publicRepos);
        dest.writeLong(this.publicGists);
        dest.writeLong(this.followers);
        dest.writeLong(this.following);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeInt(this.contributions);
        dest.writeString(this.followingName);
        dest.writeString(this.followerName);
    }

    protected UserModel(Parcel in) {
        this.id = in.readLong();
        this.login = in.readString();
        this.avatarUrl = in.readString();
        this.gravatarId = in.readString();
        this.url = in.readString();
        this.htmlUrl = in.readString();
        this.followersUrl = in.readString();
        this.followingUrl = in.readString();
        this.gistsUrl = in.readString();
        this.starredUrl = in.readString();
        this.subscriptionsUrl = in.readString();
        this.organizationsUrl = in.readString();
        this.reposUrl = in.readString();
        this.eventsUrl = in.readString();
        this.receivedEventsUrl = in.readString();
        this.type = in.readString();
        this.siteAdmin = in.readByte() != 0;
        this.name = in.readString();
        this.company = in.readString();
        this.blog = in.readString();
        this.location = in.readString();
        this.email = in.readString();
        this.hireable = in.readByte() != 0;
        this.bio = in.readString();
        this.publicRepos = in.readLong();
        this.publicGists = in.readLong();
        this.followers = in.readLong();
        this.following = in.readLong();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.contributions = in.readInt();
        this.followingName = in.readString();
        this.followerName = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override public UserModel createFromParcel(Parcel source) {return new UserModel(source);}

        @Override public UserModel[] newArray(int size) {return new UserModel[size];}
    };

    @Override public String toString() {
        return "UserModel{" +
                "id='" + id + '\'' +
                '}';
    }
}