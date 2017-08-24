package com.fastaccess.provider.markdown.extension.mention.internal;

import com.fastaccess.provider.markdown.extension.mention.Mention;

import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;

public class MentionDelimiterProcessor implements DelimiterProcessor {

    @Override public char getOpeningCharacter() {
        return '@';
    }

    @Override public char getClosingCharacter() {
        return ' ';
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
        Node mention = new Mention();
        Node tmp = opener.getNext();
        while (tmp != null && tmp != closer) {
            Node next = tmp.getNext();
            mention.appendChild(tmp);
            tmp = next;
        }
        opener.insertAfter(mention);
    }
}
