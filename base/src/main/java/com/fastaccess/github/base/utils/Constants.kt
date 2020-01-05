package com.fastaccess.github.base.utils

import com.fastaccess.data.model.ActivityType

/**
 * Created by Kosh on 12 Nov 2016, 3:55 PM
 */

const val ITEM = "item"
const val ID = "id"
const val EXTRA = "extra"
const val EXTRA_TWO = "extra2_id"
const val EXTRA_THREE = "extra3_id"
const val EXTRA_FOUR = "extra4_id"
const val EXTRA_FIVE = "extra5_id"
const val EXTRA_SIX = "extra6_id"
const val EXTRA_SEVEN = "extra7_id"
const val EXTRA_EIGHT = "extra8_id"
const val EXTRA_TYPE = "extra_type"
const val YES_NO_EXTRA = "yes_no_extra"
const val NOTIFICATION_ID = "notification_id"
const val IS_ENTERPRISE = "is_enterprise"
const val REVIEW_EXTRA = "review_extra"
const val SCHEME_URL = "scheme_url"
const val REQUEST_CODE = 2016
const val REVIEW_REQUEST_CODE = 2017
const val REFRESH_CODE = 64
const val FOR_RESULT_EXTRA = "for_result_extra"
const val EDIT_GIST_COMMENT_EXTRA = "edit_comment_extra"
const val NEW_GIST_COMMENT_EXTRA = "new_gist_comment_extra"
const val EDIT_ISSUE_COMMENT_EXTRA = "edit_issue_comment_extra"
const val NEW_ISSUE_COMMENT_EXTRA = "new_issue_comment_extra"
const val EDIT_COMMIT_COMMENT_EXTRA = "edit_commit_comment_extra"
const val NEW_COMMIT_COMMENT_EXTRA = "new_commit_comment_extra"
const val NEW_REVIEW_COMMENT_EXTRA = "new_review_comment_extra"
const val EDIT_REVIEW_COMMENT_EXTRA = "edit_review_comment_extra"
const val PAGE_SIZE = 30
const val PRE_FETCH_SIZE = 30

// DEEP LINKS

const val REDIRECT_URL = "fasthub://login"
const val SCOPE_LIST = "user,repo,gist,notifications,read:org"
const val IN_APP_LINK = "app://fasthub"
const val GITHUB_LINK = "https://github.com/"
const val EDITOR_PATH = "editor"
const val LOGIN_PATH = "login"
const val LOGIN_DEEP_LINK = "$IN_APP_LINK/$LOGIN_PATH"
const val EDITOR_DEEP_LINK = "$IN_APP_LINK/$EDITOR_PATH"
const val TRENDING_LINK = "${GITHUB_LINK}trending"

val FEEDS_LINK = "$IN_APP_LINK/me/${ActivityType.FEEDS.name}"
val NOTIFICATION_LINK = "$IN_APP_LINK/me/${ActivityType.NOTIFICATION.name}"
val FILTER_ISSUE_LINK = "$IN_APP_LINK/me/${ActivityType.FILTER_ISSUE.name}"
val FILTER_PR_LINK = "$IN_APP_LINK/me/${ActivityType.FILTER_PR.name}"
val SEARCH_LINK = "$IN_APP_LINK/me/${ActivityType.SEARCH.name}"
