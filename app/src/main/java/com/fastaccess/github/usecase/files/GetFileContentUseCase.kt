package com.fastaccess.github.usecase.files

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.FileConentModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import github.GetFileContentQuery
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 2019-07-30.
 */
class GetFileContentUseCase @Inject constructor(
    private val apolloClient: ApolloClient,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var path: String = ""

    override fun buildObservable(): Observable<FileConentModel> = Rx2Apollo.from(
        apolloClient.query(
            GetFileContentQuery.builder()
                .login(login)
                .repo(repo)
                .path(path)
                .build()
        )
    )
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
        .map {
            val response = it.data()?.repositoryOwner?.repository?.`object` as? GetFileContentQuery.AsBlob
            return@map FileConentModel(response?.text, response?.isBinary, response?.isTruncated, response?.byteSize)
        }

}