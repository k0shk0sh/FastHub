package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.fastaccess.App;
import com.fastaccess.data.dao.ReleasesAssetsListModel;
import com.fastaccess.data.dao.converters.ReleasesAssetsConverter;
import com.fastaccess.data.dao.converters.UserConverter;
import com.fastaccess.helper.RxHelper;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import io.requery.Convert;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import io.requery.Table;
import io.requery.rx.SingleEntityStore;
import lombok.NoArgsConstructor;
import rx.Completable;
import rx.Observable;

import static com.fastaccess.data.dao.model.Release.ID;
import static com.fastaccess.data.dao.model.Release.LOGIN;
import static com.fastaccess.data.dao.model.Release.REPO_ID;

/**
 * Created by Kosh on 16 Mar 2017, 7:40 PM
 */

@Entity @NoArgsConstructor @Table(name = "release_table")
public abstract class AbstractRelease implements Parcelable {
    @Key long id;
    String url;
    String htmlUrl;
    String assetsUrl;
    String uploadUrl;
    String tagName;
    String targetCommitish;
    String name;
    boolean draft;
    boolean preRelease;
    Date createdAt;
    Date publishedAt;
    String repoId;
    String login;
    @SerializedName("tarball_url") String tarballUrl;
    @SerializedName("body_html") String body;
    @SerializedName("zipball_url") String zipBallUrl;
    @Convert(UserConverter.class) User author;
    @Convert(ReleasesAssetsConverter.class) ReleasesAssetsListModel assets;

    public Completable save(Release entity) {
        return App.getInstance().getDataStore()
                .delete(Release.class)
                .where(ID.eq(entity.getId()))
                .get()
                .toSingle()
                .toCompletable()
                .andThen(App.getInstance().getDataStore()
                        .insert(entity)
                        .toCompletable());
    }

    public static Observable save(@NonNull List<Release> models, @NonNull String repoId, @NonNull String login) {
        return RxHelper.safeObservable(
                Observable.create(subscriber -> {
                    SingleEntityStore<Persistable> singleEntityStore = App.getInstance().getDataStore();
                    singleEntityStore.delete(Release.class)
                            .where(REPO_ID.eq(login))
                            .get()
                            .value();
                    Stream.of(models)
                            .forEach(releasesModel -> {
                                releasesModel.setRepoId(repoId);
                                releasesModel.setLogin(login);
                                releasesModel.save(releasesModel).toObservable().toBlocking().singleOrDefault(null);
                            });
                })
        );

    }

    public static Completable delete(@NonNull String repoId, @NonNull String login) {
        return App.getInstance().getDataStore()
                .delete(Release.class)
                .where(REPO_ID.eq(repoId)
                        .and(LOGIN.eq(login)))
                .get()
                .toSingle()
                .toCompletable();
    }

    public static Observable<Release> get(long id) {
        return App.getInstance().getDataStore()
                .select(Release.class)
                .where(ID.eq(id))
                .get()
                .toObservable();
    }

    public static Observable<List<Release>> get(@NonNull String repoId, @NonNull String login) {
        return App.getInstance().getDataStore()
                .select(Release.class)
                .where(REPO_ID.eq(repoId)
                        .and(LOGIN.eq(login)))
                .get()
                .toObservable()
                .toList();
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.url);
        dest.writeString(this.htmlUrl);
        dest.writeString(this.assetsUrl);
        dest.writeString(this.uploadUrl);
        dest.writeString(this.tagName);
        dest.writeString(this.targetCommitish);
        dest.writeString(this.name);
        dest.writeByte(this.draft ? (byte) 1 : (byte) 0);
        dest.writeByte(this.preRelease ? (byte) 1 : (byte) 0);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.publishedAt != null ? this.publishedAt.getTime() : -1);
        dest.writeString(this.repoId);
        dest.writeString(this.login);
        dest.writeString(this.tarballUrl);
        dest.writeString(this.body);
        dest.writeString(this.zipBallUrl);
        dest.writeParcelable(this.author, flags);
        dest.writeList(this.assets);
    }

    protected AbstractRelease(Parcel in) {
        this.id = in.readLong();
        this.url = in.readString();
        this.htmlUrl = in.readString();
        this.assetsUrl = in.readString();
        this.uploadUrl = in.readString();
        this.tagName = in.readString();
        this.targetCommitish = in.readString();
        this.name = in.readString();
        this.draft = in.readByte() != 0;
        this.preRelease = in.readByte() != 0;
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpPublishedAt = in.readLong();
        this.publishedAt = tmpPublishedAt == -1 ? null : new Date(tmpPublishedAt);
        this.repoId = in.readString();
        this.login = in.readString();
        this.tarballUrl = in.readString();
        this.body = in.readString();
        this.zipBallUrl = in.readString();
        this.author = in.readParcelable(User.class.getClassLoader());
        this.assets = new ReleasesAssetsListModel();
        in.readList(this.assets, this.assets.getClass().getClassLoader());
    }

    public static final Creator<Release> CREATOR = new Creator<Release>() {
        @Override public Release createFromParcel(Parcel source) {return new Release(source);}

        @Override public Release[] newArray(int size) {return new Release[size];}
    };
}
