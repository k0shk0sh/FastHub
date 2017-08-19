package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.timeline.PullRequestTimelineModel
import com.fastaccess.data.dao.types.ReviewStateType
import com.fastaccess.helper.ParseDateFormat
import com.fastaccess.provider.scheme.LinkParserHelper
import com.fastaccess.provider.timeline.HtmlHelper
import com.fastaccess.provider.timeline.handler.drawable.DrawableGetter
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.ForegroundImageView
import com.fastaccess.ui.widgets.SpannableBuilder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import pr.PullRequestTimelineQuery
import pr.type.PullRequestReviewState

/**
 * Created by Kosh on 13 Dec 2016, 1:42 AM
 */

class ReviewsViewHolder private constructor(itemView: View, adapter: BaseRecyclerAdapter<*, *, *>?, private val viewGroup: ViewGroup) :
        BaseViewHolder<PullRequestTimelineModel>(itemView, adapter) {

    @BindView(R.id.stateImage) lateinit var stateImage: ForegroundImageView
    @BindView(R.id.avatarLayout) lateinit var avatarLayout: AvatarLayout
    @BindView(R.id.stateText) lateinit var stateText: FontTextView
    @BindView(R.id.body) lateinit var body: FontTextView

    init {
        itemView.setOnLongClickListener(null)
        itemView.setOnClickListener(null)
    }

    override fun bind(model: PullRequestTimelineModel) {
        if (model.node != null) {
            model.node.let {
                when {
                    it.asPullRequestReview() != null -> initPrReview(it.asPullRequestReview()!!)
                    it.asReviewDismissedEvent() != null -> initPrDismissedReview(it.asReviewDismissedEvent()!!)
                    it.asReviewRequestedEvent() != null -> initPrRequestedReview(it.asReviewRequestedEvent()!!)
                    it.asReviewRequestRemovedEvent() != null -> initPrRemovedReview(it.asReviewRequestRemovedEvent()!!)
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
                    .bold(if (event.previousReviewState() == PullRequestReviewState.APPROVED) {
                        "Approval"
                    } else if (event.previousReviewState() == PullRequestReviewState.CHANGES_REQUESTED) {
                        "Requested Changes"
                    } else if (event.previousReviewState() == PullRequestReviewState.COMMENTED) {
                        "Comment"
                    } else if (event.previousReviewState() == PullRequestReviewState.DISMISSED) {
                        "Dismissal"
                    } else {
                        "Pending Review"
                    })
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo(event.createdAt().toString()))
        }
        if (!event.messageHtml().toString().isNullOrBlank()) {
            HtmlHelper.htmlIntoTextView(body, event.messageHtml().toString(), viewGroup.width);
            body.visibility = View.VISIBLE
        } else {
            body.visibility = View.GONE
        }
    }

    private fun initPrReview(event: PullRequestTimelineQuery.AsPullRequestReview) {
        event.author()?.let {
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false,
                    LinkParserHelper.isEnterprise(it.url().toString()))
            stateImage.setImageResource(ReviewStateType.REQUEST_CHANGES.drawableRes)
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append(stateText.resources.getString(R.string.reviewed))
                    .append(" ")
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo(event.createdAt().toString()))
        }
        if (!event.bodyHTML().toString().isNullOrBlank()) {
            HtmlHelper.htmlIntoTextView(body, event.bodyHTML().toString(), viewGroup.width);
            body.visibility = View.VISIBLE
        } else {
            body.visibility = View.GONE
        }
        //TODO add comments
    }

    override fun onViewIsDetaching() {
        val drawableGetter = stateText.getTag(R.id.drawable_callback) as DrawableGetter?
        drawableGetter?.clear(drawableGetter)
    }

    companion object {
        fun newInstance(viewGroup: ViewGroup, adapter: BaseRecyclerAdapter<*, *, *>): ReviewsViewHolder {
            return ReviewsViewHolder(BaseViewHolder.getView(viewGroup, R.layout.review_timeline_row_item), adapter, viewGroup)
        }
    }

}
