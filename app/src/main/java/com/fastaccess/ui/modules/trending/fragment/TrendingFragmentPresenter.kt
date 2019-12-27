package com.fastaccess.ui.modules.trending.fragment

import android.view.View
import com.fastaccess.data.dao.FirebaseTrendingConfigModel
import com.fastaccess.data.dao.TrendingModel
import com.fastaccess.helper.Logger
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.jsoup.JsoupProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.RepoPagerActivity
import com.github.b3er.rxfirebase.database.RxFirebaseDatabase
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Kosh on 30 May 2017, 11:04 PM
 */

class TrendingFragmentPresenter : BasePresenter<TrendingFragmentMvp.View>(), TrendingFragmentMvp.Presenter {

    private var disposel: Disposable? = null

    private val trendingList: ArrayList<TrendingModel> = ArrayList()
    private var firebaseTrendingConfigModel: FirebaseTrendingConfigModel? = null

    override fun getTendingList(): ArrayList<TrendingModel> {
        return trendingList
    }

    override fun onItemLongClick(position: Int, v: View?, item: TrendingModel?) {}

    override fun onItemClick(position: Int, v: View?, item: TrendingModel?) {
        val split = item?.title?.trim()?.split("/") ?: return
        v?.context?.let { it.startActivity(RepoPagerActivity.createIntent(it, split[1].trim(), split[0].trim())) }
    }

    override fun onCallApi(lang: String, since: String) {
        disposel?.let { if (!it.isDisposed) it.dispose() }
        val config = firebaseTrendingConfigModel

        if (config == null) {
            manageDisposable(RxHelper.getSingle(RxFirebaseDatabase.data(FirebaseDatabase.getInstance().reference.child("github_trending")))
                .doOnSubscribe { sendToView { it.showProgress(0) } }
                .map {
                    firebaseTrendingConfigModel = FirebaseTrendingConfigModel
                        .map(it.value as? HashMap<String, String>)
                    return@map firebaseTrendingConfigModel
                }
                .subscribe(
                    { callApi(lang, since) },
                    { callApi(lang, since) }
                )
            )
        } else {
            callApi(lang, since)
        }
    }

    private fun callApi(
        lang: String,
        since: String
    ) {
        val model = firebaseTrendingConfigModel ?: FirebaseTrendingConfigModel()

        val language = if (lang == "All") "" else lang.replace(" ", "_").toLowerCase(Locale.getDefault())

        disposel = RxHelper.getObservable(JsoupProvider.getTrendingService(model.pathUrl).getTrending(language, since))
            .doOnSubscribe {
                sendToView {
                    it.showProgress(0)
                    it.clearAdapter()
                }
            }.flatMap {
                RxHelper.getObservable(getTrendingObservable(it.body() ?: "", model))
            }.subscribe(
                { response -> sendToView { view -> view.onNotifyAdapter(response) } },
                { throwable -> onError(throwable) },
                { sendToView { it.hideProgress() } }
            )
        manageDisposable(disposel)
    }


    private fun getTrendingObservable(html: String, trendingModel: FirebaseTrendingConfigModel): Observable<List<TrendingModel>> {
        return Observable.fromPublisher { s ->
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
            Logger.e(trendingList as List<Any>?)
            s.onNext(trendingList)
            s.onComplete()
        }
    }
}