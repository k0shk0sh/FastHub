package com.fastaccess.ui.modules.repos.code.commit.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.repos.RepoPagerActivity
import com.fastaccess.ui.modules.repos.code.commit.RepoCommitsFragment

/**
 * Created by Hashemsergani on 02/09/2017.
 */
class FileCommitHistoryActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    @State var login: String? = null
    @State var repoId: String? = null

    override fun layout(): Int = R.layout.activity_fragment_layout

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun isTransparent(): Boolean = true

    override fun canBack(): Boolean = true

    override fun isSecured(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null && intent != null) {
            repoId = intent.extras.getString(BundleConstant.ID)
            login = intent.extras.getString(BundleConstant.EXTRA)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, RepoCommitsFragment.newInstance(intent.extras!!), RepoCommitsFragment::class.java.simpleName)
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            repoId?.let {
                val intent = RepoPagerActivity.createIntent(this, it, login!!)
                val bundle = intent.extras
                bundle.putBoolean(BundleConstant.IS_ENTERPRISE, isEnterprise)
                intent.putExtras(bundle)
                startActivity(intent)
                finish()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun startActivity(context: Context, login: String, repoId: String, branch: String, path: String,
                          enterprise: Boolean) {
            val intent = Intent(context, FileCommitHistoryActivity::class.java)
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, login)
                    .put(BundleConstant.EXTRA_TWO, branch)
                    .put(BundleConstant.EXTRA_THREE, path)
                    .put(BundleConstant.IS_ENTERPRISE, enterprise)
                    .end())
            context.startActivity(intent)
        }
    }
}