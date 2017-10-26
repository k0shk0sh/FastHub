package com.fastaccess.data.dao.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.App;
import com.fastaccess.data.dao.converters.IssueConverter;
import com.fastaccess.helper.RxHelper;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import lombok.NoArgsConstructor;

import static com.fastaccess.data.dao.model.PinnedIssues.ENTRY_COUNT;
import static com.fastaccess.data.dao.model.PinnedIssues.ID;
import static com.fastaccess.data.dao.model.PinnedIssues.LOGIN;

/**
 * Created by Hashemsergani on 14.10.17.
 */

@Entity @NoArgsConstructor public class AbstractPinnedIssues {

    @Key @Generated long id;
    @io.requery.Nullable int entryCount;
    @io.requery.Nullable String login;
    @io.requery.Nullable @Convert(IssueConverter.class) Issue issue;
    @io.requery.Nullable long issueId;

    public static void pinUpin(@NonNull Issue issue) {
        PinnedIssues pinnedIssues = get(issue.getId());
        if (pinnedIssues == null) {
            PinnedIssues pinned = new PinnedIssues();
            pinned.setLogin(Login.getUser().getLogin());
            pinned.setIssue(issue);
            pinned.setIssueId(issue.getId());
            try {
                App.getInstance().getDataStore().toBlocking().insert(pinned);
            } catch (Exception ignored) {}
        } else {
            delete(issue.getId());
        }
    }

    @Nullable public static PinnedIssues get(long issueId) {
        return App.getInstance().getDataStore().select(PinnedIssues.class)
                .where(PinnedIssues.ISSUE_ID.eq(issueId))
                .get()
                .firstOrNull();
    }

    public static void delete(long issueId) {
        App.getInstance().getDataStore().delete(PinnedIssues.class)
                .where(PinnedIssues.ISSUE_ID.eq(issueId))
                .get()
                .value();
    }

    @NonNull public static Disposable updateEntry(long issueId) {
        return RxHelper.getObservable(Observable.fromPublisher(e -> {
            PinnedIssues pinned = get(issueId);
            if (pinned != null) {
                pinned.setEntryCount(pinned.getEntryCount() + 1);
                App.getInstance().getDataStore().toBlocking().update(pinned);
                e.onNext("");
            }
            e.onComplete();
        })).subscribe(o -> {/*do nothing*/}, Throwable::printStackTrace);
    }

    @NonNull public static Single<List<Issue>> getMyPinnedIssues() {
        return App.getInstance().getDataStore().select(PinnedIssues.class)
                .where(LOGIN.eq(Login.getUser().getLogin()).or(LOGIN.isNull()))
                .orderBy(ENTRY_COUNT.desc(), ID.desc())
                .get()
                .observable()
                .map(PinnedIssues::getIssue)
                .toList();
    }

    public static boolean isPinned(long issueId) {
        return get(issueId) != null;
    }

}
