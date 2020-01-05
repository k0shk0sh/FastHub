package com.fastaccess.github.di.modules

import androidx.work.RxWorker
import com.fastaccess.fasthub.dagger.annotations.WorkerKey
import com.fastaccess.github.platform.works.MarkAsReadNotificationWorker
import com.fastaccess.fasthub.reactions.ReactionWorker
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
    abstract fun bindMarkAsReadNotificationWorker(worker: MarkAsReadNotificationWorker): RxWorker

    @Binds
    @IntoMap
    @WorkerKey(ReactionWorker::class)
    abstract fun bindReactionWorker(worker: ReactionWorker): RxWorker
}