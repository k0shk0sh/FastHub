package com.fastaccess.ui.modules.trending

import android.graphics.Color
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.colors.ColorsProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable

/**
 * Created by Kosh on 30 May 2017, 10:55 PM
 */

class TrendingPresenter : BasePresenter<TrendingMvp.View>(), TrendingMvp.Presenter {
    override fun onFilterLanguage(key: String) {
        manageObservable(RxHelper.getObservable(Observable.fromIterable(ColorsProvider.languages()))
                .doOnSubscribe { sendToView { it.onClearMenu() } }
                .filter { it.toLowerCase().contains(key.toLowerCase()) }
                .doOnNext { sendWithColor(it) })
    }

    private fun sendWithColor(t: String) {
        val color = ColorsProvider.getColor(t)
        if (color != null) {
            try {
                val lanColor = Color.parseColor(color.color)
                sendToView { it.onAppend(t, lanColor) }
            } catch (e: Exception) {
                e.printStackTrace()
                sendToView { it.onAppend(t, Color.LTGRAY) }
            }
        } else {
            sendToView { it.onAppend(t, Color.LTGRAY) }
        }
    }

    override fun onLoadLanguage() {
        manageObservable(RxHelper.getObservable(Observable.fromIterable(ColorsProvider.languages()))
                .doOnNext { sendWithColor(it) })
    }
}
