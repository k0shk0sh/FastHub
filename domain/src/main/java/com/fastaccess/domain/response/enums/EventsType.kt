package com.fastaccess.domain.response.enums

import com.fastaccess.domain.R

enum class EventsType constructor(val titleId: Int, val drawableRes: Int) {
    WatchEvent(R.string.starred, R.drawable.ic_star_filled_small),
    CreateEvent(R.string.created_repo, R.drawable.ic_repo_small),
    CommitCommentEvent(R.string.commented_on_commit, R.drawable.ic_comment_small),
    DownloadEvent(R.string.downloaded, R.drawable.ic_download_small),
    FollowEvent(R.string.followed, R.drawable.ic_add_small),
    ForkEvent(R.string.forked, R.drawable.ic_fork_small),
    GistEvent(R.string.created_gist, R.drawable.ic_gists_small),
    GollumEvent(R.string.gollum, R.drawable.ic_info_outline_small),
    IssueCommentEvent(R.string.commented_on_issue, R.drawable.ic_comment_small),
    IssuesEvent(R.string.created_issue, R.drawable.ic_issues_small),
    MemberEvent(R.string.member, R.drawable.ic_add_small),
    PublicEvent(R.string.public_event, R.drawable.ic_repo_small),
    PullRequestEvent(R.string.pull_request, R.drawable.ic_pull_requests_uncolored_small),
    PullRequestReviewCommentEvent(R.string.pr_comment_review, R.drawable.ic_comment_small),
    PullRequestReviewEvent(R.string.pr_review_event, R.drawable.ic_eye_small),
    RepositoryEvent(R.string.repo_event, R.drawable.ic_repo_small),
    PushEvent(R.string.pushed, R.drawable.ic_push_small),
    TeamAddEvent(R.string.team_event, R.drawable.ic_profile_small),
    DeleteEvent(R.string.deleted, R.drawable.ic_trash_small),
    ReleaseEvent(R.string.released, R.drawable.ic_download_small),
    ForkApplyEvent(R.string.forked, R.drawable.ic_fork_small),
    OrgBlockEvent(R.string.organization_event, R.drawable.ic_profile_small),
    ProjectCardEvent(R.string.card_event, R.drawable.ic_info_outline_small),
    ProjectColumnEvent(R.string.project_event, R.drawable.ic_info_outline_small),
    OrganizationEvent(R.string.organization_event, R.drawable.ic_profile_small),
    ProjectEvent(R.string.project_event, R.drawable.ic_info_outline_small)
}