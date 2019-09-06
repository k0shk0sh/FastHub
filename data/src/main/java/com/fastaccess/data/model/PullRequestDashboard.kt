package com.fastaccess.data.model

data class PullRequestDashboard(
    var changedFiles: Int = 0,
    var additions: Int = 0,
    var deletions: Int = 0,
    var commits: Int = 0,
    var commentedReviews: Int = 0,
    var approvedReviews: Int = 0,
    var changeRequestedReviews: Int = 0
)