package com.fastaccess.ui.modules.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.CheckBox
import butterknife.BindView
import butterknife.OnClick
import butterknife.OnEditorAction
import butterknife.OnTextChanged
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.helper.AnimHelper
import com.fastaccess.helper.AppHelper
import com.fastaccess.helper.InputHelper
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.search.repos.SearchReposFragment
import com.fastaccess.ui.widgets.FontAutoCompleteEditText

class SearchUserActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    @BindView(R.id.forkCheckBox) lateinit var forkCheckBox: CheckBox
    @BindView(R.id.clear) lateinit var clear: View
    @BindView(R.id.searchEditText) lateinit var searchEditText: FontAutoCompleteEditText

    @State var username = ""
    @State var searchTerm = ""

    @OnTextChanged(value = [R.id.searchEditText], callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun onTextChange(str: Editable) {
        searchTerm = str.toString()
        if (searchTerm.isEmpty()) {
            AnimHelper.animateVisibility(clear, false)
        } else {
            AnimHelper.animateVisibility(clear, true)
        }
    }

    @OnClick(R.id.search) fun onSearchClicked() {
        searchTerm = searchEditText.text.toString()
        makeSearch()
    }

    @OnClick(R.id.forkCheckBox) fun checkBoxClicked() {
        onSearchClicked()
    }

    @OnEditorAction(R.id.searchEditText) fun onEditor(): Boolean {
        onSearchClicked()
        return true
    }

    @OnClick(R.id.clear) internal fun onClear(view: View) {
        if (view.id == R.id.clear) {
            searchEditText.setText("")
        }
    }

    override fun layout(): Int = R.layout.activity_search_user

    override fun isTransparent(): Boolean = false

    override fun canBack(): Boolean = true

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun isSecured(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val args = intent.extras
            username = args.getString(USERNAME)
            if (InputHelper.isEmpty(username)) {
                finish()
                return
            }
            searchTerm = args.getString(SEARCH_TERM)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, SearchReposFragment.newInstance(), "SearchReposFragment")
                    .commit()
        }
        searchEditText.setText(searchTerm)
        onSearchClicked()
    }

    private fun makeSearch() {
        val query = "user:$username $searchTerm fork:${forkCheckBox.isChecked}"
        getFragment()?.onQueueSearch(query)
    }

    private fun getFragment() = AppHelper.getFragmentByTag(supportFragmentManager, "SearchReposFragment") as? SearchReposFragment?

    companion object {
        val USERNAME = "username"
        val SEARCH_TERM = "search"

        fun getIntent(context: Context, username: String, searchTerm: String?): Intent {
            val intent = Intent(context, SearchUserActivity::class.java)
            intent.putExtra(USERNAME, username)
            intent.putExtra(SEARCH_TERM, searchTerm)
            return intent
        }
    }
}