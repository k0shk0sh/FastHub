package com.fastaccess.github.platform.works

import android.app.Application
import androidx.work.*
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.extension.uiThread
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.EXTRA_TWO
import github.AddReactionMutation
import github.type.ReactionContent
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Kosh on 06.02.19.
 */
class ReactionWorker @Inject constructor(
    context: Application,
    private val workerParams: WorkerParameters,
    private val apolloClient: ApolloClient
) : RxWorker(context, workerParams) {

    private val content: String by lazy { workerParams.inputData.keyValueMap[EXTRA_TWO] as String }
    private val id: String by lazy { workerParams.inputData.keyValueMap[EXTRA] as String }

    override fun createWork(): Single<Result> = Single.fromObservable<ListenableWorker.Result> {
        Rx2Apollo.from(apolloClient.mutate(AddReactionMutation(id, ReactionContent.valueOf(content))))
            .doOnError { it.printStackTrace() }
    }.uiThread()


    companion object {

        fun enqueue(content: String, id: String) {
            val workManager = WorkManager.getInstance()
            workManager.enqueueUniqueWork(id,
                ExistingWorkPolicy.REPLACE, OneTimeWorkRequest.Builder(ReactionWorker::class.java)
                .setInputData(Data.Builder()
                    .putString(EXTRA, id)
                    .putString(EXTRA_TWO, content)
                    .build())
                .build())
        }
    }
}