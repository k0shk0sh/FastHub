package com.fastaccess.fasthub.commit.list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.fastaccess.fasthub.commit.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.base.utils.EXTRA_THREE
import com.fastaccess.github.base.utils.EXTRA_TWO

class CommitListFragment : BaseFragment() {

    private val login by lazy { arguments?.getString(EXTRA) }
    private val repo by lazy { arguments?.getString(EXTRA_TWO) }
    private val number by lazy { arguments?.getInt(EXTRA_THREE, 0) ?: 0 }
    private val isPr by lazy { number > 0 }

    override fun layoutRes(): Int = if (isPr) {
        R.layout.simple_refresh_list_layout
    } else {
        R.layout.toolbar_fragment_list_layout
    }

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        toolbar?.let {
            setupToolbar(
                "$login/$repo/${if (number > 0) {
                    "$number/${getString(R.string.commits)}"
                } else {
                    getString(R.string.commits)
                }}"
            )
        }
    }

    override fun viewModel(): BaseViewModel? = null

    companion object {
        fun newInstance(
            login: String?,
            repo: String?,
            number: Int? = null
        ) = CommitListFragment().apply {
            arguments = bundleOf(
                EXTRA to login,
                EXTRA_TWO to repo,
                EXTRA_THREE to number
            )
        }
    }
}