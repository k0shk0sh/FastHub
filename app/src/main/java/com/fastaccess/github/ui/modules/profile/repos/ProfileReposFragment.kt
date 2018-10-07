package com.fastaccess.github.ui.modules.profile.repos

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.github.R
import com.fastaccess.github.base.BasePagerFragment
import com.fastaccess.github.ui.modules.profile.fragment.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.simple_refresh_list_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 06.10.18.
 */
class ProfileReposFragment : BasePagerFragment() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java) }

    override fun layoutRes(): Int = R.layout.simple_refresh_list_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {

    }

    override fun onPageSelected(page: Int) {
        recyclerView?.scrollToPosition(0)
    }
}