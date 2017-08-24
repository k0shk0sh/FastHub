package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.timeline.PullRequestReviewModel
import com.fastaccess.data.dao.timeline.PullRequestTimelineModel
import com.fastaccess.data.dao.types.ReviewStateType
import com.fastaccess.helper.Logger
import com.fastaccess.helper.ParseDateFormat
import com.fastaccess.provider.scheme.LinkParserHelper
import com.fastaccess.provider.timeline.HtmlHelper
import com.fastaccess.provider.timeline.handler.drawable.DrawableGetter
import com.fastaccess.ui.adapter.ReviewCommentsAdapter
import com.fastaccess.ui.adapter.callback.OnToggleView
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.ForegroundImageView
import com.fastaccess.ui.widgets.SpannableBuilder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import pr.PullRequestTimelineQuery
import pr.type.PullRequestReviewState

/**
 * Created by Kosh on 13 Dec 2016, 1:42 AM
 */

class ReviewsViewHolder private constructor(itemView: View, adapter: BaseRecyclerAdapter<*, *, *>?, private val viewGroup: ViewGroup,
                                            private val onToggleView: OnToggleView) :
        BaseViewHolder<PullRequestTimelineModel>(itemView, adapter), BaseViewHolder.OnItemClickListener<PullRequestReviewModel> {

    @BindView(R.id.stateImage) lateinit var stateImage: ForegroundImageView
    @BindView(R.id.avatarLayout) lateinit var avatarLayout: AvatarLayout
    @BindView(R.id.stateText) lateinit var stateText: FontTextView
    @BindView(R.id.body) lateinit var body: FontTextView
    @BindView(R.id.line) lateinit var line: View
    @BindView(R.id.recycler) lateinit var recyclerView: DynamicRecyclerView

    init {
        itemView.setOnLongClickListener(null)
        itemView.setOnClickListener(null)
    }

    override fun bind(model: PullRequestTimelineModel) {
        if (model.reviewModel != null) {
            model.reviewModel.let {
                when {
                    it.reviewDismissedEvent != null -> initPrDismissedReview(it.reviewDismissedEvent!!)
                    it.reviewRequestedEvent != null -> initPrRequestedReview(it.reviewRequestedEvent!!)
                    it.reviewRequestRemovedEvent != null -> initPrRemovedReview(it.reviewRequestRemovedEvent!!)
                    it.id != null -> initPrReview(it)
                    else -> reset()
                }
            }
        } else {
            reset()
        }
    }

    private fun reset() {
        avatarLayout.setUrl(null, null, false, false)
        stateText.text = null
        body.text = null
        line.visibility = View.GONE
        recyclerView.visibility = View.GONE
        recyclerView.adapter = null
    }

    private fun initPrRemovedReview(event: PullRequestTimelineQuery.AsReviewRequestRemovedEvent) {
        event.actor()?.let {
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
            stateImage.setImageResource(ReviewStateType.DISMISSED.drawableRes)
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("removed")
                    .append(" ")
                    .bold(event.subject().login())
                    .append(" ")
                    .append("review")
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo(event.createdAt().toString()))
        }
        body.visibility = View.GONE
    }

    private fun initPrRequestedReview(event: PullRequestTimelineQuery.AsReviewRequestedEvent) {
        event.actor()?.let {
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
            stateImage.setImageResource(ReviewStateType.REQUEST_CHANGES.drawableRes)
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append(stateText.resources.getString(R.string.reviewed))
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo(event.createdAt().toString()))
        }
        body.visibility = View.GONE
    }

    private fun initPrDismissedReview(event: PullRequestTimelineQuery.AsReviewDismissedEvent) {
        event.review()?.let {
            avatarLayout.setUrl(it.author()?.avatarUrl().toString(), it.author()?.login(), false,
                    LinkParserHelper.isEnterprise(it.author()?.url().toString()))
            stateImage.setImageResource(ReviewStateType.DISMISSED.drawableRes)
            stateText.text = SpannableBuilder.builder()
                    .bold(it.author()?.login())
                    .append(" ")
                    .append(stateText.resources.getString(R.string.dismissed_review))
                    .append(" ")
                    .bold(when {
                        event.previousReviewState() == PullRequestReviewState.APPROVED -> "Approval"
                        event.previousReviewState() == PullRequestReviewState.CHANGES_REQUESTED -> "Requested Changes"
                        event.previousReviewState() == PullRequestReviewState.COMMENTED -> "Comment"
                        event.previousReviewState() == PullRequestReviewState.DISMISSED -> "Dismissal"
                        else -> "Pending Review"
                    })
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo(event.createdAt().toString()))
        }
        if (!event.messageHtml().toString().isBlank()) {
            HtmlHelper.htmlIntoTextView(body, event.messageHtml().toString(), viewGroup.width)
            body.visibility = View.VISIBLE
        } else {
            body.visibility = View.GONE
        }
    }

    private fun initPrReview(event: PullRequestReviewModel) {
        event.author?.let {
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false,
                    LinkParserHelper.isEnterprise(it.url().toString()))
            stateImage.setImageResource(ReviewStateType.REQUEST_CHANGES.drawableRes)
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append(stateText.resources.getString(R.string.reviewed))
                    .append(" ")
                    .append(" ")
                    .append(event.createdAt)
        }
        if (event.bodyHTML.isNullOrBlank()) {
            body.visibility = View.GONE
        } else {
            HtmlHelper.htmlIntoTextView(body, event.bodyHTML, viewGroup.width)
            body.visibility = View.VISIBLE
        }
        Logger.e(event.comments?.size)
        if (event.comments != null && !event.comments.isEmpty()) {
            line.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            recyclerView.adapter = ReviewCommentsAdapter(event.comments, this, onToggleView)
        } else {
            line.visibility = View.GONE
            recyclerView.visibility = View.GONE
            recyclerView.adapter = null
        }
        //TODO add comments
    }

    override fun onViewIsDetaching() {
        val drawableGetter = stateText.getTag(R.id.drawable_callback) as DrawableGetter?
        drawableGetter?.clear(drawableGetter)
    }

    override fun onItemClick(position: Int, v: View?, item: PullRequestReviewModel?) {

    }

    override fun onItemLongClick(position: Int, v: View?, item: PullRequestReviewModel?) {

    }

    companion object {
        fun newInstance(viewGroup: ViewGroup, adapter: BaseRecyclerAdapter<*, *, *>, onToggleView: OnToggleView): ReviewsViewHolder {
            return ReviewsViewHolder(BaseViewHolder.getView(viewGroup, R.layout.review_timeline_row_item), adapter, viewGroup, onToggleView)
        }
    }

}
