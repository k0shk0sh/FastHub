package com.fastaccess.ui.modules.repos.wiki

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.wiki.WikiContentModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.main.MainActivity
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.bindView
import com.prettifier.pretty.PrettifyWebView

/**
 * Created by Kosh on 13 Jun 2017, 8:35 PM
 */
class WikiActivity : BaseActivity<WikiMvp.View, WikiPresenter>(), WikiMvp.View {

    val navMenu: NavigationView by bindView(R.id.wikiSidebar)
    val drawerLayout: DrawerLayout by bindView(R.id.drawer)
    val progressbar: ProgressBar by bindView(R.id.progress)
    val stateLayout: StateLayout by bindView(R.id.stateLayout)
    val webView: PrettifyWebView by bindView(R.id.webView)

    @State var wiki = WikiContentModel(null, null, arrayListOf())
    @State var selectedTitle: String = "Home"

    override fun layout(): Int = R.layout.wiki_activity_layout

    override fun isTransparent(): Boolean = true

    override fun providePresenter(): WikiPresenter = WikiPresenter()

    override fun onLoadContent(wiki: WikiContentModel) {
        hideProgress()
        this.wiki = wiki
        if (wiki.sidebar.isNotEmpty()) {
            loadMenu()
        }
        if (wiki.content != null) {
            webView.setGithubContent(wiki.content, null, true)
        }
    }

    private fun loadMenu() {
        navMenu.menu.clear()
        wiki.sidebar.onEach {
            navMenu.menu.add(R.id.languageGroup, it.title?.hashCode()!!, Menu.NONE, it.title)
                    .setCheckable(true)
                    .isChecked = it.title.toLowerCase() == selectedTitle.toLowerCase()
        }
    }

    override fun canBack(): Boolean = true

    override fun isSecured(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            onLoadContent(wiki)
        } else {
            presenter.onActivityCreated(intent)
        }
        navMenu.setNavigationItemSelectedListener {
            onSidebarClicked(it)
            return@setNavigationItemSelectedListener true
        }

        toolbar?.subtitle = presenter.login + "/" + presenter.repoId
    }

    private fun onSidebarClicked(item: MenuItem) {
        this.selectedTitle = item.title.toString()
        closeDrawerLayout()
        wiki.sidebar.first { it.title?.toLowerCase() == item.title.toString().toLowerCase() }
                .let { presenter.onSidebarClicked(it) }
    }

    private fun closeDrawerLayout() {
        drawerLayout.closeDrawer(Gravity.END)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.trending_menu, menu)
        menu?.findItem(R.id.menu)?.setIcon(R.drawable.ic_menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu -> {
                drawerLayout.openDrawer(Gravity.END)
                return true
            }
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        hideProgress()
        super.showMessage(titleRes, msgRes)
    }

    override fun showMessage(titleRes: String, msgRes: String) {
        hideProgress()
        super.showMessage(titleRes, msgRes)
    }

    override fun showErrorMessage(msgRes: String) {
        hideProgress()
        super.showErrorMessage(msgRes)
    }

    override fun showProgress(resId: Int) {
        progressbar.visibility = View.VISIBLE
        stateLayout.showProgress()
    }

    override fun hideProgress() {
        progressbar.visibility = View.GONE
        stateLayout.hideProgress()
    }

    companion object {
        fun getWiki(context: Context, repoId: String?, username: String?): Intent {
            val intent = Intent(context, WikiActivity::class.java)
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, username)
                    .end())
            return intent
        }
    }
}