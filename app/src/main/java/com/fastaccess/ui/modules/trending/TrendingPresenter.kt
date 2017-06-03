package com.fastaccess.ui.modules.trending

import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.colors.ColorsProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable

/**
 * Created by Kosh on 30 May 2017, 10:55 PM
 */

class TrendingPresenter : BasePresenter<TrendingMvp.View>(), TrendingMvp.Presenter {
    override fun onLoadLanguage() {
        manageObservable(RxHelper.getObserver(Observable.fromIterable(ColorsProvider.languages()))
                .doOnNext({ t: String -> sendToView({ it.onAppend(t) }) }))
    }
}
