package com.fastaccess.github.platform.works

import android.app.Application
import androidx.work.*
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.EXTRA_THREE
import com.fastaccess.github.utils.EXTRA_TWO
import github.AddReactionMutation
import github.RemoveReactionMutation
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
    private val add: Boolean by lazy { workerParams.inputData.keyValueMap[EXTRA_THREE] as Boolean }

    override fun createWork(): Single<Result> = Single.fromCallable {
        kotlin.runCatching {
            if (add) {
                Rx2Apollo.from(apolloClient.mutate(AddReactionMutation(id, ReactionContent.valueOf(content)))).blockingSingle()
            } else {
                Rx2Apollo.from(apolloClient.mutate(RemoveReactionMutation(id, ReactionContent.valueOf(content)))).blockingSingle()
            }
        }.getOrNull()
        return@fromCallable Result.success()
    }


    companion object {

        fun enqueue(content: String, id: String, add: Boolean = true) {
            val workManager = WorkManager.getInstance()
            workManager.enqueueUniqueWork(id,
                ExistingWorkPolicy.REPLACE, OneTimeWorkRequest.Builder(ReactionWorker::class.java)
                .setInputData(Data.Builder()
                    .putString(EXTRA, id)
                    .putString(EXTRA_TWO, content)
                    .putBoolean(EXTRA_THREE, add)
                    .build())
                .build())
        }
    }
}