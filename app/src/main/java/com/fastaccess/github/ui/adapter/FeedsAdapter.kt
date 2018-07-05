package com.fastaccess.github.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.domain.response.enums.EventsType
import com.fastaccess.github.R
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.ui.widget.SpannableBuilder
import com.fastaccess.github.utils.extensions.replaceAllNewLines
import com.fastaccess.github.utils.extensions.timeAgo
import kotlinx.android.synthetic.main.feeds_main_screen_row_item.view.*

/**
 * Created by Kosh on 26.06.18.
 */
class FeedsAdapter : ListAdapter<FeedModel, FeedsAdapter.ViewHolder>(object : DiffUtil.ItemCallback<FeedModel?>() {
    override fun areItemsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean = oldItem.id == newItem.id
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class ViewHolder(parent: ViewGroup) : BaseViewHolder<FeedModel>(LayoutInflater.from(parent.context)
            .inflate(R.layout.feeds_main_screen_row_item, parent, false)) {

        override fun bind(item: FeedModel) {
            itemView.apply {
                feedDescription.text = ""
                feedTitle.text = context.getString(item.type?.titleId ?: 0).toLowerCase()
                when (item.type) {
                    EventsType.WatchEvent -> watchEvent(this, item)
                    EventsType.CreateEvent -> createEvent(this, item)
                    EventsType.CommitCommentEvent -> commitCommentEvent(this, item)
                    EventsType.DownloadEvent -> downloadEvent(this, item)
                    EventsType.FollowEvent -> followEvent(this, item)
                    EventsType.ForkEvent -> forkEvent(this, item)
                    EventsType.GistEvent -> gistEvent(this, item)
                    EventsType.GollumEvent -> gollumEvent(this, item)
                    EventsType.IssueCommentEvent -> issueCommentEvent(this, item)
                    EventsType.IssuesEvent -> issueEvent(this, item)
                    EventsType.MemberEvent -> memberEvent(this, item)
                    EventsType.PublicEvent -> publicEvent(this, item)
                    EventsType.PullRequestEvent -> pullRequestEvent(this, item)
                    EventsType.PullRequestReviewCommentEvent -> pullRequestReviewCommentEvent(this, item)
                    EventsType.PullRequestReviewEvent -> pullRequestReviewEvent(this, item)
                    EventsType.RepositoryEvent -> repositoryEvent(this, item)
                    EventsType.PushEvent -> pushEvent(this, item)
                    EventsType.TeamAddEvent -> teamAddedEvent(this, item)
                    EventsType.DeleteEvent -> deleteEvent(this, item)
                    EventsType.ReleaseEvent -> releaseEvent(this, item)
                    EventsType.ForkApplyEvent -> forkApplyEvent(this, item)
                    EventsType.OrgBlockEvent -> orgBlockEvent(this, item)
                    EventsType.ProjectCardEvent -> projectCardEvent(this, item)
                    EventsType.ProjectColumnEvent -> projectColumnEvent(this, item)
                    EventsType.OrganizationEvent -> organizationEvent(this, item)
                    EventsType.ProjectEvent -> projectEvent(this, item)
                    else -> otherEvent(this, item)
                }
                dateWithIcon.text = item.createdAt?.timeAgo()
                dateWithIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(0, item.type?.drawableRes ?: 0, 0, 0)
            }
        }

        private fun otherEvent(view: View, item: FeedModel) {

        }

        private fun projectEvent(view: View, item: FeedModel) {

        }

        private fun organizationEvent(view: View, item: FeedModel) {

        }

        private fun projectColumnEvent(view: View, item: FeedModel) {

        }

        private fun projectCardEvent(view: View, item: FeedModel) {

        }

        private fun orgBlockEvent(view: View, item: FeedModel) {

        }

        private fun forkApplyEvent(view: View, item: FeedModel) {

        }

        private fun releaseEvent(view: View, item: FeedModel) {

        }

        private fun deleteEvent(view: View, item: FeedModel) {

        }

        private fun teamAddedEvent(view: View, item: FeedModel) {

        }

        private fun pushEvent(view: View, item: FeedModel) {

        }

        private fun repositoryEvent(view: View, item: FeedModel) {

        }

        private fun pullRequestReviewEvent(view: View, item: FeedModel) = pullRequestReviewCommentEvent(view, item)

        private fun pullRequestReviewCommentEvent(view: View, item: FeedModel) {
            view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
                    .space()
                    .bold(text = "reviewed a pull request in")
                    .space()
                    .append(text = "${item.repo?.name}")
                    .bold(text = "#${item.payload?.issue?.number?.toString()}")
        }

        private fun pullRequestEvent(view: View, item: FeedModel) {
            val pullRequest = item.payload?.pullRequest
            val action = if ("synchronize" == item.payload?.action) {
                "updated"
            } else if (pullRequest?.isMerged == true) {
                "merged"
            } else {
                item.payload?.action
            }
            view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
                    .space()
                    .bold(text = action ?: "")
                    .space()
                    .append(text = "${item.repo?.name}")
                    .bold(text = "#${pullRequest?.number}")
            if ("opened" == action || "closed" == action) {
                view.feedDescription.text = pullRequest?.title?.replaceAllNewLines() ?: ""
            }
        }

        private fun publicEvent(view: View, item: FeedModel) {

        }

        private fun memberEvent(view: View, item: FeedModel) {

        }

        private fun issueEvent(view: View, item: FeedModel) {

        }

        private fun issueCommentEvent(view: View, item: FeedModel) {
            view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
                    .space()
                    .bold(text = "commented on issue")
                    .space()
                    .append(text = "${item.repo?.name}")
                    .bold(text = "#${item.payload?.issue?.number}")

            item.payload?.comment?.body?.let {
                view.feedDescription.text = it.replaceAllNewLines() ?: ""
            }
        }

        private fun gollumEvent(view: View, item: FeedModel) {

        }

        private fun gistEvent(view: View, item: FeedModel) {

        }

        private fun forkEvent(view: View, item: FeedModel) {

        }

        private fun followEvent(view: View, item: FeedModel) {

        }

        private fun downloadEvent(view: View, item: FeedModel) {

        }

        private fun commitCommentEvent(view: View, item: FeedModel) {
            view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
                    .space()
                    .bold(text = "commented on commit")
                    .space()
                    .append(text = "${item.repo?.name}")
                    .bold(text = "#${item.payload?.issue?.number?.toString()}")
        }

        private fun watchEvent(view: View, item: FeedModel) {
            view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
                    .space()
                    .append(text = view.context.getString(item.type?.titleId ?: 0).toLowerCase())
                    .space()
                    .append(text = item.repo?.fullName)
        }

        private fun createEvent(view: View, item: FeedModel) {
            item.payload?.let {
                view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
                        .space()
                        .bold("created")
                        .space()
                        .append(it.refType)
                        .space()
                        .append(if ("repository" != it.refType) it.ref else "")
                        .space()
                        .append("at")
                        .space()
                        .append(item.repo?.name)
                view.feedDescription.text = it.description?.replaceAllNewLines() ?: ""
            }
        }
    }
}