package com.fastaccess.github.platform.workmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.RxWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.fastaccess.github.di.components.WorkerSubComponent
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class DaggerWorkerFactory @Inject constructor(
    private val workerSubComponent: WorkerSubComponent.Builder
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ) = workerSubComponent
        .workerParameters(workerParameters)
        .build().run {
            createWorker(workerClassName, workers())
        }

    private fun createWorker(
        workerClassName: String,
        workers: Map<Class<out RxWorker>, Provider<RxWorker>>
    ): ListenableWorker? = try {
        val workerClass = Class.forName(workerClassName).asSubclass(RxWorker::class.java)

        var provider = workers[workerClass]
        if (provider == null) {
            for ((key, value) in workers) {
                if (workerClass.isAssignableFrom(key)) {
                    provider = value
                    break
                }
            }
        }
        if (provider == null) {
            throw IllegalArgumentException("Missing binding for $workerClassName")
        }

        provider.get()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}
