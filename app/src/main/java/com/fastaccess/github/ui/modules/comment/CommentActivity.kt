package com.fastaccess.github.ui.modules.comment

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.fastaccess.github.R
import com.fastaccess.github.base.utils.EXTRA_FIVE
import com.fastaccess.github.base.utils.EXTRA_FOUR
import com.fastaccess.github.base.utils.EXTRA_THREE
import com.fastaccess.github.extensions.replace

/**
 * Created by Kosh on 2019-08-18.
 */
class CommentActivity : com.fastaccess.github.base.BaseActivity() {
    override fun layoutRes(): Int = R.layout.fragment_activity_layout

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val bundle = intent?.extras ?: return run { finish() }
            replace(R.id.container, CommentFragment.newInstance(bundle))
        }
    }

    companion object {
        fun startActivity(
            fragment: Fragment,
            requestCode: Int,
            comment: String,
            name: String? = null,
            pictureUrl: String? = null
        ) {
            val bundle = bundleOf(
                EXTRA_FIVE to pictureUrl,
                EXTRA_FOUR to name,
                EXTRA_THREE to comment
            )

            fragment.startActivityForResult(Intent(fragment.requireContext(), CommentActivity::class.java).apply {
                putExtras(bundle)
            }, requestCode)
        }
    }
}