package com.fastaccess.github.platform.markwon.emoji

import org.commonmark.node.CustomNode
import org.commonmark.node.Delimited

/**
 * Created by kosh on 20/08/2017.
 */

class Emoji : CustomNode(), Delimited {
    var emoji: String? = null

    override fun getOpeningDelimiter(): String = DELIMITER

    override fun getClosingDelimiter(): String = DELIMITER

    companion object {
        private const val DELIMITER = ":"
    }
}
