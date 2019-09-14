package com.fastaccess.fasthub.commit.list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.fastaccess.data.model.FragmentType
import com.fastaccess.data.model.ViewPagerModel
import com.fastaccess.fasthub.commit.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BasePagerFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.base.PagerAdapter
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.base.utils.EXTRA_THREE
import com.fastaccess.github.base.utils.EXTRA_TWO
import kotlinx.android.synthetic.main.toolbar_fragment_pager_layout.*

class CommitPagerFragment : BasePagerFragment() {

    private val login by lazy { arguments?.getString("login") }
    private val repo by lazy { arguments?.getString("repo") }
    private val number by lazy { arguments?.getInt("number", 0) ?: 0 }


    override fun viewModel(): BaseViewModel? = null

    override fun layoutRes(): Int = R.layout.toolbar_fragment_pager_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        setupToolbar(R.string.commits)
        pager.adapter = PagerAdapter(
            childFragmentManager, arrayListOf(
                ViewPagerModel(getString(R.string.commits), CommitListFragment.newInstance(login, repo, number), FragmentType.COMMITS),
                ViewPagerModel(getString(R.string.files), CommitListFragment.newInstance(login, repo, number), FragmentType.FILES) // TODO
            )
        )
        tabs.setupWithViewPager(pager)
    }

    override fun onPageSelected(page: Int) = (pager.adapter?.instantiateItem(pager, page) as? BaseFragment)?.onScrollToTop() ?: Unit

    companion object {
        fun newInstance(
            login: String,
            repo: String,
            number: Int
        ) = CommitPagerFragment().apply {
            arguments = bundleOf(
                EXTRA to login,
                EXTRA_TWO to repo,
                EXTRA_THREE to number
            )
        }
    }
}