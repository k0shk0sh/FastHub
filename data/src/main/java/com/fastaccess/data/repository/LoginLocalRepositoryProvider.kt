package com.fastaccess.data.repository

import com.fastaccess.data.persistence.dao.LoginDao
import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.data.repository.services.LoginService
import com.fastaccess.domain.repository.LoginRemoteRepository
import com.fastaccess.domain.response.AccessTokenResponse
import com.fastaccess.domain.response.AuthBodyModel
import com.fastaccess.domain.response.UserResponse
import io.reactivex.Maybe
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 11.05.18.
 */
class LoginLocalRepositoryProvider @Inject constructor(private val loginDao: LoginDao,
                                                       private val loginService: LoginService) : LoginLocalRepository, LoginRemoteRepository {
    override fun getLogin(): Maybe<LoginModel?> = loginDao.getLogin()
    override fun insert(login: LoginModel): Long = loginDao.insert(login)
    override fun update(login: LoginModel): Int = loginDao.update(login)
    override fun deleteLogin(login: LoginModel) = loginDao.deleteLogin(login)
    override fun loginAccessToken(): Observable<UserResponse> = loginService.loginAccessToken()
    override fun login(authModel: AuthBodyModel): Observable<AccessTokenResponse> = loginService.login(authModel)
    override fun getAccessToken(code: String, clientId: String, clientSecret: String, state: String,
                                redirectUrl: String) = loginService.getAccessToken(code, clientId, clientSecret, state, redirectUrl)
}

interface LoginLocalRepository {
    fun getLogin(): Maybe<LoginModel?>
    fun insert(login: LoginModel): Long
    fun update(login: LoginModel): Int
    fun deleteLogin(login: LoginModel)
}