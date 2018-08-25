package com.fastaccess.github.ui.modules.profile.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.domain.FastHubObserver
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.platform.glide.GlideApp
import com.fastaccess.github.utils.BundleConstant
import com.fastaccess.github.utils.extensions.*
import github.GetProfileQuery
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.profile_main_fragment_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 18.08.18.
 */
class ProfileFragment : BaseFragment() {

    @Inject lateinit var apollo: ApolloClient
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
            callApi()
        }
        swipeRefresh.isRefreshing = true
        userImageView.showHideFabAnimation(false)
        callApi()
    }

    private fun callApi() {
        Rx2Apollo.from(apollo.query(GetProfileQuery(loginBundle)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter { !it.hasErrors() }
                .map { it.data()?.user }
                .doOnNext {
                    userImageView.showHideFabAnimation(true)
                    swipeRefresh.isRefreshing = false
                    val user = it ?: return@doOnNext
                    followBtn.isVisible = user.isViewerCanFollow == true
                    blockBtn.isCursorVisible = user.isViewer == true
                    description.isVisible = user.bio?.isNotEmpty() == true
                    description.text = user.bio ?: ""
                    email.isVisible = user.email.isNotEmpty()
                    email.text = user.email
                    company.isVisible = user.company?.isNotEmpty() == true
                    company.text = user.company ?: ""
                    joined.text = user.createdAt.timeAgo()
                    joined.isVisible = true
                    location.isVisible = user.location?.isNotEmpty() == true
                    location.text = user.location ?: ""
                    name.isVisible = user.name?.isNotEmpty() == true
                    name.text = user.name ?: ""
                    organizationHolder.isVisible = user.organizations.nodes?.isNotEmpty() == true
                    GlideApp.with(this)
                            .load(user.avatarUrl.toString())
                            .circleCrop()
                            .into(userImageView)
                }
                .subscribe(FastHubObserver())
    }


    companion object {
        fun newInstance(login: String) = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString(BundleConstant.EXTRA, login)
            }
        }
    }
}