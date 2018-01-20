package com.fastaccess.ui.modules.trending.fragment

import android.view.View
import com.fastaccess.data.dao.TrendingModel
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.jsoup.JsoupProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.RepoPagerActivity
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/**
 * Created by Kosh on 30 May 2017, 11:04 PM
 */

class TrendingFragmentPresenter : BasePresenter<TrendingFragmentMvp.View>(), TrendingFragmentMvp.Presenter {

    private var disposel: Disposable? = null

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
        disposel?.let { if (!it.isDisposed) it.dispose() }
        disposel = RxHelper.getObservable(JsoupProvider.getTrendingService().getTrending(
                (if (!InputHelper.isEmpty(lang)) lang.replace(" ".toRegex(), "-") else "").toLowerCase(), since))
                .flatMap { s -> RxHelper.getObservable(getTrendingObservable(s)) }
                .doOnSubscribe {
                    sendToView {
                        it.showProgress(0)
                        it.clearAdapter()
                    }
                }
                .subscribe({ response -> sendToView { view -> view.onNotifyAdapter(response) } },
                        { throwable -> onError(throwable) }, { sendToView({ it.hideProgress() }) })
        manageDisposable(disposel)
    }


    private fun getTrendingObservable(response: String): Observable<TrendingModel> {
        return Observable.fromPublisher { s ->
            val document: Document = Jsoup.parse(response, "")
            val repoList = document.select(".repo-list")
            if (repoList.isNotEmpty()) {
                val list: Elements? = repoList.select("li")
                list?.let {
                    if (list.isNotEmpty()) {
                        it.onEach {
                            val title = it.select(".d-inline-block > h3 > a").text()
                            val description = it.select(".py-1 > p").text()
                            val stars = it.select(".f6 > a[href*=/stargazers]").text()
                            val forks = it.select(".f6 > a[href*=/network]").text()
                            var todayStars = it.select(".f6 > span.float-right").text()
                            if (todayStars.isNullOrBlank()) {
                                todayStars = it.select(".f6 > span.float-sm-right").text()
                            }
                            var language = it.select(".f6 .mr-3 > span[itemprop=programmingLanguage]").text()
                            if (language.isNullOrBlank()) {
                                language = it.select(".f6 span[itemprop=programmingLanguage]").text()
                            }
                            s.onNext(TrendingModel(title, description, language, stars, forks, todayStars))
                        }
                    }
                }
            }
            s.onComplete()
        }
    }
}