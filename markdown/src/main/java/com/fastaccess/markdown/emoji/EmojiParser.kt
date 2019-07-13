package com.fastaccess.markdown.emoji

import java.util.*
import java.util.regex.Pattern

/**
 * Provides methods to parse strings with emojis.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
object EmojiParser {
    private val ALIAS_CANDIDATE_PATTERN = Pattern.compile("(?<=:)\\+?(\\w|\\||\\-)+(?=:)")

    /**
     * See [.parseToAliases] with the action
     * "PARSE"
     *
     * @param input the string to parse
     *
     * @return the string with the emojis replaced by their alias.
     */
    fun parseToAliases(input: String): String {
        return parseToAliases(input, FitzpatrickAction.PARSE)
    }

    /**
     * Replaces the emoji's unicode occurrences by one of their alias
     * (between 2 ':').<br></br>
     * Example: `😄` will be replaced by `:smile:`<br></br>
     * <br></br>
     * When a fitzpatrick modifier is present with a PARSE action, a "|" will be
     * appendend to the alias, with the fitzpatrick type.<br></br>
     * Example: `👦🏿` will be replaced by
     * `:boy|type_6:`<br></br>
     * The fitzpatrick types are: type_1_2, type_3, type_4, type_5, type_6<br></br>
     * <br></br>
     * When a fitzpatrick modifier is present with a REMOVE action, the modifier
     * will be deleted.<br></br>
     * Example: `👦🏿` will be replaced by `:boy:`<br></br>
     * <br></br>
     * When a fitzpatrick modifier is present with a IGNORE action, the modifier
     * will be ignored.<br></br>
     * Example: `👦🏿` will be replaced by `:boy:🏿`<br></br>
     *
     * @param input             the string to parse
     * @param fitzpatrickAction the action to apply for the fitzpatrick modifiers
     *
     * @return the string with the emojis replaced by their alias.
     */
    private fun parseToAliases(
            input: String,
            fitzpatrickAction: FitzpatrickAction
    ): String {
        val emojiTransformer = object : EmojiTransformer {
            override fun transform(unicodeCandidate: UnicodeCandidate): String {
                when (fitzpatrickAction) {
                    EmojiParser.FitzpatrickAction.PARSE -> {
                        return if (unicodeCandidate.hasFitzpatrick()) {
                            ":" + unicodeCandidate.emoji.aliases[0] + "|" + unicodeCandidate.fitzpatrickType + ":"
                        } else ":" + unicodeCandidate.emoji.aliases[0] + ":"
                    }
                    EmojiParser.FitzpatrickAction.REMOVE -> return ":" + unicodeCandidate.emoji.aliases[0] + ":"
                    EmojiParser.FitzpatrickAction.IGNORE -> return ":" +
                            unicodeCandidate.emoji.aliases[0] +
                            ":" +
                            unicodeCandidate.fitzpatrickUnicode
                    else -> {
                        return if (unicodeCandidate.hasFitzpatrick()) {
                            ":" + unicodeCandidate.emoji.aliases[0] + "|" + unicodeCandidate.fitzpatrickType + ":"
                        } else ":" + unicodeCandidate.emoji.aliases[0] + ":"
                    }
                }
            }
        }
        return parseFromUnicode(input, emojiTransformer)
    }


    /**
     * Replaces the emoji's aliases (between 2 ':') occurrences and the html
     * representations by their unicode.<br></br>
     * Examples:<br></br>
     * `:smile:` will be replaced by `😄`<br></br>
     * `&#128516;` will be replaced by `😄`<br></br>
     * `:boy|type_6:` will be replaced by `👦🏿`
     *
     * @param input the string to parse
     *
     * @return the string with the aliases and html representations replaced by
     * their unicode.
     */
    fun parseToUnicode(input: String): String {
        // Get all the potential aliases
        val candidates = getAliasCandidates(input)

        // Replace the aliases by their unicode
        var result = input
        for (candidate in candidates) {
            val emoji = EmojiManager.getForAlias(candidate.alias)
            if (emoji != null) {
                if (emoji.supportsFitzpatrick() || !emoji.supportsFitzpatrick() && candidate.fitzpatrick == null) {
                    var replacement = emoji.unicode
                    if (candidate.fitzpatrick != null) {
                        replacement += candidate.fitzpatrick.unicode
                    }
                    result = result.replace(":" + candidate.fullString + ":", replacement ?: "", true)
                }
            }
        }

        // Replace the html
        for (emoji in EmojiManager.all!!) {
            result = result.apply {
                emoji.htmlHexadecimal?.let { replace(it, emoji.unicode ?: "", true) }
                emoji.htmlDecimal?.let { replace(it, emoji.unicode ?: "", true) }
            }
        }

        return result
    }

    private fun getAliasCandidates(input: String): List<AliasCandidate> {
        val candidates = ArrayList<AliasCandidate>()

        var matcher = ALIAS_CANDIDATE_PATTERN.matcher(input)
        matcher = matcher.useTransparentBounds(true)
        while (matcher.find()) {
            val match = matcher.group()
            if (!match.contains("|")) {
                candidates.add(AliasCandidate(match, match, null))
            } else {
                val splitted = match.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (splitted.size == 2 || splitted.size > 2) {
                    candidates.add(AliasCandidate(match, splitted[0], splitted[1]))
                } else {
                    candidates.add(AliasCandidate(match, match, null))
                }
            }
        }
        return candidates
    }

    /**
     * See [.parseToHtmlDecimal] with the action
     * "PARSE"
     *
     * @param input the string to parse
     *
     * @return the string with the emojis replaced by their html decimal
     * representation.
     */
    fun parseToHtmlDecimal(input: String): String {
        return parseToHtmlDecimal(input, FitzpatrickAction.PARSE)
    }

    /**
     * Replaces the emoji's unicode occurrences by their html representation.<br></br>
     * Example: `😄` will be replaced by `&#128516;`<br></br>
     * <br></br>
     * When a fitzpatrick modifier is present with a PARSE or REMOVE action, the
     * modifier will be deleted from the string.<br></br>
     * Example: `👦🏿` will be replaced by
     * `&#128102;`<br></br>
     * <br></br>
     * When a fitzpatrick modifier is present with a IGNORE action, the modifier
     * will be ignored and will remain in the string.<br></br>
     * Example: `👦🏿` will be replaced by
     * `&#128102;🏿`
     *
     * @param input             the string to parse
     * @param fitzpatrickAction the action to apply for the fitzpatrick modifiers
     *
     * @return the string with the emojis replaced by their html decimal
     * representation.
     */
    private fun parseToHtmlDecimal(
            input: String,
            fitzpatrickAction: FitzpatrickAction
    ): String {
        val emojiTransformer = object : EmojiTransformer {
            override fun transform(unicodeCandidate: UnicodeCandidate): String {
                return when (fitzpatrickAction) {
                    EmojiParser.FitzpatrickAction.PARSE, EmojiParser.FitzpatrickAction.REMOVE -> unicodeCandidate.emoji.htmlDecimal ?: ""
                    EmojiParser.FitzpatrickAction.IGNORE -> unicodeCandidate.emoji.htmlDecimal + unicodeCandidate.fitzpatrickUnicode
                }
            }

        }

        return parseFromUnicode(input, emojiTransformer)
    }

    /**
     * See [.parseToHtmlHexadecimal] with the
     * action "PARSE"
     *
     * @param input the string to parse
     *
     * @return the string with the emojis replaced by their html hex
     * representation.
     */
    fun parseToHtmlHexadecimal(input: String): String {
        return parseToHtmlHexadecimal(input, FitzpatrickAction.PARSE)
    }

    /**
     * Replaces the emoji's unicode occurrences by their html hex
     * representation.<br></br>
     * Example: `👦` will be replaced by `&#x1f466;`<br></br>
     * <br></br>
     * When a fitzpatrick modifier is present with a PARSE or REMOVE action, the
     * modifier will be deleted.<br></br>
     * Example: `👦🏿` will be replaced by
     * `&#x1f466;`<br></br>
     * <br></br>
     * When a fitzpatrick modifier is present with a IGNORE action, the modifier
     * will be ignored and will remain in the string.<br></br>
     * Example: `👦🏿` will be replaced by
     * `&#x1f466;🏿`
     *
     * @param input             the string to parse
     * @param fitzpatrickAction the action to apply for the fitzpatrick modifiers
     *
     * @return the string with the emojis replaced by their html hex
     * representation.
     */
    private fun parseToHtmlHexadecimal(
            input: String,
            fitzpatrickAction: FitzpatrickAction
    ): String {
        val emojiTransformer = object : EmojiTransformer {
            override fun transform(unicodeCandidate: UnicodeCandidate): String {
                return when (fitzpatrickAction) {
                    EmojiParser.FitzpatrickAction.PARSE, EmojiParser.FitzpatrickAction.REMOVE -> unicodeCandidate.emoji.htmlHexadecimal ?: ""
                    EmojiParser.FitzpatrickAction.IGNORE -> unicodeCandidate.emoji.htmlHexadecimal + unicodeCandidate.fitzpatrickUnicode
                }
            }
        }

        return parseFromUnicode(input, emojiTransformer)
    }

    /**
     * Removes all emojis from a String
     *
     * @param str the string to process
     *
     * @return the string without any emoji
     */
    fun removeAllEmojis(str: String): String {
        val emojiTransformer = object : EmojiTransformer {
            override fun transform(unicodeCandidate: UnicodeCandidate): String {
                return ""
            }
        }
        return parseFromUnicode(str, emojiTransformer)
    }


    /**
     * Removes a set of emojis from a String
     *
     * @param str            the string to process
     * @param emojisToRemove the emojis to remove from this string
     *
     * @return the string without the emojis that were removed
     */
    fun removeEmojis(
            str: String,
            emojisToRemove: Collection<Emoji>
    ): String {
        val emojiTransformer = object : EmojiTransformer {
            override fun transform(unicodeCandidate: UnicodeCandidate): String {
                if (!emojisToRemove.contains(unicodeCandidate.emoji)) {
                    return unicodeCandidate.emoji.unicode + unicodeCandidate.fitzpatrickUnicode
                }
                return ""
            }
        }
        return parseFromUnicode(str, emojiTransformer)
    }

    /**
     * Removes all the emojis in a String except a provided set
     *
     * @param str          the string to process
     * @param emojisToKeep the emojis to keep in this string
     *
     * @return the string without the emojis that were removed
     */
    fun removeAllEmojisExcept(
            str: String,
            emojisToKeep: Collection<Emoji>
    ): String {
        return parseFromUnicode(str, object : EmojiTransformer {
            override fun transform(unicodeCandidate: UnicodeCandidate): String {
                if (emojisToKeep.contains(unicodeCandidate.emoji)) {
                    return unicodeCandidate.emoji.unicode + unicodeCandidate.fitzpatrickUnicode
                }
                return ""
            }
        })
    }


    /**
     * Detects all unicode emojis in input string and replaces them with the
     * return value of transformer.transform()
     *
     * @param input the string to process
     * @param transformer emoji transformer to apply to each emoji
     *
     * @return input string with all emojis transformed
     */
    private fun parseFromUnicode(
            input: String,
            transformer: EmojiTransformer
    ): String {
        var prev = 0
        val sb = StringBuilder()
        val replacements = getUnicodeCandidates(input)
        for (candidate in replacements) {
            sb.append(input.substring(prev, candidate.emojiStartIndex))

            sb.append(transformer.transform(candidate))
            prev = candidate.fitzpatrickEndIndex
        }

        return sb.append(input.substring(prev)).toString()
    }

    fun extractEmojis(input: String): List<String> {
        val emojis = getUnicodeCandidates(input)
        val result = ArrayList<String>()
        for (emoji in emojis) {
            emoji.emoji.unicode?.let { result.add(it) }
        }
        return result
    }


    /**
     * Generates a list UnicodeCandidates found in input string. A
     * UnicodeCandidate is created for every unicode emoticon found in input
     * string, additionally if Fitzpatrick modifier follows the emoji, it is
     * included in UnicodeCandidate. Finally, it contains start and end index of
     * unicode emoji itself (WITHOUT Fitzpatrick modifier whether it is there or
     * not!).
     *
     * @param input String to find all unicode emojis in
     * @return List of UnicodeCandidates for each unicode emote in text
     */
    private fun getUnicodeCandidates(input: String): List<UnicodeCandidate> {
        val inputCharArray = input.toCharArray()
        val candidates = ArrayList<UnicodeCandidate>()
        var i = 0
        while (i < input.length) {
            val emojiEnd = getEmojiEndPos(inputCharArray, i)
            if (emojiEnd != -1) {
                val emoji = EmojiManager.getByUnicode(input.substring(i, emojiEnd))
                val fitzpatrickString = if (emojiEnd + 2 <= input.length) {
                    String(inputCharArray, emojiEnd, 2)
                } else {
                    null
                }
                if (emoji != null && fitzpatrickString != null) {
                    val candidate = UnicodeCandidate(emoji, fitzpatrickString, i)
                    candidates.add(candidate)
                    i = candidate.fitzpatrickEndIndex - 1
                }
            }
            i++
        }

        return candidates
    }


    /**
     * Returns end index of a unicode emoji if it is found in text starting at
     * index startPos, -1 if not found.
     * This returns the longest matching emoji, for example, in
     * "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66"
     * it will find alias:family_man_woman_boy, NOT alias:man
     *
     * @param text the current text where we are looking for an emoji
     * @param startPos the position in the text where we should start looking for
     * an emoji end
     *
     * @return the end index of the unicode emoji starting at startPos. -1 if not
     * found
     */
    private fun getEmojiEndPos(text: CharArray, startPos: Int): Int {
        var best = -1
        for (j in startPos + 1..text.size) {
            val status = EmojiManager.isEmoji(Arrays.copyOfRange(
                    text,
                    startPos,
                    j
            ))

            if (status!!.exactMatch()) {
                best = j
            } else if (status.impossibleMatch()) {
                return best
            }
        }

        return best
    }


    class UnicodeCandidate constructor(val emoji: Emoji, fitzpatrick: String, val emojiStartIndex: Int) {
        private val fitzpatrick: Fitzpatrick? = Fitzpatrick.fitzpatrickFromUnicode(fitzpatrick)

        val fitzpatrickType: String
            get() = if (hasFitzpatrick()) fitzpatrick!!.name.toLowerCase() else ""

        val fitzpatrickUnicode: String
            get() = if (hasFitzpatrick()) fitzpatrick!!.unicode else ""

        val emojiEndIndex: Int
            get() = emojiStartIndex + (emoji.unicode?.length ?: 0)

        val fitzpatrickEndIndex: Int
            get() = emojiEndIndex + if (fitzpatrick != null) 2 else 0

        fun hasFitzpatrick(): Boolean {
            return fitzpatrick != null
        }
    }


    internal class AliasCandidate constructor(
            val fullString: String,
            val alias: String,
            fitzpatrickString: String?
    ) {
        val fitzpatrick: Fitzpatrick?

        init {
            if (fitzpatrickString == null) {
                this.fitzpatrick = null
            } else {
                this.fitzpatrick = Fitzpatrick.fitzpatrickFromType(fitzpatrickString)
            }
        }
    }

    /**
     * Enum used to indicate what should be done when a Fitzpatrick modifier is
     * found.
     */
    enum class FitzpatrickAction {
        /**
         * Tries to match the Fitzpatrick modifier with the previous emoji
         */
        PARSE,

        /**
         * Removes the Fitzpatrick modifier from the string
         */
        REMOVE,

        /**
         * Ignores the Fitzpatrick modifier (it will stay in the string)
         */
        IGNORE
    }

    interface EmojiTransformer {
        fun transform(unicodeCandidate: UnicodeCandidate): String
    }
}
