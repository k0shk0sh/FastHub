package com.fastaccess.github.ui.modules.editor

import android.os.Bundle
import android.view.View
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BaseViewModel

/**
 * Created by Kosh on 2019-07-20.
 */
class WebEditorFragment : BaseFragment() {

    override fun viewModel(): BaseViewModel? = null
    override fun layoutRes(): Int = R.layout.editor_fragment_layout

    override fun onFragmentCreatedWithUser(
        view: View,
        savedInstanceState: Bundle?
    ) {

    }
}