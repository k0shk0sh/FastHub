package com.fastaccess.github.ui.modules.profile.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.persistence.models.UserModel
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.platform.glide.GlideApp
import com.fastaccess.github.ui.adapter.ProfileOrganizationCell
import com.fastaccess.github.ui.modules.profile.fragment.viewmodel.ProfileViewModel
import com.fastaccess.github.utils.BundleConstant
import com.fastaccess.github.utils.extensions.*
import kotlinx.android.synthetic.main.profile_main_fragment_layout.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 18.08.18.
 */
class ProfileFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java) }

    private val loginBundle: String by lazy { arguments?.getString(BundleConstant.EXTRA) ?: "" }

    override fun layoutRes(): Int = R.layout.profile_main_fragment_layout

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        username.text = loginBundle
        actionsHolder.isVisible = loginBundle != me()
        toolbar.navigationIcon?.setTint(requireContext().getColorAttr(android.R.attr.textColorPrimaryInverse))
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        activity?.let { activity ->
            activity.clearDarkStatusBarIcons()
            activity.setStatusBarColor()
        }
        swipeRefresh.setOnRefreshListener {
            userImageView.showHideFabAnimation(false)
            viewModel.getUserFromRemote(loginBundle)
        }
        viewModel.getUser(loginBundle).observeNotNull(this) {
            Timber.e("$it")
            initUI(it)
        }

        if (savedInstanceState == null) {
            swipeRefresh.isRefreshing = true
            userImageView.showHideFabAnimation(false)
            viewModel.getUserFromRemote(loginBundle)
        }

        viewModel.progress.observeNotNull(this) {
            swipeRefresh.isRefreshing = it == true
        }
        viewModel.error.observeNotNull(this) {
            showSnackBar(view, resId = it.resId, message = it.message)
        }
    }

    private fun initUI(user: UserModel) {
        userImageView.showHideFabAnimation(true)
        swipeRefresh.isRefreshing = false
        followBtn.isVisible = user.viewerCanFollow == true
        blockBtn.isCursorVisible = user.isViewer == true
        description.isVisible = user.bio?.isNotEmpty() == true
        description.text = user.bio ?: ""
        email.isVisible = user.email?.isNotEmpty() == true
        email.text = user.email
        company.isVisible = user.company?.isNotEmpty() == true
        company.text = user.company ?: ""
        joined.text = user.createdAt?.timeAgo() ?: ""
        joined.isVisible = true
        location.isVisible = user.location?.isNotEmpty() == true
        location.text = user.location ?: ""
        name.isVisible = user.name?.isNotEmpty() == true
        name.text = user.name ?: ""
        user.organizations?.nodes?.let { orgs ->
            if (orgs.isNotEmpty()) {
                organizationHolder.isVisible = true
                organizationList.removeAllCells()
                organizationList.addCells(orgs.map { ProfileOrganizationCell(it, GlideApp.with(this)) })
            }
        }
        GlideApp.with(this)
                .load(user.avatarUrl.toString())
                .circleCrop()
                .into(userImageView)
    }


    companion object {
        fun newInstance(login: String) = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString(BundleConstant.EXTRA, login)
            }
        }
    }
}