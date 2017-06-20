package com.fastaccess.ui.modules.profile.events

import android.os.Parcelable
import android.support.v4.widget.SwipeRefreshLayout
import com.fastaccess.data.dao.GitCommitModel
import com.fastaccess.data.dao.SimpleUrlsModel
import com.fastaccess.data.dao.model.Event
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.widgets.dialog.ListDialogView
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Kosh on 03 Jun 2017, 4:49 PM
 */
interface ProfileEventsMvp {

    interface View : BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener,
            android.view.View.OnClickListener, ListDialogView.onSimpleItemSelection<Parcelable> {
        fun onNotifyAdapter(events: List<Event>?, page: Int)

        fun onOpenRepoChooser(models: ArrayList<SimpleUrlsModel>)

        fun getLoadMore(): OnLoadMore<String>

        fun onOpenCommitChooser(commits: List<GitCommitModel>)
    }

    interface Presenter : BaseMvp.FAPresenter,
            BaseViewHolder.OnItemClickListener<Event>,
            BaseMvp.PaginationListener<String> {
        fun getEvents(): ArrayList<Event>

        fun onWorkOffline()
    }
}