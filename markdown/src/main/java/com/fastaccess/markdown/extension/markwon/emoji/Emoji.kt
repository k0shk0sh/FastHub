package com.fastaccess.markdown.extension.markwon.emoji

import org.commonmark.node.CustomNode
import org.commonmark.node.Delimited

/**
 * Created by kosh on 20/08/2017.
 */

class Emoji : CustomNode(), Delimited {
    var emoji: String? = null

    override fun getOpeningDelimiter(): String = DELIMITER

    override fun getClosingDelimiter(): String = DELIMITER

    override fun toString(): String = emoji ?: "no emoji"

    companion object {
        private const val DELIMITER = ":"
    }
}
