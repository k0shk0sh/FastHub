package com.fastaccess.fasthub.commit.list

import android.os.Bundle
import com.airbnb.deeplinkdispatch.DeepLinkHandler
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.base.deeplink.WebDeepLink

@WebDeepLink(
    "/{login}/{repo}/commits",
    "/{login}/{repo}/pull/{number}/commits"
)
class CommitsListActivity : BaseActivity() {

    private val isPullRequest by lazy { intent?.getStringExtra(DeepLinkHandler.EXTRA_URI)?.contains("/pull/", true) }
    private val login by lazy { intent.getStringExtra("login") }
    private val repo by lazy { intent.getStringExtra("repo") }
    private val number by lazy { intent.getIntExtra("number", 0) }

    override fun layoutRes(): Int = 0

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {

        }
    }
}