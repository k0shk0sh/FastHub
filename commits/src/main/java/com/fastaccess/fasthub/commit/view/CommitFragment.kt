package com.fastaccess.fasthub.commit.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.data.model.FragmentType
import com.fastaccess.data.model.FullCommitModel
import com.fastaccess.data.model.ViewPagerModel
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.fasthub.commit.R
import com.fastaccess.fasthub.commit.list.CommitListFragment
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BasePagerFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.base.adapter.PagerAdapter
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.base.utils.EXTRA_THREE
import com.fastaccess.github.base.utils.EXTRA_TWO
import com.fastaccess.github.base.viewmodel.ViewModelProviders
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.extensions.timeAgo
import io.noties.markwon.Markwon
import io.noties.markwon.utils.NoCopySpannableFactory
import kotlinx.android.synthetic.main.commits_header_layout.*
import javax.inject.Inject

class CommitFragment : BasePagerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var markwon: Markwon
    @Inject lateinit var preference: FastHubSharedPreference

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(CommitViewModel::class.java) }

    private val sha by lazy { arguments?.getString(EXTRA) ?: throw NullPointerException("sha is null") }
    private val login by lazy { arguments?.getString(EXTRA_TWO) ?: throw NullPointerException("login is null") }
    private val repo by lazy { arguments?.getString(EXTRA_THREE) ?: throw NullPointerException("repo is null") }

    override fun layoutRes(): Int = R.layout.single_commit_pager_layout
    override fun viewModel(): BaseViewModel? = viewModel
    override fun onPageSelected(page: Int) = (pager.adapter?.instantiateItem(pager, page) as? BaseFragment)?.onScrollToTop() ?: Unit

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        setupToolbar(R.string.commit)
        observeChanges()
        if (viewModel.commitLiveData.value == null) {
            viewModel.loadCommit(login, repo, sha)
        }
    }

    private fun observeChanges() {
        viewModel.commitLiveData.observeNotNull(this) {
            initHeader(it)
            pager.adapter = PagerAdapter(
                childFragmentManager, arrayListOf(
                    ViewPagerModel(getString(R.string.commits), CommitListFragment.newInstance(login, repo, 0), FragmentType.FILES),
                    ViewPagerModel(getString(R.string.files), CommitListFragment.newInstance(login, repo, 0), FragmentType.COMMENTS) // TODO
                )
            )
            tabs.setupWithViewPager(pager)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initHeader(model: FullCommitModel) {
        author.text = model.author?.login
        verified.text = "${getString(R.string.committed)} ${model.authoredDate?.timeAgo()}"
        userIcon.loadAvatar(model.author?.avatarUrl, model.author?.url)
        files.text = "${model.changedFiles}"
        additions.text = "${model.additions}"
        deletion.text = "${model.deletions}"

        title.post {
            val bodyMd = model.messageHeadline
            //description.setMovementMethod(LinkMovementMethod.getInstance()) TODO
            title.setSpannableFactory(NoCopySpannableFactory.getInstance())
            markwon.setMarkdown(
                title, if (!bodyMd.isNullOrEmpty()) bodyMd else "**${getString(R.string.no_description_provided)}**"
            )
        }
        val bodyMd = model.messageBody
        if (!bodyMd.isNullOrEmpty()) {
            description.post {
                //description.setMovementMethod(LinkMovementMethod.getInstance()) TODO
                description.setSpannableFactory(NoCopySpannableFactory.getInstance())
                markwon.setMarkdown(description, bodyMd)
            }
        } else {
            description.isVisible = false
        }

    }

    companion object {
        fun newInstance(
            sha: String,
            login: String,
            repo: String
        ) = CommitFragment().apply {
            arguments = bundleOf(
                EXTRA to sha,
                EXTRA_TWO to login,
                EXTRA_THREE to repo
            )
        }
    }
}