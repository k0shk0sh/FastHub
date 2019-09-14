package com.fastaccess.github.ui.modules.trending.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fastaccess.data.model.FirebaseTrendingConfigModel
import com.fastaccess.data.model.TrendingModel
import com.fastaccess.data.model.parcelable.FilterTrendingModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.services.ScrapService
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
    private val retrofitBuilder: Retrofit.Builder,
    private val schedulerProvider: SchedulerProvider
) : com.fastaccess.github.base.BaseViewModel() {

    private var trendingModel = FirebaseTrendingConfigModel()
    private var service: ScrapService? = null
    val trendingLiveData = MutableLiveData<List<TrendingModel>>()
    var filterTrendingModel = FilterTrendingModel()

    init {
        if (BuildConfig.DEBUG) {
            onFirstLoad()
        } else {
            justSubscribe(RxFirebaseDatabase.childEvents(databaseReference)
                .map { it.dataSnapshot() }
                .doOnError { onFirstLoad() } // if we failed to load firebase database lets fallback to local data
                .doOnNext {
                    @Suppress("UNCHECKED_CAST")
                    trendingModel = FirebaseTrendingConfigModel.map(it.value as? HashMap<String, String>)
                })
        }
    }

    private fun onFirstLoad() {
        service = retrofitBuilder.baseUrl(trendingModel.pathUrl).build().create(ScrapService::class.java)
    }

    fun load(model: FilterTrendingModel) {
        val language = if (model.lang == "All") "" else model.lang.replace(" ", "_").toLowerCase()
        service?.getTrending(language, model.since.name.toLowerCase())?.let { observable ->
            justSubscribe(observable
                .subscribeOn(schedulerProvider.ioThread())
                .observeOn(schedulerProvider.uiThread())
                .map { html ->
                    val document = Jsoup.parse(html, "")
                    val list = document.select(trendingModel.listName)
                    val trendingList = arrayListOf<TrendingModel>()
                    list.select(trendingModel.listNameSublistTag)?.let { li ->
                        trendingList.addAll(li.map { body ->
                            val trendingLang = kotlin.runCatching { body.select(trendingModel.language).text() }
                                .getOrNull() ?: kotlin.runCatching { body.select(trendingModel.languageFallback).text() }.getOrNull()
                            val todayStars = kotlin.runCatching { body.select(trendingModel.todayStars).text() }
                                .getOrNull() ?: kotlin.runCatching { body.select(trendingModel.todayStarsFallback).text() }.getOrNull()
                            val title = kotlin.runCatching { body.select(trendingModel.title).text() }.getOrNull()
                            val description = kotlin.runCatching { body.select(trendingModel.description).text() }.getOrNull()
                            val stars = kotlin.runCatching { body.select(trendingModel.stars).text() }.getOrNull()
                            val forks = kotlin.runCatching { body.select(trendingModel.forks).text() }.getOrNull()
                            TrendingModel(title, description, trendingLang, stars, forks, todayStars)
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