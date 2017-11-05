package com.fastaccess.data.dao.types;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;

import com.annimon.stream.Stream;
import com.fastaccess.R;

/**
 * Created by Kosh on 29 Mar 2017, 10:11 PM
 */

public enum ReactionTypes {

    HEART("heart", R.id.heart, R.id.heartReaction),
    HOORAY("hooray", R.id.hurray, R.id.hurrayReaction),
    PLUS_ONE("+1", R.id.thumbsUp, R.id.thumbsUpReaction),
    MINUS_ONE("-1", R.id.thumbsDown, R.id.thumbsDownReaction),
    CONFUSED("confused", R.id.sad, R.id.sadReaction),
    LAUGH("laugh", R.id.laugh, R.id.laughReaction);

    private String content;
    private int vId;
    private int secondaryViewId;

    ReactionTypes(String content, int vId, int secondaryViewId) {
        this.content = content;
        this.vId = vId;
        this.secondaryViewId = secondaryViewId;
    }

    public String getContent() {
        return content;
    }

    @IdRes public int getvId() {
        return vId;
    }

    @Nullable public static ReactionTypes get(@IdRes int vId) {
        return Stream.of(values())
                .filter(value -> value.getvId() == vId || value.secondaryViewId == vId)
                .findFirst()
                .orElse(null);
    }
}
