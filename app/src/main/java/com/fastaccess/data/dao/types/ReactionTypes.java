package com.fastaccess.data.dao.types;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;

import com.fastaccess.R;

/**
 * Created by Kosh on 29 Mar 2017, 10:11 PM
 */

public enum ReactionTypes {

    HEART("heart", R.id.heart),
    HOORAY("hooray", R.id.hurray),
    PLUS_ONE("+1", R.id.thumbsUp),
    MINUS_ONE("-1", R.id.thumbsDown),
    CONFUSED("confused", R.id.sad),
    LAUGH("laugh", R.id.laugh);

    private String content;
    private int vId;

    ReactionTypes(String content, int vId) {
        this.content = content;
        this.vId = vId;
    }

    public String getContent() {
        return content;
    }

    @IdRes public int getvId() {
        return vId;
    }

    @Nullable public static ReactionTypes get(@IdRes int vId) {
        for (ReactionTypes type : ReactionTypes.values()) {
            if (type.vId == vId) return type;
        }

        return null;
    }
}
