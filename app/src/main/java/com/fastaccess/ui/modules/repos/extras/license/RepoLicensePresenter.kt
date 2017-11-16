package com.fastaccess.ui.modules.repos.extras.license

import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 30 Jun 2017, 12:34 PM
 */
class RepoLicensePresenter : BasePresenter<RepoLicenseMvp.View>(), RepoLicenseMvp.Presenter {

    override fun onLoadLicense(login: String, repo: String) {
        makeRestCall(RestProvider.getRepoService(isEnterprise).getLicense(login, repo),
                { license -> sendToView { it.onLicenseLoaded(license) } })
    }

}