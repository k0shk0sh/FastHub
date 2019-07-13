package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.domain.response.enums.EventsType
import com.fastaccess.github.R
import com.fastaccess.github.extensions.replaceAllNewLines
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.markdown.MarkdownProvider
import com.fastaccess.markdown.widget.SpannableBuilder
import kotlinx.android.synthetic.main.feeds_row_item.view.*

/**
 * Created by Kosh on 20.10.18.
 */
class FeedsViewHolder(
    parent: ViewGroup
) : BaseViewHolder<FeedModel>(LayoutInflater.from(parent.context)
    .inflate(R.layout.feeds_row_item, parent, false)) {

    override fun bind(item: FeedModel) {
        itemView.apply {
            feedDescription.text = ""
            feedDescription.isVisible = false
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
            stateIcon.setImageResource(item.type?.drawableRes ?: 0)
            userIcon.loadAvatar(item.actor?.avatarUrl, item.actor?.url)
        }
    }

    private fun otherEvent(view: View, item: FeedModel) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold(item.payload?.action)
            .space()
            .append(item.repo?.name)
    }

    private fun organizationEvent(view: View, item: FeedModel) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold(item.payload?.action?.replace("_", ""))
            .space()
            .append(item.payload?.invitation?.login)
            .space()
            .append(item.payload?.organization?.login)
    }

    private fun projectEvent(view: View, item: FeedModel) = projectCardEvent(view, item, false)

    private fun projectColumnEvent(view: View, item: FeedModel) = projectCardEvent(view, item, true)

    private fun projectCardEvent(view: View, item: FeedModel, isColumn: Boolean = false) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold(item.payload?.action)
            .space()
            .append(if (isColumn) "column" else "project")
            .space()
            .append(item.payload?.organization?.login)
    }

    private fun orgBlockEvent(view: View, item: FeedModel) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold(item.payload?.action)
            .space()
            .append(item.payload?.blockedUser?.login)
            .space()
            .append(item.payload?.organization?.login)
    }

    private fun forkApplyEvent(view: View, item: FeedModel) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold(item.payload?.head)
            .space()
            .append(item.payload?.before)
            .space()
            .append(item.repo?.name)
    }

    private fun releaseEvent(view: View, item: FeedModel) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold("released")
            .space()
            .append(item.payload?.release?.name)
            .space()
            .append(item.repo?.name)

    }

    private fun deleteEvent(view: View, item: FeedModel) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold("deleted")
            .space()
            .append(item.payload?.refType)
            .space()
            .bold("at ")
            .append(item.repo?.name)
    }

    private fun teamAddedEvent(view: View, item: FeedModel) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold("added")
            .space()
            .append(item.payload?.user?.login ?: item.repo?.name)
            .space()
            .bold("in ")
            .append(item.payload?.team?.name ?: item.payload?.team?.slug)

    }

    private fun pushEvent(view: View, item: FeedModel) {
        val ref = if (item.payload?.ref?.startsWith("refs/heads/") == true) {
            item.payload?.ref?.substring(11)
        } else {
            item.payload?.ref
        }
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold("push to")
            .space()
            .append(ref)
            .space()
            .bold("at")
            .space()
            .append(item.repo?.name)

        val builder = SpannableBuilder.builder()
        val commits = item.payload?.commits
        if (commits?.isNotEmpty() == true) {
            if (commits.size != 1) {
                builder.append("${item.payload?.size}")
                    .bold(" new commits")
                    .newline()
            } else {
                builder.bold("1 new commit").newline()
            }
            commits.take(5)
                .filter { !it.sha.isNullOrEmpty() }
                .forEach {
                    val sha = if ((it.sha?.length ?: 0) > 7) {
                        it.sha?.subSequence(0, 7)
                    } else {
                        it.sha
                    }
                    builder.url(sha ?: "")
                        .space()
                        .append(it.message?.replaceAllNewLines())
                        .newline()
                }
            view.feedDescription.maxLines = 5
            view.feedDescription.text = builder
            view.feedDescription.isVisible = true
        } else {
            view.feedDescription.maxLines = 2
        }
    }

    private fun repositoryEvent(view: View, item: FeedModel) = publicEvent(view, item)

    private fun pullRequestReviewEvent(view: View, item: FeedModel) = pullRequestReviewCommentEvent(view, item)

    private fun pullRequestReviewCommentEvent(view: View, item: FeedModel) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold(if (item.payload?.comment != null) "commented on a review in " else "reviewed a pull request in")
            .space()
            .append("${item.repo?.name}")
            .bold("#${item.payload?.pullRequest?.number}")
        view.feedDescription.apply {
            text = item.payload?.comment?.body ?: ""
            isVisible = !item.payload?.comment?.body.isNullOrEmpty()
        }
    }

    private fun pullRequestEvent(view: View, item: FeedModel) {
        val pullRequest = item.payload?.pullRequest
        val action = when {
            "synchronize" == item.payload?.action -> "updated"
            pullRequest?.isMerged == true -> "merged"
            else -> item.payload?.action
        }

        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold(action ?: "")
            .space()
            .append("${item.repo?.name}")
            .bold("#${pullRequest?.number}")
        if ("opened" == action || "closed" == action) {
            view.feedDescription.text = pullRequest?.title?.replaceAllNewLines() ?: ""
            view.feedDescription.isVisible = !pullRequest?.title?.replaceAllNewLines().isNullOrEmpty()
        }
    }

    private fun publicEvent(view: View, item: FeedModel) {
        val action = if ("privatized" == item.payload?.action) {
            "private"
        } else {
            "public"
        }
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .append(item.repo?.name)
            .space()
            .bold(action)
    }

    private fun memberEvent(view: View, item: FeedModel) {
        val user = item.payload?.member
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold("added")
            .space()
            .append(user?.login)
            .space()
            .append("as a collaborator to ")
            .append(item.repo?.name)
    }

    private fun issueEvent(view: View, item: FeedModel) {
        val issue = item.payload?.issue
        val label = item.payload?.issue?.labels?.lastOrNull()
        val isLabel = "label" == item.payload?.action
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold(if (isLabel && label != null) "Labeled ${label.name}" else item.payload?.action ?: "")
            .space()
            .append(item.repo?.name)
            .space()
            .bold("#${issue?.number}")
        view.feedDescription.text = issue?.title?.replaceAllNewLines() ?: ""
        view.feedDescription.isVisible = !issue?.title?.replaceAllNewLines().isNullOrEmpty()
    }

    private fun issueCommentEvent(view: View, item: FeedModel) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold("commented on ${if (item.payload?.issue?.htmlUrl?.contains("/pull/", true) == true) "a pull request" else "an issue"}")
            .space()
            .append("${item.repo?.name}")
            .bold("#${item.payload?.issue?.number}")

        view.feedDescription.text = item.payload?.comment?.body?.replaceAllNewLines()?.let { MarkdownProvider.stripMd(it) }
        view.feedDescription.isVisible = !item.payload?.comment?.body?.replaceAllNewLines().isNullOrEmpty()
    }

    private fun gollumEvent(view: View, item: FeedModel) {
        val wikies = item.payload?.pages
        val builder = SpannableBuilder.builder().append(item.actor?.login)
            .space()
        if (wikies?.isNotEmpty() == true) {
            wikies.forEach {
                builder.bold(it.action)
                    .space()
                    .append(it.packageName)
                    .space()
            }
        } else {
            builder.bold(view.resources.getString(R.string.gollum))
                .space()
        }
        builder.append(item.repo?.name)
        view.feedTitle.text = builder
    }

    private fun gistEvent(view: View, item: FeedModel) {
        val action = if ("create" == item.payload?.action) "created" else if ("update" == item.payload?.action) "updated" else item.payload?.action

        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold("$action ${view.resources.getString(R.string.gist).toLowerCase()}")
            .space()
            .append(item.payload?.gist?.id)
    }

    private fun forkEvent(view: View, item: FeedModel) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold("forked")
            .space()
            .append(item.repo?.name)
    }

    private fun followEvent(view: View, item: FeedModel) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold("started following")
            .space()
            .append(item.payload?.target?.login)
    }

    private fun downloadEvent(view: View, item: FeedModel) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold("uploaded a file")
            .space()
            .append("${item.payload?.download?.name}")
            .space()
            .append("to")
            .space()
            .bold(item.repo?.name ?: "")
    }

    private fun commitCommentEvent(view: View, item: FeedModel) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold("commented on commit")
            .space()
            .append("${item.repo?.name}")
            .bold("#${item.payload?.issue?.number?.toString()}")
    }

    private fun watchEvent(view: View, item: FeedModel) {
        view.feedTitle.text = SpannableBuilder.builder().append(item.actor?.login)
            .space()
            .bold(view.context.getString(item.type?.titleId ?: 0).toLowerCase())
            .space()
            .append(item.repo?.name)
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
            view.feedDescription.text = it.description?.replaceAllNewLines()?.let { MarkdownProvider.stripMd(it) }
            view.feedDescription.isVisible = !it.description?.replaceAllNewLines().isNullOrEmpty()
        }
    }
}