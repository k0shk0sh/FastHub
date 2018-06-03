package com.fastaccess.github.ui.modules.auth.login

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Slide
import com.fastaccess.data.persistence.models.ValidationError.FieldType.*
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.ui.modules.auth.callback.LoginChooserCallback
import com.fastaccess.github.utils.BundleConstant
import com.fastaccess.github.utils.extensions.asString
import com.fastaccess.github.utils.extensions.isTrue
import kotlinx.android.synthetic.main.login_form_layout.*
import javax.inject.Inject


/**
 * Created by Kosh on 18.05.18.
 */
class BaseAuthLoginFragment : BaseFragment() {


    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: LoginViewModel
    private var callback: LoginChooserCallback? = null
    private val isAccessToken by lazy { arguments?.getBoolean(BundleConstant.EXTRA) ?: false }
    private val isEnterprise by lazy { arguments?.getBoolean(BundleConstant.EXTRA_TWO) ?: false }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = context as LoginChooserCallback
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun layoutRes(): Int = R.layout.login_form_layout
    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        isAccessToken.isTrue {
            password.hint = getString(R.string.access_token)
        }
        isEnterprise.isTrue {
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
            viewModel.login(usernameEditText.asString(),
                    passwordEditText.asString(),
                    twoFactorEditText.asString(),
                    endpointEditText.asString(),
                    accessTokenCheckbox.isChecked)
        }
        observeData()
    }

    private fun observeData() {
        viewModel.validationLiveData.observe(this, Observer {
            it?.let {
                val requiredField = getString(R.string.required_field)
                when (it.fieldType) {
                    USERNAME -> username.error = if (it.isValid) null else requiredField
                    PASSWORD -> password.error = if (it.isValid) null else requiredField
                    TWO_FACTOR -> twoFactor.error = if (it.isValid) null else requiredField
                    URL -> endpoint.error = if (it.isValid) null else requiredField
                }
            }
        })
    }

    companion object {
        const val TAG = "BaseAuthLoginFragment"
        fun newInstance(accessToken: Boolean = false, isEnterprise: Boolean = false): BaseAuthLoginFragment = BaseAuthLoginFragment()
                .apply {
                    val enter = Slide(Gravity.END)
                    val exit = Slide(Gravity.START)
                    enterTransition = enter
                    exitTransition = exit
                    arguments = Bundle().apply {
                        putBoolean(BundleConstant.EXTRA, accessToken)
                        putBoolean(BundleConstant.EXTRA_TWO, isEnterprise)
                    }
                }
    }
}