package com.fastaccess.fasthub.commit.view

import android.os.Bundle
import android.view.View
import com.fastaccess.fasthub.commit.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel

class CommitFragment : BaseFragment() {

    override fun layoutRes(): Int = R.layout.single_commit_pager_layout
    override fun viewModel(): BaseViewModel? = null

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {

    }
}