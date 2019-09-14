package com.fastaccess.github.ui.modules.issuesprs.edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fastaccess.data.model.parcelable.EditIssuePrBundleModel
import com.fastaccess.github.R
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.replace

/**
 * Created by Kosh on 2019-07-27.
 */
class EditIssuePrActivity : com.fastaccess.github.base.BaseActivity() {

    override fun layoutRes(): Int = R.layout.fragment_activity_layout

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        (savedInstanceState == null).isTrue {
            val bundle = intent.extras ?: return@isTrue run {
                finish()
            }
            replace(R.id.container, EditIssuePrFragment.newInstance(bundle))
        }
    }

    companion object {
        fun startForResult(
            fragment: Fragment,
            model: EditIssuePrBundleModel,
            requestCode: Int
        ) {
            fragment.startActivityForResult(Intent(fragment.requireContext(), EditIssuePrActivity::class.java).apply {
                putExtra(EXTRA, model)
            }, requestCode)
        }

        fun start(
            context: Context,
            model: EditIssuePrBundleModel
        ) {
            context.startActivity(Intent(context, EditIssuePrActivity::class.java).apply {
                putExtra(EXTRA, model)
            })
        }
    }
}