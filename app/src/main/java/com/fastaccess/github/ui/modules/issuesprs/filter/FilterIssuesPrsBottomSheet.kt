package com.fastaccess.github.ui.modules.issuesprs.filter

import android.os.Bundle
import android.view.View
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel

/**
 * Created by Kosh on 19.01.19.
 */
class FilterIssuesPrsBottomSheet : BaseFragment() {
    override fun layoutRes(): Int = R.layout.filter_issue_pr_layout
    override fun viewModel(): BaseViewModel? = null
    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        setupToolbar(R.string.filter)
        //TODO
    }

    companion object {
        fun newInstance() = FilterIssuesPrsBottomSheet()
    }
}