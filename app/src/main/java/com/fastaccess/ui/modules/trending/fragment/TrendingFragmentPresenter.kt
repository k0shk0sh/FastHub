package com.fastaccess.ui.modules.trending.fragment

import android.view.View
import com.fastaccess.data.dao.TrendingModel
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.Logger
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.jsoup.JsoupProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.RepoPagerActivity
import io.reactivex.Observable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Created by Kosh on 30 May 2017, 11:04 PM
 */

class TrendingFragmentPresenter : BasePresenter<TrendingFragmentMvp.View>(), TrendingFragmentMvp.Presenter {

    private val trendingList: ArrayList<TrendingModel> = ArrayList()

    override fun getTendingList(): ArrayList<TrendingModel> {
        return trendingList
    }

    override fun onItemLongClick(position: Int, v: View?, item: TrendingModel?) {}

    override fun onItemClick(position: Int, v: View?, item: TrendingModel?) {
        val split = item?.title?.trim()?.split("/")!!
        v?.context!!.startActivity(RepoPagerActivity.createIntent(v.context!!, split[1].trim(), split[0].trim()))
    }

    override fun onCallApi(lang: String, since: String) {
        manageViewDisposable(RxHelper.getObserver(JsoupProvider.getTrendingService().getTrending(
                (if (!InputHelper.isEmpty(lang)) lang.replace(" ".toRegex(), "-") else "").toLowerCase(), since))
                .flatMap { s -> RxHelper.getObserver(getTrendingObservable(s)) }
                .doOnSubscribe { sendToView { it.showProgress(0) } }
                .subscribe({ response -> sendToView { view -> view.onNotifyAdapter(response) } },
                        { throwable -> onError(throwable) }, { sendToView({ it.hideProgress() }) }))
    }


    fun getTrendingObservable(response: String): Observable<TrendingModel> {
        return Observable.fromPublisher { s ->
            val document: Document = Jsoup.parse(response, "")
            val repoList = document.select(".repo-list")
            if (repoList.isNotEmpty()) {
                val list = repoList.select("li")
                if (list.isNotEmpty()) {
                    list.onEach {
                        val title = it.select(".d-inline-block > h3 > a").text()
                        val description = it.select(".py-1 > p").text()
                        val stars = it.select(".f6 > a[href*=/stargazers]").text()
                        val forks = it.select(".f6 > a[href*=/network]").text()
                        val todayStars = it.select(".f6 > span.float-right").text()
                        val language = it.select(".f6 .mr-3 > span[itemprop=programmingLanguage]").text()
                        Logger.e(title, description, stars, forks, todayStars, language)
                        s.onNext(TrendingModel(title, description, language, stars, forks, todayStars))
                    }
                }
            }
            s.onComplete()
        }
    }
}