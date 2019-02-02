package com.fastaccess.markdown.spans

import android.text.SpannableStringBuilder
import com.fastaccess.markdown.emoji.EmojiManager
import net.nightwhistler.htmlspanner.SpanStack
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode

/**
 * Created by Kosh on 27 May 2017, 4:54 PM
 */

class EmojiHandler : TagNodeHandler() {
    override fun handleTagNode(node: TagNode?, builder: SpannableStringBuilder?, start: Int, end: Int, spanStack: SpanStack?) {
        val emoji = node?.getAttributeByName("alias")
        if (emoji != null) {
            val unicode = EmojiManager.getForAlias(emoji)
            if (unicode?.unicode != null) {
                builder?.replace(start, end, " " + unicode.unicode + " ")
            }
        } else if (node?.text != null) {
            val unicode = EmojiManager.getForAlias(node.text.toString())
            if (unicode?.unicode != null) {
                builder?.replace(start, end, " " + unicode.unicode + " ")
            }
        }
    }
}
