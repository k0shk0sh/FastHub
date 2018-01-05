package com.fastaccess.ui.modules.repos.wiki

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import butterknife.BindView
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.NameParser
import com.fastaccess.data.dao.wiki.WikiContentModel
import com.fastaccess.helper.ActivityHelper
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.Logger
import com.fastaccess.provider.scheme.LinkParserHelper
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.repos.RepoPagerActivity
import com.fastaccess.ui.widgets.StateLayout
import com.prettifier.pretty.PrettifyWebView

/**
 * Created by Kosh on 13 Jun 2017, 8:35 PM
 */
class WikiActivity : BaseActivity<WikiMvp.View, WikiPresenter>(), WikiMvp.View {

    @BindView(R.id.wikiSidebar) lateinit var navMenu: NavigationView
    @BindView(R.id.drawer) lateinit var drawerLayout: DrawerLayout
    @BindView(R.id.progress) lateinit var progressbar: ProgressBar
    @BindView(R.id.stateLayout) lateinit var stateLayout: StateLayout
    @BindView(R.id.webView) lateinit var webView: PrettifyWebView

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
            val baseUrl = Uri.Builder().scheme("https")
                    .authority(LinkParserHelper.HOST_DEFAULT)
                    .appendPath(presenter.login)
                    .appendPath(presenter.repoId)
                    .appendPath("wiki")
                    .build()
                    .toString()
            Logger.e(baseUrl)
            webView.setWikiContent(wiki.content, baseUrl)
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
        setTaskName("${presenter.login}/${presenter.repoId} - Wiki - $selectedTitle")
    }

    private fun onSidebarClicked(item: MenuItem) {
        this.selectedTitle = item.title.toString()
        setTaskName("${presenter.login}/${presenter.repoId} - Wiki - $selectedTitle")
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
            R.id.share -> {
                ActivityHelper.shareUrl(this, "${LinkParserHelper.PROTOCOL_HTTPS}://${LinkParserHelper.HOST_DEFAULT}" +
                        "${presenter.login}/${presenter.repoId}/wiki/$selectedTitle")
                return true
            }
            android.R.id.home -> {
                if (!presenter.login.isNullOrEmpty() && !presenter.repoId.isNullOrEmpty()) {
                    val nameParse = NameParser("")
                    nameParse.name = presenter.repoId!!
                    nameParse.username = presenter.login!!
                    nameParse.isEnterprise = isEnterprise
                    RepoPagerActivity.startRepoPager(this, nameParse)
                }
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
            return getWiki(context, repoId, username, null)
        }

        fun getWiki(context: Context, repoId: String?, username: String?, page: String?): Intent {
            val intent = Intent(context, WikiActivity::class.java)
            intent.putExtras(Bundler.start()
                    .put(BundleConstant.ID, repoId)
                    .put(BundleConstant.EXTRA, username)
                    .put(BundleConstant.EXTRA_TWO, page)
                    .end())
            return intent
        }
    }
}