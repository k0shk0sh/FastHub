package com.fastaccess.ui.adapter.viewholder

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import github.GetPinnedReposQuery
import java.text.NumberFormat

/**
 * Created by kosh on 09/08/2017.
 */
class ProfilePinnedReposViewHolder private constructor(view: View, adapter: BaseRecyclerAdapter<*, *, *>) :
        BaseViewHolder<GetPinnedReposQuery.Node>(view, adapter) {

    @BindView(R.id.title) lateinit var title: FontTextView
    @BindView(R.id.issues) lateinit var issues: FontTextView
    @BindView(R.id.pullRequests) lateinit var pullRequest: FontTextView
    @BindView(R.id.language) lateinit var language: FontTextView
    @BindView(R.id.stars) lateinit var stars: FontTextView
    @BindView(R.id.forks) lateinit var forks: FontTextView

    override fun bind(t: GetPinnedReposQuery.Node) {}

    fun bind(t: GetPinnedReposQuery.Node, numberFormat: NumberFormat) {
        title.text = t.name()
        issues.text = numberFormat.format(t.issues().totalCount())
        pullRequest.text = numberFormat.format(t.pullRequests().totalCount())
        forks.text = numberFormat.format(t.forks().totalCount())
        stars.text = numberFormat.format(t.stargazers().totalCount())
        t.primaryLanguage()?.let {
            language.text = it.name()
            it.color()?.let {
                if (it.startsWith("#")) {
                    language.tintDrawables(Color.parseColor(it))
                } else {
                    val color = "#$it"
                    language.tintDrawables(Color.parseColor(color))
                }
            }
        }
    }

    companion object {
        fun newInstance(parent: ViewGroup, adapter: BaseRecyclerAdapter<*, *, *>): ProfilePinnedReposViewHolder {
            return ProfilePinnedReposViewHolder(getView(parent, R.layout.profile_pinned_repo_row_item), adapter)
        }
    }
}