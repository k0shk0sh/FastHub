package com.fastaccess.github.ui.modules.auth.login

import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.fastaccess.data.persistence.db.FastHubDatabase
import com.fastaccess.data.persistence.models.FastHubErrors
import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.data.persistence.models.ValidationError
import com.fastaccess.extension.uiThread
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.di.modules.AuthenticationInterceptor
import com.fastaccess.github.usecase.auth.GetAccessTokenUseCase
import com.fastaccess.github.usecase.auth.LoginUseCase
import com.fastaccess.github.usecase.auth.LoginWithAccessTokenUseCase
import io.reactivex.Completable
import okhttp3.Credentials
import javax.inject.Inject

/**
 * Created by Kosh on 21.05.18.
 */
class LoginViewModel @Inject constructor(
    private val loginUserCase: LoginUseCase,
    private val accessTokenUseCase: GetAccessTokenUseCase,
    private val loginWithAccessTokenUseCase: LoginWithAccessTokenUseCase,
    private val interceptor: AuthenticationInterceptor,
    private val fasthubDatabase: FastHubDatabase
) : BaseViewModel() {

    val validationLiveData = MutableLiveData<ValidationError>()
    val loggedInUser = MutableLiveData<LoginModel>()

    fun login(userName: String? = null,
              password: String? = null,
              twoFactorCode: String? = null,
              endPoint: String? = null,
              isAccessToken: Boolean = true) {
        validationLiveData.value = ValidationError(ValidationError.FieldType.TWO_FACTOR, !twoFactorCode.isNullOrBlank())
        validationLiveData.value = ValidationError(ValidationError.FieldType.URL, !endPoint.isNullOrBlank())
        validationLiveData.value = ValidationError(ValidationError.FieldType.PASSWORD, !password.isNullOrBlank())
        validationLiveData.value = ValidationError(ValidationError.FieldType.USERNAME, !userName.isNullOrBlank())
        if (userName == null || password == null) return // just to disable lint
        if (!userName.isNullOrEmpty() && !password.isNullOrEmpty()) {
            try {
                val authToken = Credentials.basic(userName, password)
                interceptor.otp = twoFactorCode
                interceptor.token = authToken
                if (!isAccessToken) {
                    loginBasic(twoFactorCode)
                } else {
                    loginWithAccessToken(password)
                }
            } catch (e: Exception) {
                error.postValue(FastHubErrors(FastHubErrors.ErrorType.OTHER, resId = R.string.failed_login))
                e.printStackTrace()
                Crashlytics.logException(e)
            }
        }
    }

    private fun loginWithAccessToken(password: String,
                                     twoFactorCode: String? = null,
                                     isEnterprise: Boolean? = false,
                                     enterpriseUrl: String? = null) {
        interceptor.token = password
        loginWithAccessTokenUseCase.executeSafely(callApi(loginWithAccessTokenUseCase.buildObservable()
            .flatMap { user ->
                user.isLoggedIn = true
                user.otpCode = twoFactorCode
                user.token = password
                user.isEnterprise = isEnterprise
                user.enterpriseUrl = enterpriseUrl
                return@flatMap loginWithAccessTokenUseCase.insertUser(user)
            }
            .doOnNext { loggedInUser.postValue(it) }
        ))
    }

    private fun loginBasic(twoFactorCode: String? = null,
                           isEnterprise: Boolean? = false,
                           enterpriseUrl: String? = null) {
        loginUserCase.setAuthBody(twoFactorCode)
        loginUserCase.executeSafely(callApi(loginUserCase.buildObservable()
            .flatMap({
                interceptor.token = it.token ?: it.accessToken
                return@flatMap loginWithAccessTokenUseCase.buildObservable()
            }, { accessToken, user ->
                user.isLoggedIn = true
                user.otpCode = twoFactorCode
                user.token = accessToken.token ?: accessToken.accessToken
                user.isEnterprise = isEnterprise
                user.enterpriseUrl = enterpriseUrl
                return@flatMap user
            })
            .flatMap { loginWithAccessTokenUseCase.insertUser(it) })
            .doOnNext { loggedInUser.postValue(it) })
    }

    override fun onCleared() {
        loginUserCase.dispose()
        accessTokenUseCase.dispose()
        loginWithAccessTokenUseCase.dispose()
        super.onCleared()
    }

    fun clearDb() = Completable.fromCallable { fasthubDatabase.clearAll() }.uiThread()
}