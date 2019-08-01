package com.fastaccess.github.ui.modules.issuesprs.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.model.parcelable.EditIssuePrBundleModel
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.extensions.route
import com.fastaccess.github.extensions.routeForResult
import com.fastaccess.github.utils.EDITOR_DEEPLINK
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.extensions.asString
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.appbar_center_title_layout.*
import kotlinx.android.synthetic.main.edit_issue_pr_fragment_layout.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 2019-07-27.
 */
class EditIssuePrFragment : BaseFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var markwon: Markwon

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(EditIssuePrViewModel::class.java) }
    private val model by lazy {
        arguments?.getParcelable(EXTRA) as? EditIssuePrBundleModel ?: throw NullPointerException("EditIssuePrBundleModel is null")
    }

    override fun viewModel(): BaseViewModel? = null
    override fun layoutRes(): Int = R.layout.edit_issue_pr_fragment_layout

    override fun onFragmentCreatedWithUser(
        view: View,
        savedInstanceState: Bundle?
    ) {

        toolbar.title = if (model.isCreate) getString(R.string.create_issue) else getString(R.string.edit)
        toolbar.subtitle = "${model.login}/${model.repo}/${getString(R.string.issue)}${if (model.isCreate) "" else "#${model.number}"}"
        setToolbarNavigationIcon(R.drawable.ic_clear)
        toolbar.inflateMenu(R.menu.submit_menu)
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        toolbar.setOnMenuItemClickListener {
            val title = titleEditText.asString()
            val description = model.description
            if (title.isEmpty() || model.login.isEmpty() || model.repo.isEmpty()) {
                titleInput.error = getString(R.string.required_field)
            } else {
                titleInput.error = null
                if (model.isCreate) {
                    viewModel.createIssue(model.login, model.repo, title, description)
                } else {
                    model.title = title
                    val intent = Intent().apply {
                        putExtra(EXTRA, model)
                    }
                    requireActivity().let {
                        it.setResult(Activity.RESULT_OK, intent)
                        it.finish()
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }

        observeChanges()

        if (savedInstanceState == null) {
            titleEditText.setText(model.title)
            val description = model.description
            if (!description.isNullOrEmpty()) {
                descriptionEditText.post { markwon.setMarkdown(descriptionEditText, description) }
            } else {
                viewModel.loadTemplate(model.login, model.repo)
            }
        }
        descriptionEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                routeForResult(EDITOR_DEEPLINK, COMMENT_REQUEST_CODE, bundleOf(EXTRA to model.description))
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                COMMENT_REQUEST_CODE -> {
                    model.description = data?.getStringExtra(EXTRA)
                    descriptionEditText.post { markwon.setMarkdown(descriptionEditText, model.description ?: "") }
                }
                else -> Timber.e("nothing yet for requestCode($requestCode)")
            }
        }
    }

    private fun observeChanges() {
        viewModel.templateLiveData.observeNotNull(this) {
            if (model.description.isNullOrEmpty()) {
                model.description = it
                descriptionEditText.post { markwon.setMarkdown(descriptionEditText, it) }
            }
        }
        viewModel.issueUrlLiveData.observeNotNull(this) {
            route(it)
            activity?.finish()
        }
    }


    companion object {
        private const val COMMENT_REQUEST_CODE = 1001
        fun newInstance(bundle: Bundle) = EditIssuePrFragment().apply {
            arguments = bundle
        }
    }
}