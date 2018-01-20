package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.App;
import com.fastaccess.helper.RxHelper;

import java.util.Date;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.requery.BlockingEntityStore;
import io.requery.Column;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import io.requery.Table;
import io.requery.Transient;
import lombok.NoArgsConstructor;

import static com.fastaccess.data.dao.model.User.FOLLOWER_NAME;
import static com.fastaccess.data.dao.model.User.FOLLOWING_NAME;
import static com.fastaccess.data.dao.model.User.ID;
import static com.fastaccess.data.dao.model.User.LOGIN;

/**
 * Created by Kosh on 16 Mar 2017, 7:55 PM
 */

@Entity @NoArgsConstructor @Table(name = "user_table")
public abstract class AbstractUser implements Parcelable {
    @Key long id;
    String login;
    String avatarUrl;
    String gravatarId;
    String url;
    String htmlUrl;
    String followersUrl;
    String followingUrl;
    String gistsUrl;
    String starredUrl;
    String subscriptionsUrl;
    String organizationsUrl;
    String reposUrl;
    String eventsUrl;
    String receivedEventsUrl;
    String type;
    boolean siteAdmin;
    String name;
    String company;
    String blog;
    String location;
    String email;
    boolean hireable;
    String bio;
    long publicRepos;
    long publicGists;
    long followers;
    long following;
    Date createdAt;
    Date updatedAt;
    int contributions;
    String followingName;
    String followerName;
    @Column(name = "date_column") Date date;
    String repoId;
    String description;
    @Transient boolean hasOrganizationProjects;

    public void save(User entity) {
        if (getUser(entity.getId()) != null) {
            App.getInstance().getDataStore().toBlocking().update(entity);
        } else {
            App.getInstance().getDataStore().toBlocking().insert(entity);
        }
    }

    @Nullable public static User getUser(String login) {
        return App.getInstance().getDataStore()
                .select(User.class)
                .where(LOGIN.eq(login))
                .get()
                .firstOrNull();
    }

    @Nullable public static User getUser(long id) {
        return App.getInstance().getDataStore()
                .select(User.class)
                .where(ID.eq(id))
                .get()
                .firstOrNull();
    }

    public static Disposable saveUserFollowerList(@NonNull List<User> models, @NonNull String followingName) {
        return RxHelper.getSingle(Single.fromPublisher(s -> {
            try {
                Login login = Login.getUser();
                if (login != null) {
                    BlockingEntityStore<Persistable> dataSource = App.getInstance().getDataStore().toBlocking();
                    if (login.getLogin().equalsIgnoreCase(followingName)) {
                        dataSource.delete(User.class)
                                .where(FOLLOWING_NAME.eq(followingName))
                                .get()
                                .value();
                        if (!models.isEmpty()) {
                            for (User user : models) {
                                dataSource.delete(User.class).where(User.ID.eq(user.getId())).get().value();
                                user.setFollowingName(followingName);
                                dataSource.insert(user);
                            }
                        }
                    } else {
                        dataSource.delete(User.class)
                                .where(User.FOLLOWING_NAME.notEqual(login.getLogin()))
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

    public static Disposable saveUserFollowingList(@NonNull List<User> models, @NonNull String followerName) {
        return RxHelper.getSingle(Single.fromPublisher(s -> {
            try {
                Login login = Login.getUser();
                if (login != null) {
                    BlockingEntityStore<Persistable> dataSource = App.getInstance().getDataStore().toBlocking();
                    if (login.getLogin().equalsIgnoreCase(followerName)) {
                        dataSource.delete(User.class)
                                .where(FOLLOWER_NAME.eq(followerName))
                                .get()
                                .value();
                        if (!models.isEmpty()) {
                            for (User user : models) {
                                dataSource.delete(User.class).where(User.ID.eq(user.getId())).get().value();
                                user.setFollowerName(followerName);
                                dataSource.insert(user);
                            }
                        }
                    } else {
                        dataSource.delete(User.class)
                                .where(User.FOLLOWER_NAME.notEqual(login.getLogin()))
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

    @NonNull public static Single<List<User>> getUserFollowerList(@NonNull String following) {
        return App.getInstance().getDataStore()
                .select(User.class)
                .where(FOLLOWING_NAME.eq(following))
                .get()
                .observable()
                .toList();
    }

    @NonNull public static Single<List<User>> getUserFollowingList(@NonNull String follower) {
        return App.getInstance().getDataStore()
                .select(User.class)
                .where(FOLLOWER_NAME.eq(follower))
                .get()
                .observable()
                .toList();
    }

    public boolean isOrganizationType() {
        return type != null && type.equalsIgnoreCase("Organization");
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
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeString(this.repoId);
        dest.writeString(this.description);
    }

    protected AbstractUser(Parcel in) {
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
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.repoId = in.readString();
        this.description = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override public User createFromParcel(Parcel source) {return new User(source);}

        @Override public User[] newArray(int size) {return new User[size];}
    };
}
