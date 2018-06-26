package com.fastaccess.github.usecase.main

import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.data.repository.FeedsRepositoryProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 26.06.18.
 */
class FeedsMainScreenUseCase @Inject constructor(private val feedsRepositoryProvider: FeedsRepositoryProvider,
                                                 private val gson: Gson) : BaseObservableUseCase() {
    override fun buildObservable(): Observable<List<FeedModel>> = feedsRepositoryProvider.getMainFeeds()
            .map { gson.fromJson<List<FeedModel>>(gson.toJson(it), object : TypeToken<List<FeedModel>>() {}.type) }
}