package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.App;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.RxHelper;

import java.util.Date;

import io.reactivex.Observable;
import io.requery.Column;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Nullable;
import lombok.NoArgsConstructor;

/**
 * Created by Kosh on 16 Mar 2017, 7:36 PM
 */

@Entity @NoArgsConstructor public abstract class AbstractLogin implements Parcelable {
    @Key long id;
    @Column String login;
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
    @Nullable boolean isEnterprise;
    @Nullable String otpCode;
    @Nullable String enterpriseUrl;

    public Observable<Login> update(Login login) {
        return RxHelper.safeObservable(App.getInstance().getDataStore().update(login)
                .toObservable());
    }

    public void save(Login entity) {
        App.getInstance().getDataStore()
                .delete(Login.class)
                .where(Login.LOGIN.eq(entity.getLogin()))
                .get()
                .single()
                .flatMap(integer -> App.getInstance().getDataStore().insert(entity))
                .blockingGet();
    }

    public static Login getUser() {
        return App.getInstance().getDataStore()
                .select(Login.class)
                .where(Login.LOGIN.notNull()
                        .and(Login.TOKEN.notNull())
                        .and(Login.IS_LOGGED_IN.eq(true)))
                .get()
                .firstOrNull();
    }

    public static Login getUser(@NonNull String login) {
        return App.getInstance().getDataStore()
                .select(Login.class)
                .where(Login.LOGIN.eq(login)
                        .and(Login.TOKEN.notNull()))
                .get()
                .firstOrNull();
    }

    public static Observable<Login> getAccounts() {
        return App.getInstance().getDataStore()
                .select(Login.class)
                .where(Login.IS_LOGGED_IN.eq(false))
                .orderBy(Login.LOGIN.desc())
                .get()
                .observable();
    }

    public static void logout() {
        Login login = getUser();
        if (login == null) return;
        App.getInstance().getDataStore().toBlocking().delete(PinnedRepos.class)
                .where(PinnedRepos.LOGIN.eq(login.getLogin())).get().value();
        App.getInstance().getDataStore().toBlocking().delete(login);
    }

    public static boolean hasNormalLogin() {
        return App.getInstance().getDataStore()
                .count(Login.class)
                .where(Login.IS_ENTERPRISE.eq(false)
                        .or(Login.IS_ENTERPRISE.isNull()))
                .get()
                .value() > 0;
    }

    public static Observable<Boolean> onMultipleLogin(@NonNull Login userModel, boolean isEnterprise, boolean isNew) {
        return Observable.fromPublisher(s -> {
            Login currentUser = Login.getUser();
            if (currentUser != null) {
                currentUser.setIsLoggedIn(false);
                App.getInstance().getDataStore()
                        .toBlocking()
                        .update(currentUser);
            }
            if (!isEnterprise) {
                PrefGetter.resetEnterprise();
            }
            userModel.setIsLoggedIn(true);
            if (isNew) {
                userModel.setIsEnterprise(isEnterprise);
                userModel.setToken(isEnterprise ? PrefGetter.getEnterpriseToken() : PrefGetter.getToken());
                userModel.setOtpCode(isEnterprise ? PrefGetter.getEnterpriseOtpCode() : PrefGetter.getOtpCode());
                userModel.setEnterpriseUrl(isEnterprise ? PrefGetter.getEnterpriseUrl() : null);
                App.getInstance().getDataStore()
                        .toBlocking()
                        .delete(Login.class)
                        .where(Login.ID.eq(userModel.getId()))
                        .get()
                        .value();
                App.getInstance().getDataStore()
                        .toBlocking()
                        .insert(userModel);
            } else {
                if (isEnterprise) {
                    PrefGetter.setTokenEnterprise(userModel.token);
                    PrefGetter.setEnterpriseOtpCode(userModel.otpCode);
                    PrefGetter.setEnterpriseUrl(userModel.enterpriseUrl);
                } else {
                    PrefGetter.resetEnterprise();
                    PrefGetter.setToken(userModel.token);
                    PrefGetter.setOtpCode(userModel.otpCode);
                }
                App.getInstance().getDataStore()
                        .toBlocking()
                        .update(userModel);
            }
            s.onNext(true);
            s.onComplete();
        });
    }

    @Override public int describeContents() {
        return 0;
    }

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
        dest.writeByte(this.isEnterprise ? (byte) 1 : (byte) 0);
        dest.writeString(this.otpCode);
        dest.writeString(this.enterpriseUrl);
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
        this.isEnterprise = in.readByte() != 0;
        this.otpCode = in.readString();
        this.enterpriseUrl = in.readString();
    }

    public static final Creator<Login> CREATOR = new Creator<Login>() {
        @Override
        public Login createFromParcel(Parcel source) {
            return new Login(source);
        }

        @Override
        public Login[] newArray(int size) {
            return new Login[size];
        }
    };
}
