package com.fastaccess.domain.response.enums

import com.fastaccess.domain.R

enum class EventsType constructor(val titleId: Int, val drawableRes: Int) {
    WatchEvent(R.string.starred, R.drawable.ic_star_filled),
    CreateEvent(R.string.created_repo, R.drawable.ic_repo),
    CommitCommentEvent(R.string.commented_on_commit, R.drawable.ic_comment),
    DownloadEvent(R.string.downloaded, R.drawable.ic_download),
    FollowEvent(R.string.followed, R.drawable.ic_add),
    ForkEvent(R.string.forked, R.drawable.ic_fork),
    GistEvent(R.string.created_gist, R.drawable.ic_gists),
    GollumEvent(R.string.gollum, R.drawable.ic_info_outline),
    IssueCommentEvent(R.string.commented_on_issue, R.drawable.ic_comment),
    IssuesEvent(R.string.created_issue, R.drawable.ic_issues),
    MemberEvent(R.string.member, R.drawable.ic_add),
    PublicEvent(R.string.public_event, R.drawable.ic_repo),
    PullRequestEvent(R.string.pull_request, R.drawable.ic_pull_requests),
    PullRequestReviewCommentEvent(R.string.pr_comment_review, R.drawable.ic_comment),
    PullRequestReviewEvent(R.string.pr_review_event, R.drawable.ic_eye),
    RepositoryEvent(R.string.repo_event, R.drawable.ic_repo),
    PushEvent(R.string.pushed, R.drawable.ic_push),
    TeamAddEvent(R.string.team_event, R.drawable.ic_profile),
    DeleteEvent(R.string.deleted, R.drawable.ic_trash),
    ReleaseEvent(R.string.released, R.drawable.ic_download),
    ForkApplyEvent(R.string.forked, R.drawable.ic_fork),
    OrgBlockEvent(R.string.organization_event, R.drawable.ic_profile),
    ProjectCardEvent(R.string.card_event, R.drawable.ic_info_outline),
    ProjectColumnEvent(R.string.project_event, R.drawable.ic_info_outline),
    OrganizationEvent(R.string.organization_event, R.drawable.ic_profile),
    ProjectEvent(R.string.project_event, R.drawable.ic_info_outline)
}