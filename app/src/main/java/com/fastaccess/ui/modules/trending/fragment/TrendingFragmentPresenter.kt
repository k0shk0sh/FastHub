package com.fastaccess.ui.modules.trending.fragment

import android.view.View
import com.fastaccess.data.dao.TrendingResponse
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.jsoup.RetroJsoupProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.RepoPagerActivity

/**
 * Created by Kosh on 30 May 2017, 11:04 PM
 */

class TrendingFragmentPresenter : BasePresenter<TrendingFragmentMvp.View>(), TrendingFragmentMvp.Presenter {

    private val trendingList: ArrayList<TrendingResponse> = ArrayList()

    override fun getTendingList(): ArrayList<TrendingResponse> {
        return trendingList
    }

    override fun onItemLongClick(position: Int, v: View?, item: TrendingResponse?) {}

    override fun onItemClick(position: Int, v: View?, item: TrendingResponse?) {
        val split = item?.title?.trim()?.split("/")!!
        v?.context!!.startActivity(RepoPagerActivity.createIntent(v.context!!, split[1].trim(), split[0].trim()))
    }

    override fun onCallApi(lang: String, since: String) {
        manageViewDisposable(RxHelper.getObserver(RetroJsoupProvider.getTrendingService(since, lang).trending)
                .doOnSubscribe { sendToView { it.showProgress(0) } }
                .subscribe({ response -> sendToView { view -> view.onNotifyAdapter(response) } },
                        { throwable -> onError(throwable) }, { sendToView({ it.hideProgress() }) }))
    }


}