package com.fastaccess.provider.emoji;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EmojiTrie {
    private Node root = new Node();

    public EmojiTrie(Collection<Emoji> emojis) {
        for (Emoji emoji : emojis) {
            Node tree = root;
            for (char c : emoji.getUnicode().toCharArray()) {
                if (!tree.hasChild(c)) {
                    tree.addChild(c);
                }
                tree = tree.getChild(c);
            }
            tree.setEmoji(emoji);
        }
    }


    /**
     * Checks if sequence of chars contain an emoji.
     *
     * @param sequence
     *         Sequence of char that may contain emoji in full or partially.
     * @return &lt;li&gt; Matches.EXACTLY if char sequence in its entirety is an emoji &lt;/li&gt; &lt;li&gt; Matches.POSSIBLY if char sequence
     * matches prefix of an emoji &lt;/li&gt; &lt;li&gt; Matches.IMPOSSIBLE if char sequence matches no emoji or prefix of an emoji &lt;/li&gt;
     */
    public Matches isEmoji(char[] sequence) {
        if (sequence == null) {
            return Matches.POSSIBLY;
        }

        Node tree = root;
        for (char c : sequence) {
            if (!tree.hasChild(c)) {
                return Matches.IMPOSSIBLE;
            }
            tree = tree.getChild(c);
        }

        return tree.isEndOfEmoji() ? Matches.EXACTLY : Matches.POSSIBLY;
    }


    /**
     * Finds Emoji instance from emoji unicode
     *
     * @param unicode
     *         unicode of emoji to get
     * @return Emoji instance if unicode matches and emoji, null otherwise.
     */
    public Emoji getEmoji(String unicode) {
        Node tree = root;
        for (char c : unicode.toCharArray()) {
            if (!tree.hasChild(c)) {
                return null;
            }
            tree = tree.getChild(c);
        }
        return tree.getEmoji();
    }

    public enum Matches {
        EXACTLY, POSSIBLY, IMPOSSIBLE;

        public boolean exactMatch() {
            return this == EXACTLY;
        }

        public boolean impossibleMatch() {
            return this == IMPOSSIBLE;
        }

        public boolean possibleMatch() {
            return this == POSSIBLY;
        }
    }

    private class Node {
        private Map<Character, Node> children = new HashMap<Character, Node>();
        private Emoji emoji;

        private void setEmoji(Emoji emoji) {
            this.emoji = emoji;
        }

        private Emoji getEmoji() {
            return emoji;
        }

        private boolean hasChild(char child) {
            return children.containsKey(child);
        }

        private void addChild(char child) {
            children.put(child, new Node());
        }

        private Node getChild(char child) {
            return children.get(child);
        }

        private boolean isEndOfEmoji() {
            return emoji != null;
        }
    }
}
