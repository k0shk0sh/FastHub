package com.fastaccess.github.ui.modules.trending.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fastaccess.data.model.FirebaseTrendingConfigModel
import com.fastaccess.data.model.TrendingModel
import com.fastaccess.domain.repository.services.ScrapService
import com.fastaccess.github.BuildConfig
import com.fastaccess.github.base.BaseViewModel
import com.github.b3er.rxfirebase.database.RxFirebaseDatabase
import com.google.firebase.database.DatabaseReference
import org.jsoup.Jsoup
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Kosh on 20.10.18.
 */
class TrendingViewModel @Inject constructor(
    @Named(value = "github_trending") private val databaseReference: DatabaseReference,
    private val retrofitBuilder: Retrofit.Builder
) : BaseViewModel() {

    private var trendingModel = FirebaseTrendingConfigModel()
    private var service: ScrapService? = null
    val trendingLiveData = MutableLiveData<List<TrendingModel>>()
    var lang: String = ""
    var since: String = "daily"

    init {
        if (BuildConfig.DEBUG) {
            onFirstLoad()
        } else {
            justSubscribe(RxFirebaseDatabase.childEvents(databaseReference)
                .map { it.dataSnapshot() }
                .doOnError { onFirstLoad() } // if we failed to load firebase database lets fallback to local data
                .doOnNext {
                    trendingModel = FirebaseTrendingConfigModel.map(it.value as? HashMap<String, String>)
                    onFirstLoad()
                })
        }
    }

    private fun onFirstLoad() {
        service = retrofitBuilder.baseUrl(trendingModel.pathUrl).build().create(ScrapService::class.java)
        load(lang, since) // load first items!
    }

    fun load(lan: String, since: String) {
        val language = if (lan == "All") "" else lan.replace(" ", "_").toLowerCase()
        service?.getTrending(language, since)?.let { observable ->
            justSubscribe(observable
                .map { html ->
                    val document = Jsoup.parse(html, "")
                    val list = document.select(trendingModel.listName)
                    val trendingList = arrayListOf<TrendingModel>()
                    list.select(trendingModel.listNameSublistTag)?.let { li ->
                        trendingList.addAll(li.map { body ->
                            val language = kotlin.runCatching { body.select(trendingModel.language).text() }
                                .getOrNull() ?: kotlin.runCatching { body.select(trendingModel.languageFallback).text() }.getOrNull()
                            val todayStars = kotlin.runCatching { body.select(trendingModel.todayStars).text() }
                                .getOrNull() ?: kotlin.runCatching { body.select(trendingModel.todayStarsFallback).text() }.getOrNull()

                            TrendingModel(
                                body.select(trendingModel.title).text(), body.select(trendingModel.description).text(), language,
                                body.select(trendingModel.stars).text(), body.select(trendingModel.forks).text(), todayStars
                            )
                        })
                    }
                    return@map trendingList
                }
                .doOnNext {
                    Timber.e("here :) (${it.size})")
                    trendingLiveData.postValue(it)
                })
        }
    }
}