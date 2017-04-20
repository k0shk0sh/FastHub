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

    @Nullable public Observable onHandleReaction(@IdRes int id, long commentId, @Nullable String login, @Nullable String repoId) {
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            if (!isPreviouslyReacted(commentId, id)) {
                ReactionTypes reactionTypes = ReactionTypes.get(id);
                if (reactionTypes != null) {
                    return RxHelper.safeObservable(RestProvider.getReactionsService()
                            .postIssueReaction(new PostReactionModel(reactionTypes.getContent()), login, repoId, commentId))
                            .doOnNext(response -> getReactionsMap().put(commentId, response));
                }
            } else {
                ReactionsModel reactionsModel = getReactionsMap().get(commentId);
                if (reactionsModel != null) {
                    return RxHelper.safeObservable(RestProvider.getReactionsService().delete(reactionsModel.getId()))
                            .doOnNext(booleanResponse -> {
                                if (booleanResponse.code() == 204) {
                                    getReactionsMap().remove(commentId);
                                }
                            });
                }
            }
        }
        return null;
    }

    public boolean isPreviouslyReacted(long commentId, int vId) {
        ReactionsModel reactionsModel = getReactionsMap().get(commentId);
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
