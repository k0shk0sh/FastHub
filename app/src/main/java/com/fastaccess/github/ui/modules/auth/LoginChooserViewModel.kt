package com.fastaccess.github.ui.modules.auth

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.fastaccess.data.persistence.db.FastHubDatabase
import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.data.repository.LoginRepository
import com.fastaccess.domain.di.AuthenticationInterceptor
import com.fastaccess.github.usecase.auth.GetAccessTokenUseCase
import com.fastaccess.github.usecase.auth.LoginWithAccessTokenUseCase
import com.fastaccess.github.base.utils.REDIRECT_URL
import io.reactivex.Completable
import javax.inject.Inject

/**
 * Created by Kosh on 2018-11-23.
 */
class LoginChooserViewModel @Inject constructor(
    private val accessTokenUseCase: GetAccessTokenUseCase,
    private val loginWithAccessTokenUseCase: LoginWithAccessTokenUseCase,
    private val interceptor: AuthenticationInterceptor,
    private val loginRepositoryProvider: LoginRepository,
    private val fastHubDatabase: FastHubDatabase
) : com.fastaccess.github.base.BaseViewModel() {

    val loggedInUser = MutableLiveData<LoginModel>()
    val loggedInUsers = MutableLiveData<List<LoginModel?>>()

    init {
        justSubscribe(loginRepositoryProvider.getAll()
            .doOnSuccess {
                loggedInUsers.postValue(ArrayList(it))
            }
            .toObservable())
    }

    fun handleBrowserLogin(uri: Uri) {
        if (uri.toString().startsWith(REDIRECT_URL)) {
            val token = uri.getQueryParameter("code")
            token?.let { code ->
                accessTokenUseCase.code = code
                add(callApi(accessTokenUseCase.buildObservable()
                    .flatMap { accessTokenResponse ->
                        interceptor.token = accessTokenResponse.token ?: accessTokenResponse.accessToken ?: ""
                        return@flatMap loginWithAccessTokenUseCase.buildObservable()
                            .flatMap {
                                it.token = interceptor.token
                                it.isLoggedIn = true
                                loginWithAccessTokenUseCase.insertUser(it)
                            }
                    })
                    .subscribe({ loggedInUser.postValue(it) }, { it.printStackTrace() }))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        accessTokenUseCase.dispose()
        loginWithAccessTokenUseCase.dispose()
    }

    fun reLogin(user: LoginModel): Completable = user.let { me ->
        loginRepositoryProvider.logoutAll() // logout everyone in case of adding account
        fastHubDatabase.clearAll() // clear everything in db and start fresh!
        me.isLoggedIn = true
        return@let loginRepositoryProvider.update(me)
    }
}