package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.App;
import com.fastaccess.helper.PrefGetter;

import java.util.Date;

import io.requery.Column;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Nullable;
import lombok.NoArgsConstructor;
import rx.Observable;

/**
 * Created by Kosh on 16 Mar 2017, 7:36 PM
 */

@Entity @NoArgsConstructor public abstract class AbstractLogin implements Parcelable {
    @Key long id;
    @Column(unique = true) String login;
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
    String token;
    int contributions;
    @Nullable boolean isLoggedIn;

    public Observable<Login> update(Login login) {
        login.setToken(PrefGetter.getToken());
        login.setIsLoggedIn(true);
        return App.getInstance().getDataStore().update(login)
                .toObservable();
    }

    public void save(Login entity) {
//        Login login = getUser();
//        if (login != null) {
//            if (!login.getLogin().equalsIgnoreCase(entity.getLogin())) {
//                App.getInstance().getDataStore().delete(login).toBlocking().value();
//            } else {
//                login.setIsLoggedIn(false);
//                App.getInstance().getDataStore().update(login).toBlocking().value();
//            }
//        }
//        entity.setIsLoggedIn(true); TODO for multiple logins
        App.getInstance().getDataStore()
                .insert(entity)
                .toBlocking()
                .value();
    }

    public static Login getUser() {
        return App.getInstance().getDataStore()
                .select(Login.class)
                .where(Login.LOGIN.notNull()
                        .and(Login.TOKEN.notNull()))
                .get()
                .firstOrNull();
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
        dest.writeString(this.token);
        dest.writeInt(this.contributions);
        dest.writeByte(this.isLoggedIn ? (byte) 1 : (byte) 0);
    }

    protected AbstractLogin(Parcel in) {
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
        this.token = in.readString();
        this.contributions = in.readInt();
        this.isLoggedIn = in.readByte() != 0;
    }

    public static final Creator<Login> CREATOR = new Creator<Login>() {
        @Override public Login createFromParcel(Parcel source) {return new Login(source);}

        @Override public Login[] newArray(int size) {return new Login[size];}
    };
}
