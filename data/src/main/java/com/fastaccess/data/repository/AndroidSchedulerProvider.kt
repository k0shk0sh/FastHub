package com.fastaccess.data.repository

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Kosh on 2019-05-09.
 */
class AndroidSchedulerProvider @Inject constructor() : SchedulerProvider {
    override fun uiThread(): Scheduler = AndroidSchedulers.mainThread()
    override fun ioThread(): Scheduler = Schedulers.io()
    override fun computationThread(): Scheduler = Schedulers.computation()
}

interface SchedulerProvider {
    fun uiThread(): Scheduler
    fun ioThread(): Scheduler
    fun computationThread(): Scheduler
}