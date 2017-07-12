package com.fastaccess.data.dao.model;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.App;
import com.fastaccess.data.dao.converters.RepoConverter;
import com.fastaccess.helper.RxHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import lombok.NoArgsConstructor;

import static com.fastaccess.data.dao.model.PinnedRepos.ENTRY_COUNT;
import static com.fastaccess.data.dao.model.PinnedRepos.ID;
import static com.fastaccess.data.dao.model.PinnedRepos.REPO_FULL_NAME;

/**
 * Created by Kosh on 25 Mar 2017, 7:29 PM
 */

@Entity @NoArgsConstructor public abstract class AbstractPinnedRepos implements Parcelable {
    @Key @Generated long id;
    @Column(unique = true) String repoFullName;
    @Convert(RepoConverter.class) Repo pinnedRepo;
    int entryCount;

    public static Single<PinnedRepos> update(@NonNull PinnedRepos entity) {
        return RxHelper.getSingle(App.getInstance().getDataStore().update(entity));
    }

    public static boolean pinUpin(@NonNull Repo repo) {
        PinnedRepos pinnedRepos = get(repo.getFullName());
        if (pinnedRepos == null) {
            PinnedRepos pinned = new PinnedRepos();
            pinned.setRepoFullName(repo.getFullName());
            pinned.setPinnedRepo(repo);
            App.getInstance().getDataStore().insert(pinned).blockingGet();
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

    public static Disposable updateEntry(@NonNull String repoFullName) {
        return Observable.fromPublisher(e -> {
            PinnedRepos pinned = get(repoFullName);
            if (pinned != null) {
                pinned.setEntryCount(pinned.getEntryCount() + 1);
                App.getInstance().getDataStore().toBlocking().update(pinned);
                e.onNext("");
            }
            e.onComplete();
        }).subscribe(o -> {/*do nothing*/}, Throwable::printStackTrace);
    }

    @NonNull public static Single<List<PinnedRepos>> getMyPinnedRepos() {
        return App.getInstance().getDataStore().select(PinnedRepos.class)
                .orderBy(ID.desc(), ENTRY_COUNT.desc())
                .get()
                .observable()
                .toList();

    }

    @NonNull public static Observable<List<PinnedRepos>> getMenuRepos() {
        return App.getInstance().getDataStore().select(PinnedRepos.class)
                .orderBy(ID.desc(), ENTRY_COUNT.desc())
                .limit(10)
                .get()
                .observable()
                .toList()
                .toObservable();
    }

    public static void delete(long id) {
        App.getInstance().getDataStore().delete(PinnedRepos.class)
                .where(ID.eq(id))
                .get()
                .value();
    }
}
