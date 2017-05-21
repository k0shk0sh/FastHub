package com.fastaccess.helper

import android.content.Intent
import com.fastaccess.data.dao.model.Login
import com.fastaccess.ui.modules.login.LoginActivity

/**
 * Created by JediB on 5/21/2017.
 */

public class SmartLockHelper {

    companion object {

        fun credential(loginActivity: LoginActivity) {

        }

        // This will be called, and we need to make sure that it's passed through to `onSuccessfullyLoggedIn`.
        fun handleSuccessfulLogin(context: LoginActivity, userModel: Login, pass: String) {
            context.onSuccessfullyLoggedIn()
        }

        // This will never be called, but for sake of actually building the function is here.
        fun handleSuccess(data: Intent, loginActivity: LoginActivity) {
        }

    }

}