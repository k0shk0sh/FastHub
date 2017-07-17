package com.fastaccess.ui.modules.repos.extras.license

import com.fastaccess.ui.base.mvp.BaseMvp

/**
 * Created by Kosh on 30 Jun 2017, 12:32 PM
 */

interface RepoLicenseMvp {
    interface View : BaseMvp.FAView {
        fun onLicenseLoaded(license: String)
    }

    interface Presenter {
        fun onLoadLicense(login: String, repo: String)
    }
}