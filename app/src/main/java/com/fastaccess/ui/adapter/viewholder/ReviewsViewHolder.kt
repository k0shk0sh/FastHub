package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.TimelineModel
import com.fastaccess.helper.ParseDateFormat
import com.fastaccess.provider.timeline.HtmlHelper
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.ForegroundImageView
import com.fastaccess.ui.widgets.SpannableBuilder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Kosh on 13 Dec 2016, 1:42 AM
 */

class ReviewsViewHolder private constructor(itemView: View,
                                            adapter: BaseRecyclerAdapter<*, *, *>?,
                                            val viewGroup: ViewGroup)
    : BaseViewHolder<TimelineModel>(itemView, adapter) {

    @BindView(R.id.stateImage) lateinit var stateImage: ForegroundImageView
    @BindView(R.id.avatarLayout) lateinit var avatarLayout: AvatarLayout
    @BindView(R.id.stateText) lateinit var stateText: FontTextView
    @BindView(R.id.body) lateinit var body: FontTextView

    init {
        itemView.setOnLongClickListener(null)
        itemView.setOnClickListener(null)
    }

    override fun bind(model: TimelineModel) {
        val review = model.review
        review?.let {
            stateImage.setImageResource(R.drawable.ic_eye)
            avatarLayout.setUrl(it.user?.avatarUrl, it.user?.login, false, false)
            stateText.text = SpannableBuilder.builder().bold(if (it.user != null) {
                it.user.login
            } else {
                ""
            }).append(" ${review.state.replace("_", " ")} ").append(ParseDateFormat.getTimeAgo(it.submittedAt))
            if (!it.bodyHtml.isNullOrBlank()) {
                HtmlHelper.htmlIntoTextView(body, it.bodyHtml, viewGroup.width)
                body.visibility = View.VISIBLE
            } else {
                body.text = ""
                body.visibility = View.GONE
            }
        }
    }

    companion object {
        fun newInstance(viewGroup: ViewGroup, adapter: BaseRecyclerAdapter<*, *, *>): ReviewsViewHolder {
            return ReviewsViewHolder(BaseViewHolder.getView(viewGroup, R.layout.review_timeline_row_item), adapter, viewGroup)
        }
    }

}