package com.fastaccess.github.ui.modules.auth.login

import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.fastaccess.data.persistence.models.ValidationError
import com.fastaccess.domain.response.AccessTokenResponse
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.di.modules.AuthenticationInterceptor
import com.fastaccess.github.usecase.auth.GetAccessTokenUseCase
import com.fastaccess.github.usecase.auth.LoginUseCase
import com.fastaccess.github.usecase.auth.LoginWithAccessTokenUseCase
import io.reactivex.Observable
import okhttp3.Credentials
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 21.05.18.
 */
class LoginViewModel @Inject constructor(private val loginUserCase: LoginUseCase,
                                         private val accessTokenUseCase: GetAccessTokenUseCase,
                                         private val loginWithAccessTokenUseCase: LoginWithAccessTokenUseCase,
                                         private val interceptor: AuthenticationInterceptor) : BaseViewModel() {

    val validationLiveData = MutableLiveData<ValidationError>()

    fun login(userName: String? = null,
              password: String? = null,
              twoFactorCode: String? = null,
              endPoint: String? = null,
              isBasicAuth: Boolean = true) {
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
                if (isBasicAuth) {
                    loginBasic(twoFactorCode)
                } else {
                    loginWithAccessToken(password)
                }
            } catch (e: Exception) {
                Timber.e(e)
                Crashlytics.logException(e)
            }
        }
    }

    private fun loginWithAccessToken(password: String) {
        val observable: Observable<AccessTokenResponse> = accessTokenUseCase.buildObservable()
        accessTokenUseCase.code = password
        accessTokenUseCase.executeSafely(observable
                .doOnSubscribe {
                    showHideProgress(true)
                }
                .doOnNext {

                }
                .doOnError {
                    handleError(it)
                }
                .doOnComplete {
                    showHideProgress(false)
                })
    }

    private fun loginBasic(twoFactorCode: String? = null, isEnterprise: Boolean? = false, enterpriseUrl: String? = null) {
        loginUserCase.setAuthBody(twoFactorCode)
        loginUserCase.executeSafely(loginUserCase.buildObservable()
                .map {
                    interceptor.token = it.token ?: it.accessToken
                    return@map it
                }
                .flatMap({
                    loginWithAccessTokenUseCase.buildObservable()
                }, { accessToken, user ->
                    user.isLoggedIn = true
                    user.otpCode = twoFactorCode
                    user.token = accessToken.token ?: accessToken.accessToken
                    user.isEnterprise = isEnterprise
                    user.enterpriseUrl = enterpriseUrl
                    return@flatMap user
                })
                .flatMap { loginWithAccessTokenUseCase.insertUser(it) }
                .doOnSubscribe { showHideProgress(true) }
                .doOnNext {
                    Timber.e("$it")
                }
                .doOnError { handleError(it) }
                .doOnComplete { showHideProgress(false) }
        )

    }

    override fun onCleared() {
        loginUserCase.dispose()
        accessTokenUseCase.dispose()
        loginWithAccessTokenUseCase.dispose()
        super.onCleared()
    }
}