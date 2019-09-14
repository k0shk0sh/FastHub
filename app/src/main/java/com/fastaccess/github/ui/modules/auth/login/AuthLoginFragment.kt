package com.fastaccess.github.ui.modules.auth.login

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Slide
import com.fastaccess.data.model.FastHubErrors
import com.fastaccess.data.model.ValidationError.FieldType.*
import com.fastaccess.github.R
import com.fastaccess.github.base.extensions.asString
import com.fastaccess.github.base.extensions.beginDelayedTransition
import com.fastaccess.github.base.extensions.hideKeyboard
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.base.utils.EXTRA_TWO
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.platform.viewmodel.ViewModelProviders
import com.fastaccess.github.ui.modules.auth.callback.LoginChooserCallback
import kotlinx.android.synthetic.main.login_form_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 18.05.18.
 */
class AuthLoginFragment : com.fastaccess.github.base.BaseFragment() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private var callback: LoginChooserCallback? = null

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java) }
    private val isAccessToken by lazy { arguments?.getBoolean(EXTRA) ?: false }
    private val isEnterpriseBundle by lazy { arguments?.getBoolean(EXTRA_TWO) ?: false }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as LoginChooserCallback
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun viewModel(): com.fastaccess.github.base.BaseViewModel? = null
    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) = Unit
    override fun layoutRes(): Int = R.layout.login_form_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isAccessToken.isTrue {
            password.hint = getString(R.string.access_token)
        }
        isEnterpriseBundle.isTrue {
            endpoint.isVisible = true
            accessTokenCheckbox.isVisible = true
            accessTokenCheckbox.setOnCheckedChangeListener { _, checked ->
                password.hint = if (checked) {
                    getString(R.string.access_token)
                } else {
                    getString(R.string.password)
                }
            }
        }

        bottomBar.setNavigationOnClickListener { callback?.popStack() }
        loginBtn.setOnClickListener {
            it.isEnabled = false
            if (progressBar.isVisible) return@setOnClickListener
            viewModel.login(usernameEditText.asString(),
                passwordEditText.asString(),
                twoFactorEditText.asString(),
                endpointEditText.asString(),
                (isAccessToken || accessTokenCheckbox.isVisible && accessTokenCheckbox.isChecked))
        }

        passwordEditText.setOnEditorActionListener { v, _, _ ->
            when {
                twoFactor.isVisible -> twoFactor.requestFocus()
                endpoint.isVisible -> endpoint.requestFocus()
                else -> {
                    v.hideKeyboard()
                    loginBtn.callOnClick()
                }
            }
            return@setOnEditorActionListener true
        }
        observeData()
    }

    private fun observeData() {
        viewModel.validationLiveData.observe(this, Observer { validationError ->
            validationError?.let {
                val requiredField = getString(R.string.required_field)
                when (it.fieldType) {
                    USERNAME -> username.error = if (it.isValid) null else requiredField
                    PASSWORD -> password.error = if (it.isValid) null else requiredField
                    TWO_FACTOR -> twoFactor.error = if (it.isValid) null else requiredField
                    URL -> endpoint.error = if (it.isValid) null else requiredField
                }
            }
        })

        viewModel.progress.observe(this, Observer { isLoading ->
            loginBtn.isEnabled = isLoading == false
            view?.beginDelayedTransition()
            loginForm.isVisible = isLoading == false
            progressBar.isVisible = isLoading == true
        })

        viewModel.error.observe(this, Observer { errors ->
            errors?.let {
                if (it.errorType == FastHubErrors.ErrorType.TWO_FACTOR) {
                    twoFactor.isVisible = true
                }
                view?.let { view ->
                    showSnackBar(view, resId = it.resId, message = it.message)
                }
            }
        })

        viewModel.loggedInUser.observe(this, Observer { model ->
            if (model != null) {
                addDisposal(viewModel.clearDb()
                    .subscribe({ callback?.onUserLoggedIn(model) }, { throwable -> view?.let { showSnackBar(it, message = throwable.message) } }))
            } else {
                view?.let { showSnackBar(it, resId = R.string.failed_login) }
            }
        })
    }

    companion object {
        const val TAG = "AuthLoginFragment"
        fun newInstance(accessToken: Boolean = false, isEnterprise: Boolean = false): AuthLoginFragment = AuthLoginFragment()
            .apply {
                val enter = Slide(Gravity.END)
                val exit = Slide(Gravity.START)
                enterTransition = enter
                exitTransition = exit
                arguments = Bundle().apply {
                    putBoolean(EXTRA, accessToken)
                    putBoolean(EXTRA_TWO, isEnterprise)
                }
            }
    }
}