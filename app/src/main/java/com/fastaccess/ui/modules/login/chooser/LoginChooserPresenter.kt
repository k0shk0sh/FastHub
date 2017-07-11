package com.fastaccess.ui.modules.login.chooser

import com.fastaccess.data.dao.model.Login
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

class LoginChooserPresenter : BasePresenter<LoginChooserMvp.View>() {
    init {
        manageObservable(Login.getAccounts().toList()
                .toObservable()
                .doOnNext { sendToView { view -> view.onAccountsLoaded(it) } })
    }
}