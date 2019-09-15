package com.fastaccess.fasthub.commit.list

import android.os.Bundle
import com.fastaccess.fasthub.commit.R
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.base.deeplink.WebDeepLink
import com.fastaccess.github.extensions.replace

@WebDeepLink(
    "/{login}/{repo}/commits",
    "/{login}/{repo}/commits/{branch}",
    "/{login}/{repo}/pull/{number}/commits",
    "/repos/{login}/{repo}/commits/{oid}"
)
class CommitsListActivity : BaseActivity() {

    private val login by lazy { intent?.getStringExtra("login") }
    private val repo by lazy { intent?.getStringExtra("repo") }
    private val number by lazy { intent?.getStringExtra("number")?.toIntOrNull() ?: 0 }
    private val oid by lazy { intent?.getStringExtra("oid") }
    private val branch by lazy { intent?.getStringExtra("branch") }

    override fun layoutRes(): Int = R.layout.fragment_activity_layout

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val login = login
            val repo = repo
            if (!login.isNullOrEmpty() && !repo.isNullOrEmpty()) {
                if (number > 0) {
                    replace(R.id.container, CommitPagerFragment.newInstance(login, repo, number))
                } else {
                    replace(R.id.container, CommitListFragment.newInstance(login, repo, number))
                }
            } else {
                finish()
            }
        }
    }
}