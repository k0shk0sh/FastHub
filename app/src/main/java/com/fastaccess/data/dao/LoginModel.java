package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.siimkinks.sqlitemagic.LoginModelTable;
import com.siimkinks.sqlitemagic.Select;
import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Id;
import com.siimkinks.sqlitemagic.annotation.Table;
import com.siimkinks.sqlitemagic.annotation.Unique;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Created by Kosh on 08 Feb 2017, 8:51 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class LoginModel implements Parcelable {

    @Id(autoIncrement = false) @Column long id;
    @Column @Unique String login;
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
    @Column String token;
    @Column int contributions;

    public void save() {
        this.persist().execute();
    }

    public static LoginModel getUser() {
        return Select.from(LoginModelTable.LOGIN_MODEL)
                .where(LoginModelTable.LOGIN_MODEL.LOGIN.isNotNull()
                        .and(LoginModelTable.LOGIN_MODEL.TOKEN.isNotNull()))
                .takeFirst()
                .execute();
    }

    public static LoginModel getUser(String login) {
        return Select.from(LoginModelTable.LOGIN_MODEL)
                .where(LoginModelTable.LOGIN_MODEL.LOGIN.is(login)
                        .and(LoginModelTable.LOGIN_MODEL.TOKEN.isNotNull()))
                .takeFirst()
                .execute();
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
    }

    protected LoginModel(Parcel in) {
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
    }

    public static final Creator<LoginModel> CREATOR = new Creator<LoginModel>() {
        @Override public LoginModel createFromParcel(Parcel source) {return new LoginModel(source);}

        @Override public LoginModel[] newArray(int size) {return new LoginModel[size];}
    };

    @Override public String toString() {
        return login;
    }
}