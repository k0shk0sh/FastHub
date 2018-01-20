package com.fastaccess.ui.modules.settings.sound

import com.fastaccess.App
import com.fastaccess.data.dao.NotificationSoundModel
import com.fastaccess.helper.FileHelper
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import io.reactivex.Observable

/**
 * Created by kosh on 23/07/2017.
 */
class NotificationSoundPresenter : BasePresenter<NotificationSoundMvp.View>(), NotificationSoundMvp.Presenter {
    override fun loadSounds(default: String?) {
        manageObservable(Observable.fromPublisher<NotificationSoundModel> { s ->
            val sounds = FileHelper.getNotificationSounds(App.getInstance(), default)
            sounds.filterNotNull()
                    .sortedBy { !it.isSelected }
                    .onEach { s.onNext(it) }
            s.onComplete()
        }.doOnNext({ t -> sendToView { it.onAddSound(t) } })
                .doOnComplete { sendToView { it.onCompleted() } })
    }
}