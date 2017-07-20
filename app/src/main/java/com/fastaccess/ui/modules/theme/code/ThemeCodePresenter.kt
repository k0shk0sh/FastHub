package com.fastaccess.ui.modules.theme.code

import com.fastaccess.helper.RxHelper
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.prettifier.pretty.helper.CodeThemesHelper
import io.reactivex.Observable

/**
 * Created by Kosh on 22 Jun 2017, 11:52 PM
 */

class ThemeCodePresenter : BasePresenter<ThemeCodeMvp.View>(), ThemeCodeMvp.Presenter {

    override fun onLoadThemes() {
        manageDisposable(RxHelper.getObservable(Observable.just(CodeThemesHelper.listThemes()))
                .subscribe({ list -> sendToView { it.onInitAdapter(list) } }, { onError(it) }))
    }

}