package com.fastaccess.ui.modules.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnEditorAction
import butterknife.OnTextChanged
import com.fastaccess.R
import com.fastaccess.helper.AnimHelper
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.Logger
import com.fastaccess.ui.modules.search.repos.SearchReposFragment
import kotlinx.android.synthetic.main.activity_search_user.*

class SearchUserActivity : AppCompatActivity() {

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

    private var username = ""
    private var searchTerm = ""
    private var isFork = true
    private lateinit var searchReposFragment: SearchReposFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)
        ButterKnife.bind(this)

        searchReposFragment = SearchReposFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .replace(R.id.containerFragment, searchReposFragment, "SearchReposFragment")
                .commit()

        val args = intent.extras
        username = args.getString(USERNAME)
        if (InputHelper.isEmpty(username))
            throw UninitializedPropertyAccessException("Username cannot be empty")
        searchTerm = args.getString(SEARCH_TERM)
        if (InputHelper.isEmpty(searchTerm))
            searchTerm = ""
        Logger.d(username + " " + searchTerm)
        makeSearch()

        forkCheckBox.setOnClickListener {
            isFork = forkCheckBox.isChecked
            onSearchClicked()
        }
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
}
