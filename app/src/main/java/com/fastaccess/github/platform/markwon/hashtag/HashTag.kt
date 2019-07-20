package com.fastaccess.github.platform.markwon.hashtag

import org.commonmark.node.CustomNode
import org.commonmark.node.Delimited


/**
 * Created by kosh on 20/08/2017.
 */

class HashTag : CustomNode(), Delimited {

    var url: String? = null
        get() = DELIMITER + field!!

    override fun getOpeningDelimiter(): String {
        return DELIMITER
    }

    override fun getClosingDelimiter(): String {
        return " "
    }

    companion object {
        private const val DELIMITER = "#"
    }
}
