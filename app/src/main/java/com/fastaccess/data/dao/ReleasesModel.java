package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.siimkinks.sqlitemagic.Delete;
import com.siimkinks.sqlitemagic.ReleasesModelTable;
import com.siimkinks.sqlitemagic.Select;
import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Id;
import com.siimkinks.sqlitemagic.annotation.Table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rx.Completable;
import rx.Observable;

/**
 * Created by Kosh on 31 Dec 2016, 1:28 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class ReleasesModel implements Parcelable {

    @Column String url;
    @Column String htmlUrl;
    @Column String assetsUrl;
    @Column String uploadUrl;
    @SerializedName("tarball_url") @Column String tarballUrl;
    @SerializedName("zipball_url") @Column String zipBallUrl;
    @Column @Id(autoIncrement = false) long id;
    @Column String tagName;
    @Column String targetCommitish;
    @Column String name;
    @Column String body;
    @Column boolean draft;
    @Column boolean preRelease;
    @Column Date createdAt;
    @Column Date publishedAt;
    @Column UserModel author;
    @Column ReleasesAssetsListModel assets;
    @Column String repoId;
    @Column String login;

    public Completable save() {
        return this.persist().observe().toCompletable();
    }

    public static Completable save(@NonNull List<ReleasesModel> models, @NonNull String repoId, @NonNull String login) {
        return Delete.from(ReleasesModelTable.RELEASES_MODEL)
                .where(ReleasesModelTable.RELEASES_MODEL.REPO_ID.is(repoId))
                .observe()
                .toCompletable()
                .andThen(Observable.from(models)
                        .map(releasesModel -> {
                            releasesModel.setRepoId(repoId);
                            releasesModel.setLogin(login);
                            return releasesModel.save();
                        }))
                .toCompletable();
    }

    public static Completable delete(@NonNull String repoId, @NonNull String login) {
        return Delete.from(ReleasesModelTable.RELEASES_MODEL)
                .where(ReleasesModelTable.RELEASES_MODEL.REPO_ID.is(repoId)
                        .and(ReleasesModelTable.RELEASES_MODEL.LOGIN.is(login)))
                .observe()
                .toCompletable();
    }

    public static Observable<ReleasesModel> get(long id) {
        return Select.from(ReleasesModelTable.RELEASES_MODEL)
                .where(ReleasesModelTable.RELEASES_MODEL.ID.is(id))
                .queryDeep()
                .takeFirst()
                .observe()
                .runQuery();
    }

    public static Observable<List<ReleasesModel>> get(@NonNull String repoId, @NonNull String login) {
        return Select.from(ReleasesModelTable.RELEASES_MODEL)
                .where(ReleasesModelTable.RELEASES_MODEL.REPO_ID.is(repoId)
                        .and(ReleasesModelTable.RELEASES_MODEL.LOGIN.is(login)))
                .queryDeep()
                .observe()
                .runQueryOnceOrDefault(new ArrayList<>());
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.htmlUrl);
        dest.writeString(this.assetsUrl);
        dest.writeString(this.uploadUrl);
        dest.writeString(this.tarballUrl);
        dest.writeString(this.zipBallUrl);
        dest.writeLong(this.id);
        dest.writeString(this.tagName);
        dest.writeString(this.targetCommitish);
        dest.writeString(this.name);
        dest.writeString(this.body);
        dest.writeByte(this.draft ? (byte) 1 : (byte) 0);
        dest.writeByte(this.preRelease ? (byte) 1 : (byte) 0);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.publishedAt != null ? this.publishedAt.getTime() : -1);
        dest.writeParcelable(this.author, flags);
        dest.writeList(this.assets);
        dest.writeString(this.repoId);
        dest.writeString(this.login);
    }

    protected ReleasesModel(Parcel in) {
        this.url = in.readString();
        this.htmlUrl = in.readString();
        this.assetsUrl = in.readString();
        this.uploadUrl = in.readString();
        this.tarballUrl = in.readString();
        this.zipBallUrl = in.readString();
        this.id = in.readLong();
        this.tagName = in.readString();
        this.targetCommitish = in.readString();
        this.name = in.readString();
        this.body = in.readString();
        this.draft = in.readByte() != 0;
        this.preRelease = in.readByte() != 0;
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpPublishedAt = in.readLong();
        this.publishedAt = tmpPublishedAt == -1 ? null : new Date(tmpPublishedAt);
        this.author = in.readParcelable(UserModel.class.getClassLoader());
        this.assets = new ReleasesAssetsListModel();
        in.readList(this.assets, this.assets.getClass().getClassLoader());
        this.repoId = in.readString();
        this.login = in.readString();
    }

    public static final Creator<ReleasesModel> CREATOR = new Creator<ReleasesModel>() {
        @Override public ReleasesModel createFromParcel(Parcel source) {return new ReleasesModel(source);}

        @Override public ReleasesModel[] newArray(int size) {return new ReleasesModel[size];}
    };
}
