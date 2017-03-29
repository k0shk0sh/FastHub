package com.fastaccess.data.dao.types;

/**
 * Created by Kosh on 29 Mar 2017, 10:11 PM
 */

public enum ReactionTypes {

    HEART("heart"),
    HOORAY("hooray"),
    PLUS_ONE("+1"),
    MINUS_ONE("-1"),
    CONFUSED("confused"),
    LAUGH("laugh");

    private String content;

    ReactionTypes(String content) {this.content = content;}

    public String getContent() {
        return content;
    }
}
