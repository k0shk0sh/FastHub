package com.fastaccess.github.ui.modules.auth.login

import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.fastaccess.data.persistence.models.FastHubErrors
import com.fastaccess.data.persistence.models.ValidationError
import com.fastaccess.domain.response.AuthBodyModel
import com.fastaccess.github.BuildConfig
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.di.modules.AuthenticationInterceptor
import com.fastaccess.github.ui.modules.auth.usecase.GetAccessTokenUseCase
import com.fastaccess.github.ui.modules.auth.usecase.LoginUseCase
import com.fastaccess.github.ui.modules.auth.usecase.LoginWithAccessTokenUseCase
import okhttp3.Credentials
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
                loginBasic()
                if (isBasicAuth) {
                } else {
                    loginWithAccessToken()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Crashlytics.logException(e)
            }
        }
    }

    private fun loginWithAccessToken() {
        accessTokenUseCase.executeSafely(accessTokenUseCase.buildObservable()
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

    private fun loginBasic() {
        loginUserCase.authBodyModel = AuthBodyModel(BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_SECRET,
                "fasthub://login", arrayListOf("user", "repo", "gist", "notifications", "read:org"),
                BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID, "fasthub://login")
        loginUserCase.executeSafely(loginUserCase.buildObservable()
                .doOnSubscribe {
                    showHideProgress(true)
                }
                .doOnNext {
                    val token = it.token ?: it.accessToken
                    if (token != null) {
                        loginWithAccessTokenUseCase.executeSafely(loginWithAccessTokenUseCase.buildObservable()
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
                    } else {
                        error.postValue(FastHubErrors(FastHubErrors.ErrorType.OTHER, resId = R.string.failed_login))
                    }
                }
                .doOnError {
                    handleError(it)
                }
                .doOnComplete {
                    showHideProgress(false)
                })
    }

    override fun onCleared() {
        loginUserCase.dispose()
        accessTokenUseCase.dispose()
        loginWithAccessTokenUseCase.dispose()
        super.onCleared()
    }
}