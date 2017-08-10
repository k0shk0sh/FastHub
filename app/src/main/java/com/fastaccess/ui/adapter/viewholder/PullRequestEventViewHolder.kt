package com.fastaccess.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.text.style.BackgroundColorSpan
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.timeline.PullRequestTimelineModel
import com.fastaccess.helper.ParseDateFormat
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.ViewHelper
import com.fastaccess.provider.scheme.LinkParserHelper
import com.fastaccess.provider.timeline.HtmlHelper
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.ForegroundImageView
import com.fastaccess.ui.widgets.SpannableBuilder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import com.zzhoujay.markdown.style.CodeSpan
import pr.PullRequestTimelineQuery
import pr.type.StatusState

/**
 * Created by kosh on 03/08/2017.
 */

class PullRequestEventViewHolder private constructor(view: View, adapter: BaseRecyclerAdapter<*, *, *>) :
        BaseViewHolder<PullRequestTimelineModel>(view, adapter) {

    @BindView(R.id.stateImage) lateinit var stateImage: ForegroundImageView
    @BindView(R.id.avatarLayout) lateinit var avatarLayout: AvatarLayout
    @BindView(R.id.stateText) lateinit var stateText: FontTextView
    @BindView(R.id.commitStatus) lateinit var commitStatus: ForegroundImageView

    override fun bind(t: PullRequestTimelineModel) {
        val node = t.node
        avatarLayout.setUrl(null, null, false, false)
        stateText.text = null
        node?.let {
            it.asAssignedEvent()?.let { assignedEvent(it) }
            it.asBaseRefForcePushedEvent()?.let { forcePushEvent(it) }
            it.asClosedEvent()?.let { closedEvent(it) }
            it.asCommit()?.let { commitEvent(it) }
            it.asDemilestonedEvent()?.let { demilestonedEvent(it) }
            it.asDeployedEvent()?.let { deployedEvent(it) }
            it.asHeadRefDeletedEvent()?.let { refDeletedEvent(it) }
            it.asHeadRefForcePushedEvent()?.let { refForPushedEvent(it) }
            it.asHeadRefRestoredEvent()?.let { headRefRestoredEvent(it) }
            it.asLabeledEvent()?.let { labeledEvent(it) }
            it.asLockedEvent()?.let { lockEvent(it) }
            it.asMergedEvent()?.let { mergedEvent(it) }
            it.asMilestonedEvent()?.let { milestoneEvent(it) }
            it.asReferencedEvent()?.let { referenceEvent(it) }
            it.asRenamedTitleEvent()?.let { renamedEvent(it) }
            it.asReopenedEvent()?.let { reopenedEvent(it) }
            it.asUnassignedEvent()?.let { unassignedEvent(it) }
            it.asUnlabeledEvent()?.let { unlabeledEvent(it) }
            it.asUnlockedEvent()?.let { unlockedEvent(it) }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun unlockedEvent(event: PullRequestTimelineQuery.AsUnlockedEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("unlocked this conversation")
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_lock)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun unlabeledEvent(event: PullRequestTimelineQuery.AsUnlabeledEvent) {
        event.actor()?.let {
            val color = Color.parseColor("#" + event.label().color())
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("removed")
                    .append(" ")
                    .append(event.label().name(), CodeSpan(color, ViewHelper.generateTextColor(color), 5.0f))
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_label)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun unassignedEvent(event: PullRequestTimelineQuery.AsUnassignedEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("unassigned") //TODO add "removed their assignment" for self
                    .append(" ")
                    .append(event.user()?.login())
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_profile)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun reopenedEvent(event: PullRequestTimelineQuery.AsReopenedEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("reopened this")
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_issue_opened)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun renamedEvent(event: PullRequestTimelineQuery.AsRenamedTitleEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("changed the title from").append(" ").append(event.previousTitle())
                    .append(" ").append("to").append(" ").bold(event.currentTitle())
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_edit)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun referenceEvent(event: PullRequestTimelineQuery.AsReferencedEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append(if (event.isCrossRepository) "cross referenced this" else "referenced this")
                    .append(" ")
                    .append("from").append(" ")
                    .url(if (event.commit() != null) {
                        substring(event.commit()?.oid()?.toString())
                    } else if (event.subject().asIssue() != null) {
                        if (event.isCrossRepository) {
                            "${event.commitRepository().nameWithOwner()} ${event.subject().asIssue()?.title()}#${event.subject().asIssue()?.number()}"
                        } else {
                            "${event.subject().asIssue()?.title()}#${event.subject().asIssue()?.number()}"
                        }
                    } else if (event.subject().asPullRequest() != null) {
                        if (event.isCrossRepository) {
                            "${event.commitRepository().nameWithOwner()} ${event.subject().asPullRequest()?.title()}" +
                                    "#${event.subject().asPullRequest()?.number()}"
                        } else {
                            "${event.subject().asPullRequest()?.title()}#${event.subject().asPullRequest()?.number()}"
                        }
                    } else {
                        event.commitRepository().nameWithOwner()
                    })
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_push)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun milestoneEvent(event: PullRequestTimelineQuery.AsMilestonedEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("added this to the")
                    .append(" ")
                    .append(event.milestoneTitle()).append(" ").append("milestone")
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_milestone)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun mergedEvent(event: PullRequestTimelineQuery.AsMergedEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("merged commit")
                    .append(" ")
                    .url(substring(event.commit()?.oid()?.toString()))
                    .append(" ")
                    .append("into")
                    .append(" ")
                    .append(event.actor())
                    .append(":")
                    .append(event.mergeRefName(), BackgroundColorSpan(HtmlHelper.getWindowBackground(PrefGetter.getThemeType())))
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_merge)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun lockEvent(event: PullRequestTimelineQuery.AsLockedEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("locked and limited conversation to collaborators")
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_lock)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun labeledEvent(event: PullRequestTimelineQuery.AsLabeledEvent) {
        event.actor()?.let {
            val color = Color.parseColor("#" + event.label().color())
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("labeled")
                    .append(" ")
                    .append(event.label().name(), CodeSpan(color, ViewHelper.generateTextColor(color), 5.0f))
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_label)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun headRefRestoredEvent(event: PullRequestTimelineQuery.AsHeadRefRestoredEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("restored the")
                    .append(" ")
                    .append(it.login())
                    .append(":")
                    .append(event.pullRequest().headRefName(), BackgroundColorSpan(HtmlHelper.getWindowBackground(PrefGetter.getThemeType())))
                    .append(" ")
                    .append("branch")
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_push)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun refForPushedEvent(event: PullRequestTimelineQuery.AsHeadRefForcePushedEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("reference force pushed to", BackgroundColorSpan(HtmlHelper.getWindowBackground(PrefGetter.getThemeType())))
                    .append(" ")
                    .url(substring(event.afterCommit().oid().toString()))
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_push)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun refDeletedEvent(event: PullRequestTimelineQuery.AsHeadRefDeletedEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("deleted the")
                    .append(" ")
                    .append(it.login())
                    .append(":")
                    .append(substring(event.headRefName()), BackgroundColorSpan(HtmlHelper.getWindowBackground(PrefGetter.getThemeType())))
                    .append(" ")
                    .append("branch")
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_trash)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun deployedEvent(event: PullRequestTimelineQuery.AsDeployedEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("made a deployment", BackgroundColorSpan(HtmlHelper.getWindowBackground(PrefGetter.getThemeType())))
                    .append(" ")
                    .append(event.deployment().latestStatus()?.state()?.name)
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_push)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun demilestonedEvent(event: PullRequestTimelineQuery.AsDemilestonedEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("removed this from the")
                    .append(" ")
                    .append(event.milestoneTitle()).append(" ").append("milestone")
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_milestone)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun commitEvent(event: PullRequestTimelineQuery.AsCommit) {
        event.author()?.let {
            stateText.text = SpannableBuilder.builder()//Review[k0shk0sh] We may want to suppress more then 3 or 4 commits. since it will clog the it
                    .bold(if (it.user() == null) it.name() else it.user()?.login())
                    .append(" ")
                    .append("committed")
                    .append(" ")
                    .append(event.messageHeadline())
                    .append(" ")
                    .url(substring(event.oid().toString()))
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.committedDate().toString())))
            stateImage.setImageResource(R.drawable.ic_push)
            avatarLayout.setUrl(it.user()?.avatarUrl().toString(), it.user()?.login(), false,
                    LinkParserHelper.isEnterprise(it.user()?.url().toString()))
            event.status()?.let {
                commitStatus.visibility = View.VISIBLE
                val context = commitStatus.context
                commitStatus.tintDrawableColor(when (it.state()) {
                    StatusState.ERROR -> ContextCompat.getColor(context, R.color.material_red_700)
                    StatusState.FAILURE -> ContextCompat.getColor(context, R.color.material_deep_orange_700)
                    StatusState.SUCCESS -> ContextCompat.getColor(context, R.color.material_green_700)
                    else -> ContextCompat.getColor(context, R.color.material_yellow_700)
                })
            }
        }
    }

    private fun closedEvent(event: PullRequestTimelineQuery.AsClosedEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("closed this in")
                    .append(" ")
                    .url(substring(event.commit()?.oid()?.toString()))
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_merge)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun forcePushEvent(event: PullRequestTimelineQuery.AsBaseRefForcePushedEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("force pushed to", BackgroundColorSpan(HtmlHelper.getWindowBackground(PrefGetter.getThemeType())))
                    .append(" ")
                    .url(substring(event.afterCommit().oid().toString()))
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_push)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun assignedEvent(event: PullRequestTimelineQuery.AsAssignedEvent) {
        event.actor()?.let {
            stateText.text = SpannableBuilder.builder()
                    .bold(it.login())
                    .append(" ")
                    .append("assigned") //TODO add "self-assigned" for self
                    .append(" ")
                    .append(event.user()?.login())
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.createdAt().toString())))
            stateImage.setImageResource(R.drawable.ic_profile)
            avatarLayout.setUrl(it.avatarUrl().toString(), it.login(), false, LinkParserHelper.isEnterprise(it.url().toString()))
        }
    }

    private fun substring(value: String?): String {
        if (value == null) {
            return ""
        }
        if (value.length <= 7) return value
        else return value.substring(0, 7)
    }

    companion object {
        fun newInstance(parent: ViewGroup, adapter: BaseRecyclerAdapter<*, *, *>): PullRequestEventViewHolder {
            return PullRequestEventViewHolder(getView(parent, R.layout.issue_timeline_row_item), adapter)
        }
    }
}