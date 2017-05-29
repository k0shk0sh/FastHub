package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.App;
import com.fastaccess.data.dao.types.FilesType;
import com.fastaccess.helper.RxHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import lombok.NoArgsConstructor;

import static com.fastaccess.data.dao.model.RepoFile.LOGIN;
import static com.fastaccess.data.dao.model.RepoFile.REPO_ID;
import static com.fastaccess.data.dao.model.RepoFile.SHA;
import static com.fastaccess.data.dao.model.RepoFile.TYPE;

/**
 * Created by Kosh on 16 Mar 2017, 7:53 PM
 */

@Entity @NoArgsConstructor public abstract class AbstractRepoFile implements Parcelable {
    @Key @Generated long id;
    String name;
    String path;
    String sha;
    long size;
    String url;
    String htmlUrl;
    String gitUrl;
    String downloadUrl;
    FilesType type;
    String repoId;
    String login;


    public Single<RepoFile> save(RepoFile entity) {
        return RxHelper.getSingle(App.getInstance().getDataStore().insert(entity));
    }

    public static Observable<RepoFile> save(@NonNull List<RepoFile> models, @NonNull String login, @NonNull String repoId) {
        ReactiveEntityStore<Persistable> singleEntityStore = App.getInstance().getDataStore();
        return RxHelper.safeObservable(singleEntityStore.delete(RepoFile.class)
                .where(REPO_ID.eq(repoId)
                        .and(LOGIN.eq(login)))
                .get()
                .single()
                .toObservable()
                .flatMap(integer -> Observable.fromIterable(models))
                .flatMap(filesModel -> {
                    filesModel.setRepoId(repoId);
                    filesModel.setLogin(login);
                    return filesModel.save(filesModel).toObservable();
                }));
    }

    public static Single<List<RepoFile>> getFiles(@NonNull String login, @NonNull String repoId) {
        return App.getInstance().getDataStore()
                .select(RepoFile.class)
                .where(REPO_ID.eq(repoId)
                        .and(LOGIN.eq(login)))
                .orderBy(TYPE.asc())
                .get()
                .observable()
                .toList();
    }

    public static Observable<RepoFile> getFile(@NonNull String login, @NonNull String repoId, @NonNull String sha) {
        return App.getInstance().getDataStore()
                .select(RepoFile.class)
                .where(REPO_ID.eq(repoId)
                        .and(LOGIN.eq(login))
                        .and(SHA.eq(sha)))
                .orderBy(TYPE.asc())
                .get()
                .observable();
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
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
    }

    protected AbstractRepoFile(Parcel in) {
        this.id = in.readLong();
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
    }

    public static final Creator<RepoFile> CREATOR = new Creator<RepoFile>() {
        @Override public RepoFile createFromParcel(Parcel source) {return new RepoFile(source);}

        @Override public RepoFile[] newArray(int size) {return new RepoFile[size];}
    };
}
