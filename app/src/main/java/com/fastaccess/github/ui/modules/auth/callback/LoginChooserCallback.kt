package com.fastaccess.github.ui.modules.auth.callback

import android.view.View
import com.fastaccess.data.persistence.models.LoginModel

/**
 * Created by Kosh on 19.05.18.
 */

interface LoginChooserCallback {
    fun navToBasicAuth(view: View)
    fun navToAccessToken(view: View)
    fun navToEnterprise(view: View)
    fun popStack()
    fun onUserLoggedIn(login: LoginModel)
    fun loginWithBrowser()
}