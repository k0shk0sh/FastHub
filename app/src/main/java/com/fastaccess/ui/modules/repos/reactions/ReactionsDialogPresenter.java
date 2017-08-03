package com.fastaccess.ui.modules.repos.reactions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.ReactionsModel;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.timeline.ReactionsProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by Kosh on 11 Apr 2017, 11:20 AM
 */

public class ReactionsDialogPresenter extends BasePresenter<ReactionsDialogMvp.View> implements ReactionsDialogMvp.Presenter {
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    private ArrayList<User> users = new ArrayList<>();
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State String repoId;
    @com.evernote.android.state.State long id;
    @com.evernote.android.state.State ReactionTypes reactionType;
    @com.evernote.android.state.State @ReactionsProvider.ReactionType int reactionTypeMode;

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle != null) {
            repoId = bundle.getString(BundleConstant.EXTRA);
            login = bundle.getString(BundleConstant.EXTRA_TWO);
            id = bundle.getLong(BundleConstant.ID);
            reactionType = (ReactionTypes) bundle.getSerializable(BundleConstant.EXTRA_TYPE);
            reactionTypeMode = bundle.getInt(BundleConstant.EXTRA_THREE);
            onCallApi(1, null);
        }
    }

    @NonNull @Override public ArrayList<User> getUsers() {
        return users;
    }

    @Override public int getCurrentPage() {
        return page;
    }

    @Override public int getPreviousTotal() {
        return previousTotal;
    }

    @Override public void setCurrentPage(int page) {
        this.page = page;
    }

    @Override public void setPreviousTotal(int previousTotal) {
        this.previousTotal = previousTotal;
    }

    @Override public boolean onCallApi(int page, @Nullable Object parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0 || (login == null || repoId == null || reactionType == null)) {
            sendToView(ReactionsDialogMvp.View::hideProgress);
            return false;
        }
        setCurrentPage(page);
        Observable<Pageable<ReactionsModel>> observable = null;
        switch (reactionTypeMode) {
            case ReactionsProvider.COMMENT:
                observable = RestProvider.getReactionsService(isEnterprise())
                        .getIssueCommentReaction(login, repoId, id, reactionType.getContent(), page);
                break;
            case ReactionsProvider.COMMIT:
                observable = RestProvider.getReactionsService(isEnterprise())
                        .getCommitReaction(login, repoId, id, reactionType.getContent(), page);
                break;
            case ReactionsProvider.HEADER:
                observable = RestProvider.getReactionsService(isEnterprise())
                        .getIssueReaction(login, repoId, id, reactionType.getContent(), page);
                break;
            case ReactionsProvider.REVIEW_COMMENT:
                observable = RestProvider.getReactionsService(isEnterprise())
                        .getPullRequestReactions(login, repoId, id, reactionType.getContent(), page);
                break;
        }
        if (observable == null) {
            throw new NullPointerException("Reaction is null?");
        }
        makeRestCall(observable, response -> {
            lastPage = response.getLast();
            sendToView(view -> view.onNotifyAdapter(Stream.of(response.getItems())
                    .filter(reactionsModel -> reactionsModel.getUser() != null)
                    .map(ReactionsModel::getUser)
                    .collect(Collectors.toList()), page));
        });
        return true;
    }

    ReactionTypes getReactionType() {
        return reactionType;
    }
}
