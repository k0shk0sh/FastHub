package com.fastaccess.markdown.di

import android.annotation.SuppressLint
import android.content.Context
import com.fastaccess.fasthub.dagger.scopes.PerFragment
import com.fastaccess.markdown.GrammarLocatorDef
import com.fastaccess.markdown.R
import dagger.Module
import dagger.Provides
import io.noties.markwon.Markwon
import io.noties.markwon.PrecomputedTextSetterCompat
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.recycler.MarkwonAdapter
import io.noties.markwon.recycler.SimpleEntry
import io.noties.markwon.recycler.table.TableEntry
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.Prism4jThemeDefault
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.node.FencedCodeBlock
import java.util.concurrent.Executors
import javax.inject.Named

@Module
class MarkdownModule {
    @SuppressLint("PrivateResource")
    @PerFragment @Provides fun provideMarkwon(
        context: Context,
        @Named("theme") theme: Int
    ): Markwon = Markwon.builder(context)
        .usePlugin(JLatexMathPlugin.create(context.resources.getDimension(R.dimen.abc_text_size_subhead_material)))
        .usePlugin(TaskListPlugin.create(context))
        .usePlugin(HtmlPlugin.create())
        .usePlugin(GlideImagesPlugin.create(context))
        .usePlugin(TablePlugin.create(context))
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(
            SyntaxHighlightPlugin.create(
                Prism4j(GrammarLocatorDef()), if (theme == R.style.ThemeLight) {
                    Prism4jThemeDefault.create()
                } else {
                    Prism4jThemeDarkula.create()
                }
            )
        )
        .textSetter(PrecomputedTextSetterCompat.create(Executors.newCachedThreadPool()))
        .build()

    @PerFragment @Provides fun provideMarkwonAdapterBuilder(): MarkwonAdapter.Builder =
        MarkwonAdapter.builder(R.layout.markdown_textview_row_item, R.id.text)
            .include(FencedCodeBlock::class.java, SimpleEntry.create(R.layout.markwon_fenced_cod_block_row_item, R.id.text))
            .include(TableBlock::class.java, TableEntry.create {
                it.tableLayout(R.layout.markwon_table_row_item, R.id.table_layout)
                    .textLayoutIsRoot(R.layout.markwon_table_entry_cell)
            })
}