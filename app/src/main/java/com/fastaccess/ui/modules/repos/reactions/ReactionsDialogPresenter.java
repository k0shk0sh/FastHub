package com.fastaccess.ui.modules.repos.reactions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.model.ReactionsModel;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Kosh on 11 Apr 2017, 11:20 AM
 */

public class ReactionsDialogPresenter extends BasePresenter<ReactionsDialogMvp.View> implements ReactionsDialogMvp.Presenter {
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    private ArrayList<User> users = new ArrayList<>();
    private String login;
    private String repoId;
    private long id;
    private ReactionTypes reactionType;
    private boolean isHeader;

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle != null) {
            repoId = bundle.getString(BundleConstant.EXTRA);
            login = bundle.getString(BundleConstant.EXTRA_TWO);
            id = bundle.getLong(BundleConstant.ID);
            reactionType = (ReactionTypes) bundle.getSerializable(BundleConstant.EXTRA_TYPE);
            isHeader = bundle.getBoolean(BundleConstant.EXTRA_THREE);
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

    @Override public void onCallApi(int page, @Nullable Object parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0 || (login == null || repoId == null || reactionType == null)) {
            sendToView(ReactionsDialogMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        Observable<Pageable<ReactionsModel>> observable = RestProvider.getReactionsService()
                .getIssueCommentReaction(login, repoId, id, reactionType.getContent());
        if (isHeader) {
            observable = RestProvider.getReactionsService()
                    .getIssueReaction(login, repoId, id, reactionType.getContent());
        }
        makeRestCall(observable,
                response -> {
                    lastPage = response.getLast();
                    sendToView(view -> view.onNotifyAdapter(Stream.of(response.getItems())
                            .filter(reactionsModel -> reactionsModel.getUser() != null)
                            .map(ReactionsModel::getUser)
                            .collect(Collectors.toList()), page));
                });
    }

    ReactionTypes getReactionType() {
        return reactionType;
    }
}
