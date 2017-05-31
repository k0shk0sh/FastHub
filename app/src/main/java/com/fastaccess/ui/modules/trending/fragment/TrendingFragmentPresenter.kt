package com.fastaccess.ui.modules.trending.fragment

import android.view.View
import com.fastaccess.data.dao.kot.TrendingResponse
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 30 May 2017, 11:04 PM
 */

class TrendingFragmentPresenter : BasePresenter<TrendingFragmentMvp.View>(), TrendingFragmentMvp.Presenter {
    override fun onItemLongClick(position: Int, v: View?, item: TrendingResponse?) {
        //TODO
    }

    override fun onItemClick(position: Int, v: View?, item: TrendingResponse?) {
        //TODO
    }

    override fun onCallApi(lang: String, since: String) {
        makeRestCall<List<TrendingResponse>>(RestProvider.getTrendingService().getTrending(lang, since)) { response ->
            sendToView { view -> view.onNotifyAdapter(response) }
        }
    }


}