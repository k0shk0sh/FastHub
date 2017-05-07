package com.fastaccess.provider.timeline;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.PostReactionModel;
import com.fastaccess.data.dao.model.ReactionsModel;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;

import java.util.LinkedHashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by Kosh on 09 Apr 2017, 10:40 AM
 */

public class ReactionsProvider {
    private Map<Long, ReactionsModel> reactionsMap = new LinkedHashMap<>();

    @Nullable public Observable onHandleReaction(@IdRes int viewId, long idOrNumber, @Nullable String login,
                                                 @Nullable String repoId, boolean isHeader) {
        return onHandleReaction(viewId, idOrNumber, login, repoId, isHeader, false);
    }

    @Nullable public Observable onHandleReaction(@IdRes int viewId, long idOrNumber, @Nullable String login,
                                                 @Nullable String repoId, boolean isHeader, boolean isCommit) {
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            if (!isPreviouslyReacted(idOrNumber, viewId)) {
                ReactionTypes reactionTypes = ReactionTypes.get(viewId);
                if (reactionTypes != null) {
                    Observable<ReactionsModel> observable = RestProvider.getReactionsService()
                            .postIssueCommentReaction(new PostReactionModel(reactionTypes.getContent()), login, repoId, idOrNumber);
                    if (isHeader) {
                        observable = RestProvider.getReactionsService()
                                .postIssueReaction(new PostReactionModel(reactionTypes.getContent()), login, repoId, idOrNumber);
                    }
                    if (isCommit) {
                        observable = RestProvider.getReactionsService()
                                .postCommitReaction(new PostReactionModel(reactionTypes.getContent()), login, repoId, idOrNumber);
                    }
                    return RxHelper.safeObservable(observable)
                            .doOnNext(response -> getReactionsMap().put(idOrNumber, response));
                }
            } else {
                ReactionsModel reactionsModel = getReactionsMap().get(idOrNumber);
                if (reactionsModel != null) {
                    return RxHelper.safeObservable(RestProvider.getReactionsService().delete(reactionsModel.getId()))
                            .doOnNext(booleanResponse -> {
                                if (booleanResponse.code() == 204) {
                                    getReactionsMap().remove(idOrNumber);
                                }
                            });
                }
            }
        }
        return null;
    }

    public boolean isPreviouslyReacted(long idOrNumber, @IdRes int vId) {
        ReactionsModel reactionsModel = getReactionsMap().get(idOrNumber);
        if (reactionsModel == null || InputHelper.isEmpty(reactionsModel.getContent())) {
            return false;
        }
        ReactionTypes type = ReactionTypes.get(vId);
        return type != null && type.getContent().equals(reactionsModel.getContent());
    }

    @NonNull private Map<Long, ReactionsModel> getReactionsMap() {
        return reactionsMap;
    }
}
