package com.fastaccess.github.ui.modules.pr

import android.os.Bundle
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.extensions.replace
import com.fastaccess.github.platform.deeplink.WebDeepLink
import com.fastaccess.github.ui.modules.issue.fragment.IssueFragment
import com.fastaccess.github.ui.modules.pr.fragment.PullRequestFragment

/**
 * Created by Kosh on 28.01.19.
 */
@WebDeepLink("/{login}/{repo}/pull/{number}", "/{login}/{repo}/pulls/{number}")
class PullRequestActivity : BaseActivity() {

    override fun layoutRes(): Int = R.layout.activity_main
    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null && intent != null) {
            val login = intent?.extras?.getString("login")
            val repo = intent?.extras?.getString("repo")
            val number = intent?.extras?.getString("number")?.toIntOrNull()
            if (login == null || repo == null || number == null) {
                finish()
                return
            }
            replace(R.id.container, PullRequestFragment.newInstance(login, repo, number), IssueFragment.TAG)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
