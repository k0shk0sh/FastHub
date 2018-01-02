package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.fullscreen

import android.content.Intent
import com.fastaccess.data.dao.CommitFileChanges
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.RxHelper
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable

/**
 * Created by Hashemsergani on 24.09.17.
 */
class FullScreenFileChangePresenter : BasePresenter<FullScreenFileChangeMvp.View>(), FullScreenFileChangeMvp.Presenter {

    var model: CommitFileChanges? = null
    var position: Int = -1
    var isCommit: Boolean = false

    override fun onLoad(intent: Intent) {
        intent.extras?.let {
            position = it.getInt(BundleConstant.ITEM)
            model = it.getParcelable(BundleConstant.EXTRA)
            isCommit = it.getBoolean(BundleConstant.YES_NO_EXTRA)
        }
        model?.let {
            manageDisposable(RxHelper.getObservable(Observable.fromIterable(it.linesModel))
                    .doOnSubscribe({ sendToView { it.showProgress(0) } })
                    .flatMap { Observable.just(it) }
                    .subscribe
                    ({
                        sendToView { v -> v.onNotifyAdapter(it) }
                    }, {
                        onError(it)
                    }, {
                        sendToView { it.hideProgress() }
                    }))
        }
    }
}