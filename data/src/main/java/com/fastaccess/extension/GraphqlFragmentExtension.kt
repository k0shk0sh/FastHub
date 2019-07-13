package com.fastaccess.extension

import com.fastaccess.data.model.*
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.data.model.parcelable.MilestoneModel
import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import github.fragment.*

/**
 * Created by Kosh on 05.02.19.
 */


fun ShortActor.toUser(): ShortUserModel = ShortUserModel(login, login, url.toString(), avatarUrl = avatarUrl.toString())

fun ShortUserRowItem.toUser(): ShortUserModel = ShortUserModel(id, login, url.toString(), name, avatarUrl = avatarUrl.toString())

fun ShortPullRequestRowItem.toPullRequest(): MyIssuesPullsModel = MyIssuesPullsModel(id, databaseId, number,
    title, repository.nameWithOwner, comments.totalCount,
    state.rawValue(), url.toString())

fun ShortIssueRowItem.toIssue(): MyIssuesPullsModel = MyIssuesPullsModel(id, databaseId, number,
    title, repository.nameWithOwner, comments.totalCount, "", url.toString())

fun CommitFragment.toCommit(): CommitModel = CommitModel(id,
    ShortUserModel(author?.name, author?.name, avatarUrl = author?.avatarUrl?.toString()), message,
    abbreviatedOid, commitUrl.toString(), authoredDate, isCommittedViaWeb)

fun Reactions.toReactionGroup(): ReactionGroupModel = ReactionGroupModel(ReactionContent.getByValue(this.content.rawValue()),
    this.createdAt, CountModel(this.users.totalCount), this.isViewerHasReacted)

fun LoginModel.me(): ShortUserModel = ShortUserModel(login, login, url, name, avatarUrl = avatarUrl)

fun Labels?.toLabels(): LabelModel = LabelModel(this?.name, this?.color, this?.url.toString(), this?.isDefault)

fun FullIssue.Milestone.toMilestone(): MilestoneModel = this.fragments.milestoneFragment.let {
    MilestoneModel(it.id, it.title, it.description, it.state.rawValue(), it.url.toString(), it.number, it.isClosed, it.dueOn)
}