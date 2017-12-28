package ru.noties.markwon.extension.emoji.internal;

import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;

import ru.noties.markwon.extension.emoji.Emoji;

public class EmojiDelimiterProcessor implements DelimiterProcessor {

    @Override public char getOpeningCharacter() {
        return ':';
    }

    @Override public char getClosingCharacter() {
        return ':';
    }

    @Override public int getMinLength() {
        return 1;
    }

    @Override public int getDelimiterUse(DelimiterRun opener, DelimiterRun closer) {
        if (opener.length() >= 1 && closer.length() >= 1) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override public void process(Text opener, Text closer, int delimiterCount) {
        Emoji emoji = null;
        Node text = opener.getNext();
        if (text instanceof Text) {
            emoji = new Emoji();
            emoji.setEmoji(((Text) text).getLiteral());
            text.unlink();
        }
        if (emoji != null) opener.insertAfter(emoji);
    }
}
