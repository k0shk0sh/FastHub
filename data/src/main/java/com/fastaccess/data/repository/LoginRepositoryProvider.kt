package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import com.fastaccess.data.persistence.dao.LoginDao
import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.domain.repository.LoginRemoteRepository
import com.fastaccess.domain.repository.services.LoginService
import com.fastaccess.domain.response.AccessTokenResponse
import com.fastaccess.domain.response.AuthBodyModel
import com.fastaccess.domain.response.UserResponse
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

/**
 * Created by Kosh on 11.05.18.
 */
class LoginRepositoryProvider @Inject constructor(private val loginDao: LoginDao,
                                                  private val loginService: LoginService) : LoginLocalRepository, LoginRemoteRepository {
    override fun getLogin(): Maybe<LoginModel?> = loginDao.getLogin()
    override fun getLoginBlocking(): LoginModel? = loginDao.getLoginBlocking()
    override fun getAllLiveData(): LiveData<LoginModel?> = loginDao.getAllLiveData()
    override fun getAll(): Maybe<List<LoginModel?>> = loginDao.getAll()
    override fun getLoggedInUsers(): Observable<List<LoginModel?>> = loginDao.getLoggedInUsers().toObservable()
    override fun insert(login: LoginModel): Long = loginDao.insert(login)
    override fun update(login: LoginModel): Completable = Completable.fromCallable { loginDao.update(login) }
    override fun deleteLogin(login: LoginModel) = loginDao.deleteLogin(login)
    override fun logoutAll() = loginDao.logoutAll()
    override fun loginAccessToken(): Observable<UserResponse> = loginService.loginAccessToken()
    override fun login(authModel: AuthBodyModel): Observable<AccessTokenResponse> = loginService.login(authModel)
    override fun getAccessToken(code: String, clientId: String, clientSecret: String, state: String,
                                redirectUrl: String) = loginService.getAccessToken(
        "https://github.com/login/oauth/access_token", code, clientId, clientSecret, state, redirectUrl
    )
}

interface LoginLocalRepository {
    fun getLogin(): Maybe<LoginModel?>
    fun getLoginBlocking(): LoginModel?
    fun getAllLiveData(): LiveData<LoginModel?>
    fun getAll(): Maybe<List<LoginModel?>>
    fun getLoggedInUsers(): Observable<List<LoginModel?>>
    fun insert(login: LoginModel): Long
    fun update(login: LoginModel): Completable
    fun deleteLogin(login: LoginModel)
    fun logoutAll()
}

fun LoginRepositoryProvider.isMe(login: String, action: (isMe: Boolean) -> Unit): Disposable {
    return getLogin().subscribe({ action.invoke(login == it?.login) }, { action.invoke(false) })
}