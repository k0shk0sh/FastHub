package com.fastaccess.ui.modules.repos.git

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.InputHelper
import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.provider.markdown.MarkDownProvider
import com.fastaccess.provider.scheme.LinkParserHelper
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.widgets.markdown.MarkDownLayout
import com.fastaccess.ui.widgets.markdown.MarkdownEditText

/**
 * Created by kosh on 29/08/2017.
 */
class EditRepoFileActivity : BaseActivity<EditRepoFileMvp.View, EditRepoFilePresenter>(), EditRepoFileMvp.View {

    @BindView(R.id.markDownLayout) lateinit var markDownLayout: MarkDownLayout
    @BindView(R.id.editText) lateinit var editText: MarkdownEditText

    override fun layout(): Int = R.layout.edit_repo_file_layout

    override fun isTransparent(): Boolean = false

    override fun canBack(): Boolean = true

    override fun isSecured(): Boolean = true

    override fun providePresenter(): EditRepoFilePresenter = EditRepoFilePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        markDownLayout.markdownListener = this
        setToolbarIcon(R.drawable.ic_clear)
        if (savedInstanceState == null) {
            presenter.onInit(intent)
        }
        val path = presenter.path
        if (!path.isNullOrBlank()) {
            title = Uri.parse(path)?.lastPathSegment
            toolbar?.let {
                it.subtitle = "${presenter.login}/${presenter.repoId}"
            }
        }
        invalidateOptionsMenu()
    }

    override fun onSetText(content: String?) {
        hideProgress()
        editText.setText(content)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.done_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.submit) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (menu.findItem(R.id.submit) != null) {
            menu.findItem(R.id.submit).isEnabled = true
        }
        presenter.isEdit?.let {
            menu.findItem(R.id.submit).setIcon(R.drawable.ic_done)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onAppendLink(title: String?, link: String?, isLink: Boolean) {
        if (isLink) {
            MarkDownProvider.addLink(editText, InputHelper.toString(title), InputHelper.toString(link))
        } else {
            editText.setText(String.format("%s\n", editText.text))
            MarkDownProvider.addPhoto(editText, InputHelper.toString(title), InputHelper.toString(link))
        }
    }

    override fun getEditText(): EditText = editText

    override fun getSavedText(): CharSequence? = editText.savedText

    override fun fragmentManager(): FragmentManager = supportFragmentManager

    @SuppressLint("SetTextI18n")
    override fun onEmojiAdded(emoji: Emoji?) {
        markDownLayout.onEmojiAdded(emoji)
    }

    companion object {
        val EDIT_RQ = 2017

        fun startForResult(activity: Activity, repoId: String, login: String,
                           path: String, contentUrl: String, isEdit: Boolean) {
            val bundle = Bundler.start()
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.EXTRA_TWO, path)
                    .put(BundleConstant.EXTRA_THREE, contentUrl)
                    .put(BundleConstant.EXTRA_TYPE, isEdit)
                    .put(BundleConstant.IS_ENTERPRISE, LinkParserHelper.isEnterprise(contentUrl))
                    .end()
            val intent = Intent(activity, EditRepoFileActivity::class.java)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, EDIT_RQ)
        }

        fun startForResult(activity: Fragment, repoId: String, login: String,
                           path: String, contentUrl: String, isEdit: Boolean) {
            val bundle = Bundler.start()
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.EXTRA_TWO, path)
                    .put(BundleConstant.EXTRA_THREE, contentUrl)
                    .put(BundleConstant.EXTRA_TYPE, isEdit)
                    .put(BundleConstant.IS_ENTERPRISE, LinkParserHelper.isEnterprise(contentUrl))
                    .end()
            val intent = Intent(activity.context, EditRepoFileActivity::class.java)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, EDIT_RQ)
        }
    }
}