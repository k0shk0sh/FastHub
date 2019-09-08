package com.fastaccess.github.di.modules

import android.annotation.SuppressLint
import android.content.Context
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.github.R
import com.fastaccess.github.base.engine.ThemeEngine
import com.fastaccess.github.di.scopes.PerFragment
import com.fastaccess.github.platform.mentions.MentionsPresenter
import com.fastaccess.github.ui.modules.comment.CommentFragment
import com.fastaccess.github.ui.modules.editor.EditorFragment
import com.fastaccess.github.ui.modules.issue.fragment.IssueFragment
import com.fastaccess.github.ui.modules.issuesprs.edit.EditIssuePrFragment
import com.fastaccess.github.ui.modules.pr.fragment.PullRequestFragment
import com.fastaccess.github.ui.modules.pr.reviews.ListReviewsFragment
import com.fastaccess.github.usecase.search.FilterSearchUsersUseCase
import com.fastaccess.github.utils.extensions.theme
import com.fastaccess.markdown.GrammarLocatorDef
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

/**
 * Created by Kosh on 02.02.19.
 */
@Module
class FragmentModule {

    @SuppressLint("PrivateResource")
    @PerFragment @Provides fun provideMarkwon(
        context: Context,
        preference: FastHubSharedPreference
    ): Markwon = Markwon.builder(context)
        .usePlugin(JLatexMathPlugin.create(context.resources.getDimension(R.dimen.abc_text_size_subhead_material)))
        .usePlugin(TaskListPlugin.create(context))
        .usePlugin(HtmlPlugin.create())
        .usePlugin(GlideImagesPlugin.create(context))
        .usePlugin(TablePlugin.create(context))
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(
            SyntaxHighlightPlugin.create(
                Prism4j(GrammarLocatorDef()), if (ThemeEngine.isLightTheme(preference.theme)) {
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

    @PerFragment @Provides fun provideMentionsPresenter(
        context: Context,
        searchUsersUseCase: FilterSearchUsersUseCase
    ) = MentionsPresenter(context, searchUsersUseCase)
}

@Module(includes = [FragmentModule::class])
class IssueModule {
    @PerFragment @Provides fun provideIssueContext(fragment: IssueFragment) = fragment.requireContext()
}

@Module(includes = [FragmentModule::class])
class EditorModule {
    @PerFragment @Provides fun provideEditorContext(fragment: EditorFragment) = fragment.requireContext()
}

@Module(includes = [FragmentModule::class])
class EditIssuePrModule {
    @PerFragment @Provides fun provideEditorContext(fragment: EditIssuePrFragment) = fragment.requireContext()
}

@Module(includes = [FragmentModule::class])
class CommentModule {
    @PerFragment @Provides fun provideEditorContext(fragment: CommentFragment) = fragment.requireContext()
}

@Module(includes = [FragmentModule::class])
class PullRequestModule {
    @PerFragment @Provides fun provideEditorContext(fragment: PullRequestFragment) = fragment.requireContext()
}

@Module(includes = [FragmentModule::class])
class ListReviewsModule {
    @PerFragment @Provides fun provideEditorContext(fragment: ListReviewsFragment) = fragment.requireContext()
}