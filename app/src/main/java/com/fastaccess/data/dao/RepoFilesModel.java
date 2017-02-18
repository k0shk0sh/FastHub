package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.fastaccess.data.dao.types.FilesType;
import com.siimkinks.sqlitemagic.Delete;
import com.siimkinks.sqlitemagic.RepoFilesModelTable;
import com.siimkinks.sqlitemagic.Select;
import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Table;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rx.Completable;
import rx.Observable;

/**
 * Created by Kosh on 17 Feb 2017, 7:51 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class RepoFilesModel implements Parcelable {

    @Column String name;
    @Column String path;
    @Column String sha;
    @Column long size;
    @Column String url;
    @Column String htmlUrl;
    @Column String gitUrl;
    @Column String downloadUrl;
    @Column FilesType type;
    @Column String repoId;
    @Column String login;

    public Completable save() {
        return this.persist().observe().toCompletable();
    }

    public static Completable save(@NonNull List<RepoFilesModel> models, @NonNull String login, @NonNull String repoId) {
        return Delete.from(RepoFilesModelTable.REPO_FILES_MODEL)
                .where(RepoFilesModelTable.REPO_FILES_MODEL.REPO_ID.is(repoId)
                        .and(RepoFilesModelTable.REPO_FILES_MODEL.LOGIN.is(login)))
                .observe()
                .toCompletable()
                .andThen(Observable.create(subscriber -> Stream.of(models).forEach(filesModel -> {
                    filesModel.setRepoId(repoId);
                    filesModel.setLogin(login);
                    filesModel.save();
                })))
                .toCompletable();
    }

    public static Observable<List<RepoFilesModel>> getFiles(@NonNull String login, @NonNull String repoId) {
        return Select.from(RepoFilesModelTable.REPO_FILES_MODEL)
                .where(RepoFilesModelTable.REPO_FILES_MODEL.LOGIN.is(login)
                        .and(RepoFilesModelTable.REPO_FILES_MODEL.REPO_ID.is(repoId)))
                .orderBy(RepoFilesModelTable.REPO_FILES_MODEL.TYPE.asc())
                .observe()
                .runQuery();
    }

    public static Observable<RepoFilesModel> getFile(@NonNull String login, @NonNull String repoId, @NonNull String sha) {
        return Select.from(RepoFilesModelTable.REPO_FILES_MODEL)
                .where(RepoFilesModelTable.REPO_FILES_MODEL.LOGIN.is(login)
                        .and(RepoFilesModelTable.REPO_FILES_MODEL.REPO_ID.is(repoId))
                        .and(RepoFilesModelTable.REPO_FILES_MODEL.SHA.is(sha)))
                .takeFirst()
                .observe()
                .runQuery();
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.sha);
        dest.writeLong(this.size);
        dest.writeString(this.url);
        dest.writeString(this.htmlUrl);
        dest.writeString(this.gitUrl);
        dest.writeString(this.downloadUrl);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.repoId);
        dest.writeString(this.login);
        dest.writeLong(this.id);
    }

    protected RepoFilesModel(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.sha = in.readString();
        this.size = in.readLong();
        this.url = in.readString();
        this.htmlUrl = in.readString();
        this.gitUrl = in.readString();
        this.downloadUrl = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : FilesType.values()[tmpType];
        this.repoId = in.readString();
        this.login = in.readString();
        this.id = in.readLong();
    }

    public static final Parcelable.Creator<RepoFilesModel> CREATOR = new Parcelable.Creator<RepoFilesModel>() {
        @Override public RepoFilesModel createFromParcel(Parcel source) {return new RepoFilesModel(source);}

        @Override public RepoFilesModel[] newArray(int size) {return new RepoFilesModel[size];}
    };
}
