package com.fastaccess.github.ui.modules.editor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.platform.deeplink.RawWebDeepLink

/**
 * Created by Kosh on 2019-04-11.
 */
@RawWebDeepLink("")
class EditorWebViewActivity : BaseActivity() {
    override fun layoutRes(): Int = R.layout.code_editor_activity_layout

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {}

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, EditorWebViewActivity::class.java))
        }
    }
}