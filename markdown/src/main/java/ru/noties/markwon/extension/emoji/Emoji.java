package ru.noties.markwon.extension.emoji;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;

/**
 * Created by kosh on 20/08/2017.
 */

public class Emoji extends CustomNode implements Delimited {

    private static final String DELIMITER = ":";
    private String emoji;

    @Override public String getOpeningDelimiter() {
        return DELIMITER;
    }

    @Override public String getClosingDelimiter() {
        return DELIMITER;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
