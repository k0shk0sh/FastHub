package com.fastaccess.markdown.extension.markwon.emoji

import android.text.SpannedString
import com.fastaccess.markdown.emoji.EmojiManager
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.Prop
import io.noties.markwon.RenderProps
import io.noties.markwon.SpanFactory
import timber.log.Timber

/**
 * Created by Kosh on 2019-07-20.
 */
class EmojiSpanFactory : SpanFactory {

    override fun getSpans(
        configuration: MarkwonConfiguration,
        props: RenderProps
    ): Any? {
        val emoji = props.get<Emoji>(Prop.of(":"))
        Timber.e("$props $emoji")
        if (emoji != null) {
            val unicode = EmojiManager.getForAlias(emoji.emoji)
            if (unicode?.unicode != null) {
                return SpannedString(unicode.unicode)
            }
        }
        return null
    }
}