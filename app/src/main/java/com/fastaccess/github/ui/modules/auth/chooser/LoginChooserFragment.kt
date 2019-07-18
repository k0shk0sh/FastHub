package com.fastaccess.github.ui.modules.auth.chooser

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.ui.adapter.LoggedInUsersAdapter
import com.fastaccess.github.ui.modules.auth.LoginChooserViewModel
import com.fastaccess.github.ui.modules.auth.callback.LoginChooserCallback
import com.fastaccess.github.utils.extensions.beginDelayedTransition
import kotlinx.android.synthetic.main.login_chooser_fragment_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 19.05.18.
 */
class LoginChooserFragment : BaseFragment() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var schedulerProvider: SchedulerProvider

    private val viewModel by lazy { ViewModelProviders.of(requireActivity(), viewModelFactory).get(LoginChooserViewModel::class.java) }
    private var callback: LoginChooserCallback? = null
    private val adapter by lazy {
        LoggedInUsersAdapter { user ->
            addDisposal(
                viewModel.reLogin(user)
                    .subscribeOn(schedulerProvider.ioThread())
                    .observeOn(schedulerProvider.uiThread())
                    .subscribe({ (requireActivity() as? LoginChooserCallback)?.onUserLoggedIn(user) },
                        { throwable -> view?.let { showSnackBar(it, message = throwable.message) } })
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as LoginChooserCallback
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun viewModel(): BaseViewModel? = null
    override fun layoutRes() = R.layout.login_chooser_fragment_layout
    override fun onFragmentCreatedWithUser(
        view: View,
        savedInstanceState: Bundle?
    ) {
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        basicAuth.setOnClickListener { callback?.navToBasicAuth(loginCard) }
        accessToken.setOnClickListener { callback?.navToAccessToken(loginCard) }
        enterprise.setOnClickListener { callback?.navToEnterprise(loginCard) }
        browserLogin.setOnClickListener { callback?.loginWithBrowser() }

        viewModel.progress.observeNotNull(this) { progress ->
            this@LoginChooserFragment.view?.beginDelayedTransition()
            buttonsLayout.isVisible = !progress
            progressBar.isVisible = progress
        }

        toggle.setOnClickListener {
            parentLayout.beginDelayedTransition()
            val isVisible = recycler.isVisible
            recycler.isVisible = !isVisible
            toggleImage.rotation = if (!isVisible) 180f else 0f
        }

        viewModel.loggedInUsers.observeNotNull(this) {
            if (it.isEmpty()) return@observeNotNull
            multiAccLayout?.isVisible = true
            adapter.submitList(it)
            recycler.adapter = adapter
        }
    }

    companion object {
        const val TAG = "LoginChooserFragment"
    }
}