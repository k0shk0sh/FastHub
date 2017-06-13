package com.fastaccess.ui.modules.repos.wiki

import android.content.Intent
import com.fastaccess.data.dao.wiki.WikiContentModel
import com.fastaccess.data.dao.wiki.WikiSideBarModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.jsoup.JsoupProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Created by Kosh on 13 Jun 2017, 8:14 PM
 */
class WikiPresenter : BasePresenter<WikiMvp.View>(), WikiMvp.Presenter {

    @com.evernote.android.state.State var repoId: String? = null
    @com.evernote.android.state.State var login: String? = null

    override fun onActivityCreated(intent: Intent?) {
        if (intent != null) {
            val bundle = intent.extras
            repoId = bundle.getString(BundleConstant.ID)
            login = bundle.getString(BundleConstant.EXTRA)
            if (!repoId.isNullOrEmpty() && !login.isNullOrEmpty()) {
                onSidebarClicked(WikiSideBarModel("Home", "$login/$repoId/wiki"))
            }
        }
    }

    override fun onSidebarClicked(sidebar: WikiSideBarModel) {
        manageViewDisposable(RxHelper.getObserver(JsoupProvider.getWiki().getWiki(sidebar.link))
                .flatMap { s -> RxHelper.getObserver(getWikiContent(s)) }
                .doOnSubscribe { sendToView { it.showProgress(0) } }
                .subscribe({ response -> sendToView { view -> view.onLoadContent(response) } },
                        { throwable -> onError(throwable) }, { sendToView({ it.hideProgress() }) }))
    }

    private fun getWikiContent(body: String?): Observable<WikiContentModel> {
        return Observable.fromPublisher { s ->
            val document: Document = Jsoup.parse(body, "")
            val wikiWrapper = document.select("#wiki-wrapper")
            if (wikiWrapper.isNotEmpty()) {
//                val header = wikiWrapper.select(".gh-header-title").html()
                val wikiContent = wikiWrapper.select(".wiki-content")
                val content = wikiContent.select(".markdown-body").html()
                val rightBarList = wikiContent.select(".wiki-pages").select("li")
                val sidebarList = arrayListOf<WikiSideBarModel>()
                if (rightBarList.isNotEmpty()) {
                    rightBarList.onEach {
                        val sidebarTitle = it.select("a").text()
                        val sidebarLink = it.select("a").attr("href")
                        sidebarList.add(WikiSideBarModel(sidebarTitle, sidebarLink))
                    }
                }
                s.onNext(WikiContentModel(content, "", sidebarList))
            }
            s.onComplete()
        }
    }
}