package com.fastaccess.ui.modules.repos.wiki

import android.content.Intent
import com.fastaccess.BuildConfig
import com.fastaccess.R
import com.fastaccess.data.dao.wiki.FirebaseWikiConfigModel
import com.fastaccess.data.dao.wiki.WikiContentModel
import com.fastaccess.data.dao.wiki.WikiSideBarModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.jsoup.JsoupProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.github.b3er.rxfirebase.database.RxFirebaseDatabase
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Observable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.HttpException
import java.util.*

/**
 * Created by Kosh on 13 Jun 2017, 8:14 PM
 */
class WikiPresenter : BasePresenter<WikiMvp.View>(), WikiMvp.Presenter {

    @com.evernote.android.state.State var repoId: String? = null
    @com.evernote.android.state.State var login: String? = null
    private var firebaseWikiConfigModel: FirebaseWikiConfigModel? = null

    override fun onActivityCreated(intent: Intent?) {
        if (intent != null) {
            val bundle = intent.extras
            repoId = bundle?.getString(BundleConstant.ID)
            login = bundle?.getString(BundleConstant.EXTRA)
            val page = bundle?.getString(BundleConstant.EXTRA_TWO)
            if (!page.isNullOrEmpty()) {
                sendToView { it.onSetPage(page) }
            }
            if (!repoId.isNullOrEmpty() && !login.isNullOrEmpty()) {
                onSidebarClicked(WikiSideBarModel("Home", "$login/$repoId/wiki" + if (!page.isNullOrEmpty()) "/$page" else ""))
            }
        }
    }

    override fun onSidebarClicked(sidebar: WikiSideBarModel) {
        if (firebaseWikiConfigModel == null) {
            manageDisposable(
                RxHelper.getSingle(RxFirebaseDatabase.data(FirebaseDatabase.getInstance().reference.child("github_wiki")))
                    .doOnSubscribe { sendToView { it.showProgress(0) } }
                    .map {
                        firebaseWikiConfigModel = FirebaseWikiConfigModel.map(it.value as? HashMap<String, String>)
                        return@map firebaseWikiConfigModel
                    }
                    .subscribe(
                        { callApi(sidebar) },
                        { callApi(sidebar) }
                    )
            )
        } else {
            callApi(sidebar)
        }
    }

    private fun callApi(sidebar: WikiSideBarModel) {
        manageViewDisposable(RxHelper.getObservable(JsoupProvider.getWiki().getWiki(sidebar.link))
            .flatMap { s -> RxHelper.getObservable(getWikiContent(s)) }
            .doOnSubscribe { sendToView { it.showProgress(0) } }
            .subscribe(
                { response -> sendToView { view -> view.onLoadContent(response) } },
                { throwable ->
                    if (throwable is HttpException) {
                        if (throwable.code() == 404) {
                            sendToView { it.showPrivateRepoError() }
                            return@subscribe
                        }
                    }
                    onError(throwable)
                },
                { sendToView { it.hideProgress() } }
            )
        )
    }

    private fun getWikiContent(body: String?): Observable<WikiContentModel> {
        return Observable.fromPublisher { s ->
            val document: Document = Jsoup.parse(body, "")
            val firebaseWikiConfigModel = firebaseWikiConfigModel ?: kotlin.run {
                s.onNext(WikiContentModel("<h2 align='center'>No Wiki</h4>", "", arrayListOf()))
                s.onComplete()
                return@fromPublisher
            }
            val wikiWrapper = document.select(firebaseWikiConfigModel.wikiWrapper)
            if (!wikiWrapper.isNullOrEmpty()) {
                val header = wikiWrapper.select(firebaseWikiConfigModel.wikiHeader)?.text()
                val subHeaderText = wikiWrapper.select(firebaseWikiConfigModel.wikiSubHeader)?.text()
                val wikiContent = wikiWrapper.select(firebaseWikiConfigModel.wikiContent)
                val wikiBody = wikiContent?.select(firebaseWikiConfigModel.wikiBody)?.html()
                val rightBarList = wikiContent?.select(firebaseWikiConfigModel.sideBarUl)?.select(firebaseWikiConfigModel.sideBarList)
                val headerHtml = "<div class='gh-header-meta'><h1>$header</h1><p>$subHeaderText</p></div>"
                val content = "$headerHtml $wikiBody"
                s.onNext(WikiContentModel(content, null, rightBarList?.map {
                    WikiSideBarModel(
                        it.select(firebaseWikiConfigModel.sideBarListTitle).text(),
                        it.select(firebaseWikiConfigModel.sideBarListTitle).attr(firebaseWikiConfigModel.sideBarListLink)
                    )
                } ?: listOf()))
            } else {
                s.onNext(WikiContentModel("<h2 align='center'>No Wiki</h4>", "", arrayListOf()))
            }

            s.onComplete()
        }
    }
}