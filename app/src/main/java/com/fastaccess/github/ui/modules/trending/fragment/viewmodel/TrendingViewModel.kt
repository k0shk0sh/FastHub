package com.fastaccess.github.ui.modules.trending.fragment.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.fastaccess.data.model.FirebaseTrendingConfigModel
import com.fastaccess.data.model.TrendingModel
import com.fastaccess.data.model.parcelable.FilterTrendingModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.services.ScrapService
import com.github.b3er.rxfirebase.database.RxFirebaseDatabase
import com.google.firebase.database.DatabaseReference
import org.jsoup.Jsoup
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Kosh on 20.10.18.
 */
class TrendingViewModel @Inject constructor(
    @Named(value = "github_trending") private val databaseReference: DatabaseReference,
    private val service: ScrapService,
    private val schedulerProvider: SchedulerProvider
) : com.fastaccess.github.base.BaseViewModel() {

    private var trendingModel: FirebaseTrendingConfigModel? = null
    val trendingLiveData = MutableLiveData<List<TrendingModel>>()
    var filterTrendingModel = FilterTrendingModel()

    @SuppressLint("DefaultLocale") fun load(
        model: FilterTrendingModel,
        locale: Locale
    ) {
        val firebaseTrendingConfigModel = trendingModel

        if (firebaseTrendingConfigModel == null) {
            justSubscribe(
                RxFirebaseDatabase.data(databaseReference)
                    .doOnError { loadTrendings(model, locale, FirebaseTrendingConfigModel()) }
                    .doOnSuccess {
                        @Suppress("UNCHECKED_CAST")
                        Timber.e("${it.value}")
                        trendingModel = FirebaseTrendingConfigModel.map(it.value as? HashMap<String, String>)
                        loadTrendings(model, locale, requireNotNull(trendingModel))
                    })
        } else {
            loadTrendings(model, locale, firebaseTrendingConfigModel)
        }
    }

    private fun loadTrendings(
        model: FilterTrendingModel,
        locale: Locale,
        trendingModel: FirebaseTrendingConfigModel
    ) {
        val language = if (model.lang == "All") "" else model.lang.replace(" ", "_").toLowerCase(locale)
        trendingModel.pathUrl += language
        justSubscribe(service.getTrending(trendingModel.pathUrl, model.since.name.toLowerCase(locale))
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
                trendingLiveData.postValue(it)
            })
    }
}