package com.fastaccess.fasthub.reviews

import android.os.Bundle
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.base.deeplink.AppDeepLink
import com.fastaccess.github.base.utils.IN_APP_LINK
import com.fastaccess.github.extensions.replace

@AppDeepLink(
    "/repo/{login}/{repo}/pull/{number}/review/{id}",
    "/repo/{login}/{repo}/pull/{number}/reviews"
)
class PullRequestReviewsActivity : BaseActivity() {

    private val id by lazy { intent.getStringExtra("id") }
    private val login by lazy { intent.getStringExtra("login") ?: throw NullPointerException("no login") }
    private val repo by lazy { intent.getStringExtra("repo") ?: throw NullPointerException("no repo") }
    private val number by lazy { intent.getStringExtra("number")?.toIntOrNull() ?: throw NullPointerException("no number") }

    override fun layoutRes(): Int = R.layout.fragment_activity_layout

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            replace(R.id.container, ReviewsFragment.newInstance(login, repo, number, id))
        }
    }

    companion object {
        fun getUrl(
            login: String,
            repo: String,
            number: Int,
            id: String? = null
        ) = if (id.isNullOrEmpty()) {
            "$IN_APP_LINK/repo/$login/$repo/pull/$number/reviews"
        } else {
            "$IN_APP_LINK/repo/$login/$repo/pull/$number/review/$id"
        }
    }
}