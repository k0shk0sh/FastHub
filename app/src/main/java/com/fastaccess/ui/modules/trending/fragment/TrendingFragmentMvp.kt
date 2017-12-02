package com.fastaccess.ui.modules.trending.fragment


import com.fastaccess.data.dao.TrendingModel
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Kosh on 30 May 2017, 11:03 PM
 */

interface TrendingFragmentMvp {
    interface View : BaseMvp.FAView {
        fun onNotifyAdapter(items: TrendingModel)
        fun onSetQuery(lang: String, since: String)
        fun clearAdapter()
    }

    interface Presenter : BaseViewHolder.OnItemClickListener<TrendingModel> {
        fun onCallApi(lang: String, since: String)
        fun getTendingList(): ArrayList<TrendingModel>
    }
}
