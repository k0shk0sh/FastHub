package com.fastaccess.ui.modules.main.premium

import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.RxHelper
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.github.b3er.rxfirebase.database.data
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import io.reactivex.Observable


/**
 * Created by kosh on 15/07/2017.
 */
class PremiumPresenter : BasePresenter<PremiumMvp.View>(), PremiumMvp.Presenter {
    override fun onCheckPromoCode(promo: String) {
        val ref = FirebaseDatabase.getInstance().reference
        manageDisposable(RxHelper.getObservable(ref.child("promoCodes")
                .data()
                .toObservable())
                .doOnSubscribe { sendToView { it.showProgress(0) } }
                .flatMap {
                    var exists: Boolean? = false
                    if (it.exists()) {
                        val gti = object : GenericTypeIndicator<ArrayList<String>>() {}
                        val map = it.getValue(gti)
                        exists = map?.contains(promo)
                    }
                    return@flatMap Observable.just(exists)
                }
                .doOnComplete { sendToView { it.hideProgress() } }
                .subscribe({
                    when (it) {
                        true -> sendToView {
                            if (promo.contains("student")) {
                                PrefGetter.setProItems()
                            } else {
                                PrefGetter.setProItems()
                                PrefGetter.setEnterpriseItem()
                            }
                            it.onSuccessfullyActivated()
                        }
                        else -> sendToView { it.onNoMatch() }
                    }
                }, ::println))
    }
}