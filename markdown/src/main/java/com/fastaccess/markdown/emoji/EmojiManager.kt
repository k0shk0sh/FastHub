package com.fastaccess.markdown.emoji

import android.content.Context
import com.fastaccess.domain.rx.FastHubSubscriber
import io.reactivex.Observable
import java.io.IOException
import java.util.*

/**
 * Holds the loaded emojis and provides search functions.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
object EmojiManager {
    private val PATH = "emojis.json"
    private val EMOJIS_BY_ALIAS = hashMapOf<String, Emoji>()
    private val EMOJIS_BY_TAG = hashMapOf<String, HashSet<Emoji>>()
    var all: List<Emoji>? = null
        private set
    private var EMOJI_TRIE: EmojiTrie? = null

    val allTags: Collection<String>
        get() = EMOJIS_BY_TAG.keys

    fun load(context: Context) {
        Observable.fromCallable {
            try {
                val stream = context.assets.open(PATH)
                val emojis = EmojiLoader.loadEmojis(stream)
                all = emojis
                for (emoji in emojis) {
                    for (tag in emoji.tags) {
                        if (EMOJIS_BY_TAG[tag] == null) {
                            EMOJIS_BY_TAG[tag] = HashSet()
                        }
                        EMOJIS_BY_TAG[tag]?.add(emoji)
                    }
                    for (alias in emoji.aliases) {
                        EMOJIS_BY_ALIAS[alias] = emoji
                    }
                }
                EMOJI_TRIE = EmojiTrie(emojis)
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            true
        }.subscribe(FastHubSubscriber())
    }

    fun getForTag(tag: String?): Set<Emoji>? {
        return if (tag == null) {
            null
        } else EMOJIS_BY_TAG[tag]
    }

    fun getForAlias(alias: String?): Emoji? {
        return if (alias == null) {
            null
        } else EMOJIS_BY_ALIAS[trimAlias(alias)]
    }

    private fun trimAlias(alias: String): String {
        var result = alias
        if (result.startsWith(":")) {
            result = result.substring(1, result.length)
        }
        if (result.endsWith(":")) {
            result = result.substring(0, result.length - 1)
        }
        return result
    }

    fun getByUnicode(unicode: String?): Emoji? {
        return if (unicode == null) {
            null
        } else EMOJI_TRIE?.getEmoji(unicode)
    }

    fun isEmoji(string: String?): Boolean {
        return string != null && EMOJI_TRIE?.isEmoji(string.toCharArray())?.exactMatch() == true
    }

    fun isOnlyEmojis(string: String?): Boolean {
        return string != null && EmojiParser.removeAllEmojis(string).isEmpty()
    }

    fun isEmoji(sequence: CharArray): EmojiTrie.Matches? {
        return EMOJI_TRIE?.isEmoji(sequence)
    }
}
