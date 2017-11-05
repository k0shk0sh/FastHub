package com.fastaccess.ui.modules.main.premium

import com.fastaccess.data.dao.ProUsersModel
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.RxHelper
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.github.b3er.rxfirebase.database.data
import com.github.b3er.rxfirebase.database.rxUpdateChildren
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import io.reactivex.Completable
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
                    }
                    return@flatMap Observable.just(user)
                }
                .subscribe({ user ->
                    var completable: Completable? = null
                    val isAllowed = user.isAllowed
                    if (isAllowed && !user.isBlocked) {
                        if (user.type == 1) {
                            PrefGetter.setProItems()
                            user.isAllowed = false
                            user.count = user.count + 1
                            completable = ref.child("fasthub_pro")
                                    .rxUpdateChildren(hashMapOf(Pair(promo, user)))
                        } else {
                            PrefGetter.setProItems()
                            PrefGetter.setEnterpriseItem()
                            user.count = user.count + 1
                            completable = ref.child("fasthub_pro")
                                    .rxUpdateChildren(hashMapOf(Pair(promo, user)))
                        }
                    }
                    if (completable != null) {
                        manageDisposable(completable.doOnComplete({
                            if (isAllowed) {
                                sendToView { it.onSuccessfullyActivated() }
                            } else {
                                sendToView { it.onNoMatch() }
                            }
                        }).subscribe({}, { it.printStackTrace() }))
                    } else {
                        if (isAllowed) {
                            sendToView { it.onSuccessfullyActivated() }
                        } else {
                            if (user.isBlocked) {
                                PrefGetter.clearPurchases()
                            }
                            sendToView { it.onNoMatch() }
                        }
                    }
                }, { it.printStackTrace() }))
    }
}