package com.fastaccess.fasthub.commit.list

import android.os.Bundle
import com.airbnb.deeplinkdispatch.DeepLink
import com.fastaccess.fasthub.commit.R
import com.fastaccess.fasthub.commit.view.CommitFragment
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.base.deeplink.WebDeepLink
import com.fastaccess.github.extensions.replace

@WebDeepLink(
    "/{login}/{repo}/commits",
    "/{login}/{repo}/commits/{branch}",
    "/{login}/{repo}/commit/{oid}",
    "/repos/{login}/{repo}/commits/",
    "/repos/{login}/{repo}/commits/{oid}",
    "/{login}/{repo}/pull/{number}/commits",
    "/{login}/{repo}/pull/{number}/commits/{oid}",
    "/{login}/{repo}/pull/{number}/files"
)
class CommitsListActivity : BaseActivity() {

    private val login by lazy { intent?.getStringExtra("login") }
    private val repo by lazy { intent?.getStringExtra("repo") }
    private val number by lazy { intent?.getStringExtra("number")?.toIntOrNull() ?: 0 }
    private val oid by lazy { intent?.getStringExtra("oid") }
    private val branch by lazy { intent?.getStringExtra("branch") }
    private val page by lazy { if (intent.getStringExtra(DeepLink.URI)?.contains("/files") == true) 1 else 0 }

    override fun layoutRes(): Int = R.layout.fragment_activity_layout

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val login = login
            val repo = repo
            if (!login.isNullOrEmpty() && !repo.isNullOrEmpty()) {
                val oid = oid
                if (number > 0) {
                    if (oid.isNullOrEmpty()) {
                        replace(R.id.container, CommitPagerFragment.newInstance(login, repo, number, page))
                    } else {
                        showCommitFragment(oid, login, repo)
                    }
                } else {
                    if (oid.isNullOrEmpty()) {
                        replace(R.id.container, CommitListFragment.newInstance(login, repo, number))
                    } else {
                        showCommitFragment(oid, login, repo)
                    }
                }
            } else {
                finish()
            }
        }
    }

    private fun showCommitFragment(oid: String, login: String, repo: String) {
        replace(R.id.container, CommitFragment.newInstance(oid, login, repo))
    }
}