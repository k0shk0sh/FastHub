package com.fastaccess.provider.emoji;

import com.fastaccess.App;
import com.fastaccess.helper.RxHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Holds the loaded emojis and provides search functions.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
public class EmojiManager {
    private static final String PATH = "emojis.json";
    private static final Map<String, Emoji> EMOJIS_BY_ALIAS = new HashMap<>();
    private static final Map<String, Set<Emoji>> EMOJIS_BY_TAG = new HashMap<>();
    private static List<Emoji> ALL_EMOJIS;
    private static EmojiTrie EMOJI_TRIE;

    public static void load() {
        RxHelper.safeObservable(Observable.fromCallable(() -> {
            try {
                InputStream stream = App.getInstance().getAssets().open(PATH);
                List<Emoji> emojis = EmojiLoader.loadEmojis(stream);
                ALL_EMOJIS = emojis;
                for (Emoji emoji : emojis) {
                    for (String tag : emoji.getTags()) {
                        if (EMOJIS_BY_TAG.get(tag) == null) {
                            EMOJIS_BY_TAG.put(tag, new HashSet<>());
                        }
                        EMOJIS_BY_TAG.get(tag).add(emoji);
                    }
                    for (String alias : emoji.getAliases()) {
                        EMOJIS_BY_ALIAS.put(alias, emoji);
                    }
                }
                EMOJI_TRIE = new EmojiTrie(emojis);
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        })).subscribeOn(Schedulers.io()).subscribe();
    }

    private EmojiManager() {}

    public static Set<Emoji> getForTag(String tag) {
        if (tag == null) {
            return null;
        }
        return EMOJIS_BY_TAG.get(tag);
    }

    public static Emoji getForAlias(String alias) {
        if (alias == null) {
            return null;
        }
        return EMOJIS_BY_ALIAS.get(trimAlias(alias));
    }

    private static String trimAlias(String alias) {
        String result = alias;
        if (result.startsWith(":")) {
            result = result.substring(1, result.length());
        }
        if (result.endsWith(":")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static Emoji getByUnicode(String unicode) {
        if (unicode == null) {
            return null;
        }
        return EMOJI_TRIE.getEmoji(unicode);
    }

    public static List<Emoji> getAll() {
        return ALL_EMOJIS;
    }

    public static boolean isEmoji(String string) {
        return string != null &&
                EMOJI_TRIE.isEmoji(string.toCharArray()).exactMatch();
    }

    public static boolean isOnlyEmojis(String string) {
        return string != null && EmojiParser.removeAllEmojis(string).isEmpty();
    }

    public static EmojiTrie.Matches isEmoji(char[] sequence) {
        return EMOJI_TRIE.isEmoji(sequence);
    }

    public static Collection<String> getAllTags() {
        return EMOJIS_BY_TAG.keySet();
    }
}
