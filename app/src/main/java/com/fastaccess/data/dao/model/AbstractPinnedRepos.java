package com.fastaccess.data.dao.model;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.App;
import com.fastaccess.data.dao.converters.RepoConverter;

import java.util.List;

import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import lombok.NoArgsConstructor;
import rx.Observable;
import rx.Single;

import static com.fastaccess.data.dao.model.PinnedRepos.ID;
import static com.fastaccess.data.dao.model.PinnedRepos.REPO_FULL_NAME;

/**
 * Created by Kosh on 25 Mar 2017, 7:29 PM
 */

@Entity @NoArgsConstructor public abstract class AbstractPinnedRepos implements Parcelable {
    @Key @Generated long id;
    @Column(unique = true) String repoFullName;
    @Convert(RepoConverter.class) Repo pinnedRepo;

    public static Single<PinnedRepos> save(@NonNull PinnedRepos entity) {
        return App.getInstance().getDataStore().insert(entity);
    }

    public static boolean pinUpin(@NonNull Repo repo) {
        PinnedRepos pinnedRepos = get(repo.getFullName());
        if (pinnedRepos == null) {
            PinnedRepos pinned = new PinnedRepos();
            pinned.setRepoFullName(repo.getFullName());
            pinned.setPinnedRepo(repo);
            save(pinned).toObservable().toBlocking().firstOrDefault(null);
            return true;
        } else {
            delete(pinnedRepos.getId());
            return false;
        }
    }

    @Nullable public static PinnedRepos get(long id) {
        return App.getInstance().getDataStore().select(PinnedRepos.class)
                .where(ID.eq(id))
                .get()
                .firstOrNull();
    }

    @Nullable public static PinnedRepos get(@NonNull String repoFullName) {
        return App.getInstance().getDataStore().select(PinnedRepos.class)
                .where(REPO_FULL_NAME.eq(repoFullName))
                .get()
                .firstOrNull();
    }

    public static boolean isPinned(@NonNull String repoFullName) {
        return get(repoFullName) != null;
    }

    @NonNull public static Observable<List<PinnedRepos>> getMyPinnedRepos() {
        return App.getInstance().getDataStore().select(PinnedRepos.class)
                .orderBy(ID.desc())
                .get()
                .toObservable()
                .toList();

    }

    public static void delete(long id) {
        App.getInstance().getDataStore().delete(PinnedRepos.class)
                .where(ID.eq(id))
                .get()
                .value();
    }
}
