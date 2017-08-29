package com.fastaccess.ui.modules.repos.git

import android.content.Intent
import com.fastaccess.helper.BundleConstant
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by kosh on 29/08/2017.
 */
class EditRepoFilePresenter : BasePresenter<EditRepoFileMvp.View>(), EditRepoFileMvp.Presenter {

    @com.evernote.android.state.State var path: String? = null
    @com.evernote.android.state.State var repoId: String? = null
    @com.evernote.android.state.State var login: String? = null
    @com.evernote.android.state.State var isEdit: Boolean? = null
    @com.evernote.android.state.State var contentUrl: String? = null
    var downloadedContent: String? = null

    override fun onInit(intent: Intent?) {
        if (downloadedContent.isNullOrBlank()) {
            intent?.let {
                it.extras?.let {
                    repoId = it.getString(BundleConstant.ID)
                    login = it.getString(BundleConstant.EXTRA)
                    path = it.getString(BundleConstant.EXTRA_TWO)
                    contentUrl = it.getString(BundleConstant.EXTRA_THREE)
                    isEdit = it.getBoolean(BundleConstant.EXTRA_TYPE)
                    loadContent()
                }
            }
        } else {
            sendToView { it.onSetText(downloadedContent) }
        }
    }

    private fun loadContent() {
        contentUrl?.let {
            makeRestCall(RestProvider.getRepoService(isEnterprise)
                    .getFileAsStream(it), { sendToView({ v -> v.onSetText(it) }) })
        }
    }
}