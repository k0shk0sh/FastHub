package com.fastaccess.fasthub.diff

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.fastaccess.fasthub.dagger.annotations.ForActivity
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.base.utils.EXTRA
import kotlinx.android.synthetic.main.diff_patch_viewer_layout.*

class DiffViewerActivity : BaseActivity() {

    override fun layoutRes(): Int = R.layout.diff_patch_viewer_layout

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        title = ""
        findViewById<Toolbar>(R.id.toolbar).apply {
            title = ""
            findViewById<TextView>(R.id.toolbarTitle).text = getString(R.string.commit)
            setNavigationIcon(R.drawable.ic_clear)
            setSupportActionBar(this)
        }
        intent?.getStringExtra(EXTRA)?.let {
            webview.loadDiff(it)
        } ?: run { finish() }
    }

    companion object {
        fun startActivity(@ForActivity context: Context, patch: String) {
            context.startActivity(Intent(context, DiffViewerActivity::class.java).apply {
                putExtra(EXTRA, patch)
            })
        }
    }
}