package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.fullscreen

import android.content.Intent
import com.fastaccess.data.dao.CommitLinesModel
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.modules.reviews.callback.ReviewCommentListener
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Hashemsergani on 24.09.17.
 */

interface FullScreenFileChangeMvp {
    interface View : BaseMvp.FAView, BaseViewHolder.OnItemClickListener<CommitLinesModel>, ReviewCommentListener {
        fun onNotifyAdapter(model: CommitLinesModel)
    }

    interface Presenter {
        fun onLoad(intent: Intent)
    }
}