package com.fastaccess.ui.modules.repos.projects.list.columns

import android.os.Bundle
import android.view.View
import com.fastaccess.R
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Hashemsergani on 11.09.17.
 */
class PorjectColumnsFragment : BaseFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {
    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun fragmentLayout(): Int = R.layout.project_columns_layout

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {}
}