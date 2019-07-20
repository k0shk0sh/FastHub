package com.fastaccess.github.platform.markwon.mention

import com.fastaccess.github.utils.GITHUB_LINK
import org.commonmark.node.CustomNode
import org.commonmark.node.Delimited


/**
 * Created by kosh on 20/08/2017.
 */

class Mention : CustomNode(), Delimited {

    var url: String? = null
        get() = BASE_URL + field!!

    override fun getOpeningDelimiter(): String = DELIMITER
    override fun getClosingDelimiter(): String = " "

    companion object {
        private const val BASE_URL = GITHUB_LINK
        private const val DELIMITER = "@"
    }
}
