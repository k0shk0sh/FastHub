package com.fastaccess.github.editor

import android.os.Bundle
import com.fastaccess.github.base.deeplink.AppDeepLink
import com.fastaccess.github.base.utils.EDITOR_PATH
import com.fastaccess.github.extensions.replace

/**
 * Created by Kosh on 2019-04-11.
 */
@AppDeepLink("/$EDITOR_PATH")
class EditorActivity : com.fastaccess.github.base.BaseActivity() {
    override fun layoutRes(): Int = R.layout.fragment_activity_layout

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            replace(R.id.container,
                EditorFragment.newInstance(intent?.extras),
                EditorFragment.TAG
            )
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(EditorFragment.TAG) as? EditorFragment
        if (fragment?.onBackPressed() == true) {
            super.onBackPressed()
        }
    }
}