package com.fastaccess.fasthub.commit.list

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Input
import com.fastaccess.data.model.FullCommitModel
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.fasthub.commit.usecase.GetPullRequestCommitListUseCase
import com.fastaccess.github.base.BaseViewModel
import javax.inject.Inject

class CommitListViewModel @Inject constructor(
    private val usecase: GetPullRequestCommitListUseCase
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    private val list = arrayListOf<FullCommitModel>()
    val commitsLiveData = MutableLiveData<List<FullCommitModel>>()
    val changedFilesCount = MutableLiveData<Int>()


    fun loadData(
        login: String,
        repo: String,
        number: Int,
        reload: Boolean = false
    ) {
        if (reload) {
            pageInfo = null
            list.clear()
        }
        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        usecase.page = Input.optional(pageInfo?.endCursor)
        usecase.login = login
        usecase.repo = repo
        usecase.number = number
        justSubscribe(usecase.buildObservable()
            .doOnNext {
                changedFilesCount.postValue(usecase.changedFiles)
                this.pageInfo = it.first
                postCounter(it.second.totalCount)
                list.addAll(it.second.t)
                commitsLiveData.postValue(ArrayList(list))
            })
    }

    fun hasNext() = pageInfo?.hasNextPage == true
}