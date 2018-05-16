package com.fastaccess.ui.modules.repos.wiki

import android.content.Intent
import com.fastaccess.data.dao.wiki.WikiContentModel
import com.fastaccess.data.dao.wiki.WikiSideBarModel
import com.fastaccess.ui.base.mvp.BaseMvp

/**
 * Created by Kosh on 13 Jun 2017, 8:11 PM
 */
interface WikiMvp {
    interface View : BaseMvp.FAView {
        fun onLoadContent(wiki: WikiContentModel)
        fun onSetPage(page: String)
    }

    interface Presenter {
        fun onActivityCreated(intent: Intent?)
        fun onSidebarClicked(sidebar: WikiSideBarModel)
    }
}