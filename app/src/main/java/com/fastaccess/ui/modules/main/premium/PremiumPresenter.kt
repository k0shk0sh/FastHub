package com.fastaccess.ui.modules.main.premium

import com.fastaccess.data.dao.ProUsersModel
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.RxHelper
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.github.b3er.rxfirebase.database.data
import com.github.b3er.rxfirebase.database.rxUpdateChildren
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import io.reactivex.Observable


/**
 * Created by kosh on 15/07/2017.
 */
class PremiumPresenter : BasePresenter<PremiumMvp.View>(), PremiumMvp.Presenter {
    override fun onCheckPromoCode(promo: String) {
        val ref = FirebaseDatabase.getInstance().reference
        manageDisposable(RxHelper.getObservable(ref.child("fasthub_pro").child(promo)
                .data()
                .toObservable())
                .doOnSubscribe { sendToView { it.showProgress(0) } }
                .flatMap {
                    var user = ProUsersModel()
                    if (it.exists() && it.hasChildren()) {
                        val gti = object : GenericTypeIndicator<ProUsersModel>() {}
                        user = it.getValue(gti) ?: ProUsersModel()
                        if (user.isAllowed) {
                            if (user.type == 1) {
                                PrefGetter.setProItems()
                                user.isAllowed = false
                                user.count = user.count + 1
                                return@flatMap RxHelper.getObservable(ref.child("fasthub_pro").rxUpdateChildren(hashMapOf(Pair(promo, user)))
                                        .toObservable<ProUsersModel>())
                                        .map { true }
                            } else {
                                PrefGetter.setProItems()
                                PrefGetter.setEnterpriseItem()
                                user.count = user.count + 1
                                return@flatMap RxHelper.getObservable(ref.child("fasthub_pro").rxUpdateChildren(hashMapOf(Pair(promo, user)))
                                        .toObservable<ProUsersModel>())
                                        .map { true }
                            }
                        }
                    }
                    return@flatMap Observable.just(user.isAllowed)
                }
                .doOnComplete { sendToView { it.hideProgress() } }
                .subscribe({
                    if (it) {
                        sendToView { it.onSuccessfullyActivated() }
                    } else {
                        sendToView { it.onNoMatch() }
                    }
                }, ::println))
    }
}