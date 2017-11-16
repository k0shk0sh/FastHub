package com.fastaccess.provider.timeline;

import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.PostReactionModel;
import com.fastaccess.data.dao.ReactionsModel;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by Kosh on 09 Apr 2017, 10:40 AM
 */

public class ReactionsProvider {

    public static final int HEADER = 0;
    public static final int COMMENT = 1;
    public static final int REVIEW_COMMENT = 2;
    public static final int COMMIT = 3;

    @IntDef({
            HEADER,
            COMMENT,
            REVIEW_COMMENT,
            COMMIT
    })
    @Retention(RetentionPolicy.SOURCE) public @interface ReactionType {}

    private Map<Long, ReactionsModel> reactionsMap = new LinkedHashMap<>();

    @Nullable public Observable onHandleReaction(@IdRes int viewId, long idOrNumber, @Nullable String login,
                                                 @Nullable String repoId, @ReactionType int reactionType, boolean isEnterprise) {
        if (!InputHelper.isEmpty(login) && !InputHelper.isEmpty(repoId)) {
            if (!isPreviouslyReacted(idOrNumber, viewId)) {
                ReactionTypes reactionTypes = ReactionTypes.get(viewId);
                if (reactionTypes != null) {
                    Observable<ReactionsModel> observable = null;
                    switch (reactionType) {
                        case COMMENT:
                            observable = RestProvider.getReactionsService(isEnterprise)
                                    .postIssueCommentReaction(new PostReactionModel(reactionTypes.getContent()), login, repoId, idOrNumber);
                            break;
                        case HEADER:
                            observable = RestProvider.getReactionsService(isEnterprise)
                                    .postIssueReaction(new PostReactionModel(reactionTypes.getContent()), login, repoId, idOrNumber);
                            break;
                        case REVIEW_COMMENT:
                            observable = RestProvider.getReactionsService(isEnterprise)
                                    .postCommentReviewReaction(new PostReactionModel(reactionTypes.getContent()), login, repoId, idOrNumber);
                            break;
                        case COMMIT:
                            observable = RestProvider.getReactionsService(isEnterprise)
                                    .postCommitReaction(new PostReactionModel(reactionTypes.getContent()), login, repoId, idOrNumber);
                            break;
                    }
                    if (observable == null) return null;
                    return RxHelper.safeObservable(observable)
                            .doOnNext(response -> getReactionsMap().put(idOrNumber, response));
                }
            } else {
                ReactionsModel reactionsModel = getReactionsMap().get(idOrNumber);
                if (reactionsModel != null) {
                    return RxHelper.safeObservable(RestProvider.getReactionsService(isEnterprise).delete(reactionsModel.getId()))
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

    public boolean isCallingApi(long id, int vId) {
        ReactionsModel reactionsModel = getReactionsMap().get(id);
        if (reactionsModel == null || InputHelper.isEmpty(reactionsModel.getContent())) {
            return false;
        }
        ReactionTypes type = ReactionTypes.get(vId);
        return type != null && type.getContent().equals(reactionsModel.getContent()) && reactionsModel.isCallingApi();
    }

    @NonNull private Map<Long, ReactionsModel> getReactionsMap() {
        return reactionsMap;
    }
}
