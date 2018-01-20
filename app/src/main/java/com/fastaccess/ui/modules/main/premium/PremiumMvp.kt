package com.fastaccess.ui.modules.main.premium

import com.fastaccess.ui.base.mvp.BaseMvp

/**
 * Created by kosh on 15/07/2017.
 */
interface PremiumMvp {

    interface View : BaseMvp.FAView {
        fun onSuccessfullyActivated()
        fun onNoMatch()
    }

    interface Presenter {
        fun onCheckPromoCode(promo: String)
    }
}