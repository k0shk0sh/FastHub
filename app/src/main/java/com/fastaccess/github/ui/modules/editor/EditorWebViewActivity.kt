package com.fastaccess.github.ui.modules.editor

import android.os.Bundle
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.extensions.replace
import com.fastaccess.github.platform.deeplink.AppDeepLink
import com.fastaccess.github.utils.WEB_EDITOR_PATH

/**
 * Created by Kosh on 2019-04-11.
 */
@AppDeepLink("/$WEB_EDITOR_PATH")
class EditorWebViewActivity : BaseActivity() {
    override fun layoutRes(): Int = R.layout.fragment_activity_layout

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        replace(R.id.container, WebEditorFragment())
    }
}