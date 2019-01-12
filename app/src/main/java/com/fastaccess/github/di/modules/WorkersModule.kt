package com.fastaccess.github.di.modules

import androidx.work.RxWorker
import com.fastaccess.github.di.annotations.WorkerKey
import com.fastaccess.github.platform.works.MarkAsReadNotificationWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Kosh on 12.01.19.
 */
@Module abstract class WorkersModule {
    @Binds
    @IntoMap
    @WorkerKey(MarkAsReadNotificationWorker::class)
    abstract fun bindHelloWorldWorker(worker: MarkAsReadNotificationWorker): RxWorker
}