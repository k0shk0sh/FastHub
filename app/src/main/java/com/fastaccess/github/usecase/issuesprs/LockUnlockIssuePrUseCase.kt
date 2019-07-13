package com.fastaccess.github.usecase.issuesprs

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.LockUnlockEventModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.repository.IssueRepositoryProvider
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.me
import github.LockMutation
import github.UnlockMutation
import github.type.LockReason
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

/**
 * Created by Kosh on 16.02.19.
 */
class LockUnlockIssuePrUseCase @Inject constructor(
    private val issueRepositoryProvider: IssueRepositoryProvider,
    private val apolloClient: ApolloClient,
    private val loginRepositoryProvider: LoginRepositoryProvider
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var number: Int = -1
    var lockReason: LockReason? = null
    var lock: Boolean = false

    override fun buildObservable(): Observable<TimelineModel> = issueRepositoryProvider.getIssueByNumberMaybe("$login/$repo", number)
        .flatMapObservable { issue ->
            if (lockReason == null) {
                Rx2Apollo.from(apolloClient.mutate(LockMutation(issue.id, Input.optional(lockReason))))
            } else {
                Rx2Apollo.from(apolloClient.mutate(UnlockMutation(issue.id)))
            }
                .map {
                    issue.locked = lock == true
                    issue.activeLockReason = lockReason?.rawValue()
                    issueRepositoryProvider.upsert(issue)
                    val me = loginRepositoryProvider.getLoginBlocking()?.me()
                    return@map if (lock) {
                        TimelineModel(lockUnlockEventModel = LockUnlockEventModel(Date(), me, lockReason?.rawValue(), null, true))
                    } else {
                        TimelineModel(lockUnlockEventModel = LockUnlockEventModel(Date(), me, null, null, false))
                    }
                }
        }
}