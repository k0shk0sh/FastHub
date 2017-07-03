package com.fastaccess.ui.modules.reviews.callback

import android.os.Bundle
import com.fastaccess.data.dao.CommitLinesModel

/**
 * Created by Kosh on 24 Jun 2017, 12:38 PM
 */
interface ReviewCommentListener {
    fun onCommentAdded(comment: String, item: CommitLinesModel, bundle: Bundle?)
}