package com.fastaccess.github.di.modules

import android.annotation.SuppressLint
import android.content.Context
import android.text.util.Linkify
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.github.R
import com.fastaccess.github.base.engine.ThemeEngine
import com.fastaccess.github.di.annotations.ForApplication
import com.fastaccess.github.di.scopes.PerFragment
import com.fastaccess.github.platform.mentions.MentionsPresenter
import com.fastaccess.github.ui.modules.issue.fragment.IssueFragment
import com.fastaccess.github.usecase.search.FilterSearchUsersUseCase
import com.fastaccess.github.utils.extensions.theme
import com.fastaccess.markdown.GrammarLocatorDef
import dagger.Module
import dagger.Provides
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.recycler.MarkwonAdapter
import io.noties.markwon.recycler.SimpleEntry
import io.noties.markwon.recycler.table.TableEntry
import io.noties.markwon.recycler.table.TableEntryPlugin
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.Prism4jThemeDefault
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.node.FencedCodeBlock

/**
 * Created by Kosh on 02.02.19.
 */
@Module
class FragmentModule {

    @PerFragment @Provides fun provideContext(fragment: IssueFragment) = fragment.requireContext()

    @SuppressLint("PrivateResource")
    @PerFragment @Provides fun provideMarkwon(
        @ForApplication context: Context,
        preference: FastHubSharedPreference
    ): Markwon = Markwon.builder(context)
        .usePlugin(JLatexMathPlugin.create(context.resources.getDimension(R.dimen.abc_text_size_subhead_material)))
        .usePlugin(TaskListPlugin.create(context))
        .usePlugin(HtmlPlugin.create())
        .usePlugin(GlideImagesPlugin.create(context))
        .usePlugin(TableEntryPlugin.create(context))
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(LinkifyPlugin.create(Linkify.EMAIL_ADDRESSES or Linkify.WEB_URLS))
        .usePlugin(
            SyntaxHighlightPlugin.create(
                Prism4j(GrammarLocatorDef()), if (ThemeEngine.isLightTheme(preference.theme)) {
                    Prism4jThemeDefault.create()
                } else {
                    Prism4jThemeDarkula.create()
                }
            )
        )
        .build()

    @PerFragment @Provides fun provideMarkwonAdapterBuilder(): MarkwonAdapter.Builder =
        MarkwonAdapter.builder(R.layout.markdown_textview_row_item, R.id.text)
            .include(FencedCodeBlock::class.java, SimpleEntry.create(R.layout.markwon_fenced_cod_block_row_item, R.id.text))
            .include(TableBlock::class.java, TableEntry.create {
                it.tableLayout(R.layout.markwon_table_row_item, R.id.table_layout)
                    .textLayoutIsRoot(R.layout.markwon_table_entry_cell)
            })

    @PerFragment @Provides fun provideMentionsPresenter(
        context: Context,
        searchUsersUseCase: FilterSearchUsersUseCase
    ) = MentionsPresenter(context, searchUsersUseCase)
}