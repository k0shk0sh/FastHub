package com.fastaccess.github.di.modules

import android.content.Context
import com.fastaccess.github.di.annotations.ForApplication
import com.fastaccess.github.di.scopes.PerFragment
import com.fastaccess.github.extensions.getDrawableCompat
import com.fastaccess.markdown.R
import com.fastaccess.markdown.spans.*
import dagger.Module
import dagger.Provides
import net.nightwhistler.htmlspanner.HtmlSpanner
import net.nightwhistler.htmlspanner.handlers.StyledTextHandler
import net.nightwhistler.htmlspanner.style.Style

/**
 * Created by Kosh on 02.02.19.
 */
@Module
class FragmentModule {

    @PerFragment @Provides fun provideHtmlSpanner(@ForApplication context: Context): HtmlSpanner {
        val mySpanner = HtmlSpanner()
        mySpanner.isStripExtraWhiteSpace = true
        val checked = context.getDrawableCompat(R.drawable.ic_checkbox_small)
        val unchecked = context.getDrawableCompat(R.drawable.ic_checkbox_empty_small)
        mySpanner.registerHandler("li", ListsHandler(checked, unchecked))
        mySpanner.registerHandler("g-emoji", EmojiHandler())
        mySpanner.registerHandler("b", StyledTextHandler(Style().setFontWeight(Style.FontWeight.BOLD)))
        mySpanner.registerHandler("strong", StyledTextHandler(Style().setFontWeight(Style.FontWeight.BOLD)))
        mySpanner.registerHandler("i", ItalicHandler())
        mySpanner.registerHandler("em", ItalicHandler())
        mySpanner.registerHandler("ul", MarginHandler())
        mySpanner.registerHandler("ol", MarginHandler())
        mySpanner.registerHandler("u", UnderlineHandler())
        mySpanner.registerHandler("strike", StrikethroughHandler())
        mySpanner.registerHandler("ins", UnderlineHandler())
        mySpanner.registerHandler("del", StrikethroughHandler())
        mySpanner.registerHandler("sub", SubScriptHandler())
        mySpanner.registerHandler("sup", SuperScriptHandler())
        mySpanner.registerHandler("a", LinkHandler())
        mySpanner.registerHandler("emoji", EmojiHandler())
        mySpanner.registerHandler("mention", LinkHandler())
        mySpanner.registerHandler("h1", HeaderHandler(1.5f))
        mySpanner.registerHandler("h2", HeaderHandler(1.4f))
        mySpanner.registerHandler("h3", HeaderHandler(1.3f))
        mySpanner.registerHandler("h4", HeaderHandler(1.2f))
        mySpanner.registerHandler("h5", HeaderHandler(1.1f))
        mySpanner.registerHandler("h6", HeaderHandler(1.0f))
        return mySpanner
    }
}