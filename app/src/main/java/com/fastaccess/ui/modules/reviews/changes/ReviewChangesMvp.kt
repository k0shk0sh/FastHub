package com.fastaccess.ui.modules.reviews.changes

import com.fastaccess.data.dao.ReviewRequestModel
import com.fastaccess.ui.base.mvp.BaseMvp

/**
 * Created by Kosh on 25 Jun 2017, 1:15 AM
 */
interface ReviewChangesMvp {

    interface View : BaseMvp.FAView {
        fun onSuccessfullySubmitted()
        fun onErrorSubmitting()
    }

    interface Presenter {
        fun onSubmit(reviewRequest: ReviewRequestModel, repoId: String, owner: String, number: Long, comment: String, method: String)
    }
}