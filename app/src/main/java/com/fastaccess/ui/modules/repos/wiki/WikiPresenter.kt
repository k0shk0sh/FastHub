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
            val page = bundle.getString(BundleConstant.EXTRA_TWO)
            if (!page.isNullOrEmpty()) {
                sendToView { it.onSetPage(page) }
            }
            if (!repoId.isNullOrEmpty() && !login.isNullOrEmpty()) {
                onSidebarClicked(WikiSideBarModel("Home", "$login/$repoId/wiki" +
                        if (!page.isNullOrEmpty()) "/$page" else ""))
            }
        }
    }

    override fun onSidebarClicked(sidebar: WikiSideBarModel) {
        manageViewDisposable(RxHelper.getObservable(JsoupProvider.getWiki().getWiki(sidebar.link))
                .flatMap { s -> RxHelper.getObservable(getWikiContent(s)) }
                .doOnSubscribe { sendToView { it.showProgress(0) } }
                .subscribe({ response -> sendToView { view -> view.onLoadContent(response) } },
                        { throwable -> onError(throwable) }, { sendToView({ it.hideProgress() }) }))
    }

    private fun getWikiContent(body: String?): Observable<WikiContentModel> {
        return Observable.fromPublisher { s ->
            try {
                val document: Document = Jsoup.parse(body, "")
                val wikiWrapper = document.select("#wiki-wrapper")
                if (wikiWrapper.isNotEmpty()) {
                    val cloneUrl = wikiWrapper.select(".clone-url")
//                val bottomRightBar = wikiWrapper.select(".wiki-custom-sidebar")
                    if (cloneUrl.isNotEmpty()) {
                        cloneUrl.remove()
                    }
//                if (bottomRightBar.isNotEmpty()) {
//                    bottomRightBar.remove()
//                }
                    val headerHtml = wikiWrapper.select(".gh-header .gh-header-meta")
                    val revision = headerHtml.select("a.history")
                    if (revision.isNotEmpty()) {
                        revision.remove()
                    }
                    val header = "<div class='gh-header-meta'>${headerHtml.html()}</div>"
                    val wikiContent = wikiWrapper.select(".wiki-content")
                    val content = header + wikiContent.select(".wiki-body").html()
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
                } else {
                    s.onNext(WikiContentModel("<h2 align='center'>No Wiki</h4>", "", arrayListOf()))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            s.onComplete()
        }
    }
}