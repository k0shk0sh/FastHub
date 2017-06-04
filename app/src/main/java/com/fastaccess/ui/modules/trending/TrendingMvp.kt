package com.fastaccess.ui.modules.trending

import com.fastaccess.ui.base.mvp.BaseMvp

/**
 * Created by Kosh on 30 May 2017, 10:51 PM
 */

interface TrendingMvp {
    interface View : BaseMvp.FAView{
        fun onAppend(title: String)
    }

    interface Presenter {
        fun onLoadLanguage()
    }
}
