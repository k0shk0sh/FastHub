package com.fastaccess.ui.modules.theme.code

import com.fastaccess.ui.base.mvp.BaseMvp
import com.prettifier.pretty.PrettifyWebView

/**
 * Created by Kosh on 22 Jun 2017, 11:50 PM
 */
interface ThemeCodeMvp {

    interface View : BaseMvp.FAView, PrettifyWebView.OnContentChangedListener {
        fun onInitAdapter(list: List<String>)
    }

    interface Presenter {
        fun onLoadThemes()
    }
}