package com.fastaccess.ui.modules.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.CheckBox
import butterknife.*
import com.fastaccess.R
import com.fastaccess.helper.AnimHelper
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.Logger
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.search.repos.SearchReposFragment
import com.fastaccess.ui.widgets.FontAutoCompleteEditText

class SearchUserActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    companion object {

        @JvmStatic
        val USERNAME = "username"
        @JvmStatic
        val SEARCH_TERM = "search"

        @JvmStatic
        fun getIntent(context: Context, username: String, searchTerm: String?): Intent {
            val intent = Intent(context, SearchUserActivity::class.java)
            intent.putExtra(USERNAME, username)
            intent.putExtra(SEARCH_TERM, searchTerm)
            return intent
        }
    }

    @BindView(R.id.forkCheckBox) lateinit var forkCheckBox: CheckBox
    @BindView(R.id.clear) lateinit var clear: View
    @BindView(R.id.searchEditText) lateinit var searchEditText: FontAutoCompleteEditText

    private var username = ""
    private var searchTerm = ""
    private var isFork = true
    private lateinit var searchReposFragment: SearchReposFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)

        searchReposFragment = SearchReposFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .replace(R.id.containerFragment, searchReposFragment, "SearchReposFragment")
                .commit()

        val args = intent.extras
        Logger.d("savedS $args")
        if (args != null) {
            username = args.getString(USERNAME)
            if (InputHelper.isEmpty(username))
                throw UninitializedPropertyAccessException("Username cannot be empty")
            searchTerm = args.getString(SEARCH_TERM)
            searchEditText.setText(searchTerm)
            onSearchClicked()
        }

        forkCheckBox.setOnClickListener {
            isFork = forkCheckBox.isChecked
            onSearchClicked()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        intent.putExtra(SEARCH_TERM, searchTerm)
    }

    @OnTextChanged(value = R.id.searchEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun onTextChange(str: Editable) {
        searchTerm = str.toString()
        if (searchTerm.isEmpty()) {
            AnimHelper.animateVisibility(clear, false)
        } else {
            AnimHelper.animateVisibility(clear, false)
        }
    }

    @OnClick(R.id.search)
    fun onSearchClicked() {
        searchTerm = searchEditText.text.toString()
        Logger.d("savedS $searchTerm")
        makeSearch()
    }

    @OnEditorAction(R.id.searchEditText)
    fun onEditor(): Boolean {
        onSearchClicked()
        return true
    }

    private fun makeSearch() {
        val query = "user:$username $searchTerm fork:$isFork"
        Logger.d(query)
        searchReposFragment.onQueueSearch(query)
    }

    override fun layout(): Int = R.layout.activity_search_user

    override fun isTransparent(): Boolean = false

    override fun canBack(): Boolean = true

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun isSecured(): Boolean = false

}
