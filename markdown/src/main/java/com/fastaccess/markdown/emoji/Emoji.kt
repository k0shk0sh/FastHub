package com.fastaccess.markdown.emoji

import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

/**
 * This class represents an emoji.<br></br>
 * <br></br>
 * This object is immutable so it can be used safely in a multithreaded context.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
class Emoji constructor(
        val description: String,
        private val supportsFitzpatrick: Boolean,
        aliases: List<String>,
        tags: List<String>,
        vararg bytes: Byte
) {
    /**
     * Returns the aliases of the emoji
     *
     * @return the aliases (unmodifiable)
     */
    val aliases: List<String>
    /**
     * Returns the tags of the emoji
     *
     * @return the tags (unmodifiable)
     */
    val tags: List<String>
    /**
     * Returns the unicode representation of the emoji
     *
     * @return the unicode representation
     */
    var unicode: String? = null
        private set
    /**
     * Returns the HTML decimal representation of the emoji
     *
     * @return the HTML decimal representation
     */
    var htmlDecimal: String? = null
        private set
    /**
     * Returns the HTML hexadecimal representation of the emoji
     *
     * @return the HTML hexadecimal representation
     */
    var htmlHexadecimal: String? = null
        private set

    /**
     * @return the HTML hexadecimal representation
     */
    val htmlHexidecimal: String?
        @Deprecated("identical to {@link #getHtmlHexadecimal()} for backwards-compatibility. Use that instead.")
        get() = this.htmlHexadecimal

    init {
        this.aliases = Collections.unmodifiableList(aliases)
        this.tags = Collections.unmodifiableList(tags)

        var count = 0
        try {
            this.unicode = String(bytes, Charset.forName("UTF-8"))
            val stringLength = unicode?.length ?: 0
            val pointCodes = arrayOfNulls<String>(stringLength)
            val pointCodesHex = arrayOfNulls<String>(stringLength)
            var offset = 0
            while (offset < stringLength) {
                val codePoint = unicode!!.codePointAt(offset)
                pointCodes[count] = String.format(Locale.getDefault(), "&#%d;", codePoint)
                pointCodesHex[count++] = String.format("&#x%x;", codePoint)
                offset += Character.charCount(codePoint)
            }
            this.htmlDecimal = stringJoin(pointCodes, count)
            this.htmlHexadecimal = stringJoin(pointCodesHex, count)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

    }

    /**
     * Method to replace String.join, since it was only introduced in java8
     *
     * @param array
     * the array to be concatenated
     * @return concatenated String
     */
    private fun stringJoin(array: Array<String?>, count: Int): String {
        var joined = ""
        for (i in 0 until count) joined += array[i]
        return joined
    }

    /**
     * Returns wether the emoji supports the Fitzpatrick modifiers or not
     *
     * @return true if the emoji supports the Fitzpatrick modifiers
     */
    fun supportsFitzpatrick(): Boolean {
        return this.supportsFitzpatrick
    }

    /**
     * Returns the unicode representation of the emoji associated with the
     * provided Fitzpatrick modifier.<br></br>
     * If the modifier is null, then the result is similar to
     * [Emoji.getUnicode]
     *
     * @param fitzpatrick
     * the fitzpatrick modifier or null
     * @return the unicode representation
     * @throws UnsupportedOperationException
     * if the emoji doesn't support the Fitzpatrick modifiers
     */
    fun getUnicode(fitzpatrick: Fitzpatrick?): String? {
        if (!this.supportsFitzpatrick()) {
            throw UnsupportedOperationException(
                    "Cannot get the unicode with a fitzpatrick modifier, " + "the emoji doesn't support fitzpatrick."
            )
        } else if (fitzpatrick == null) {
            return this.unicode
        }
        return this.unicode!! + fitzpatrick.unicode
    }

    override fun equals(other: Any?): Boolean {
        return !(other == null || other !is Emoji) && other.unicode == unicode
    }

    override fun hashCode(): Int {
        return unicode!!.hashCode()
    }

    /**
     * Returns the String representation of the Emoji object.<br></br>
     * <br></br>
     * Example:<br></br>
     * `Emoji {
     * description='smiling face with open mouth and smiling eyes',
     * supportsFitzpatrick=false,
     * aliases=[smile],
     * tags=[happy, joy, pleased],
     * unicode='😄',
     * htmlDec='&#128516;',
     * htmlHex='&#x1f604;'
     * }`
     *
     * @return the string representation
     */
    override fun toString(): String {
        return "Emoji{" +
                "description='" + description + '\''.toString() +
                ", supportsFitzpatrick=" + supportsFitzpatrick +
                ", aliases=" + aliases +
                ", tags=" + tags +
                ", unicode='" + unicode + '\''.toString() +
                ", htmlDec='" + htmlDecimal + '\''.toString() +
                ", htmlHex='" + htmlHexadecimal + '\''.toString() +
                '}'.toString()
    }
}
