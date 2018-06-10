package com.fastaccess.domain.repository

import io.reactivex.Observable

/**
 * Created by Kosh on 10.06.18.
 */
interface UserRemoteRepository {
    fun getUser(): Observable<*>
}