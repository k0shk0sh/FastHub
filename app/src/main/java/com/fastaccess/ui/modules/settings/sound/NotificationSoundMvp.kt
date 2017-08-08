package com.fastaccess.ui.modules.settings.sound

import android.net.Uri
import com.fastaccess.data.dao.NotificationSoundModel
import com.fastaccess.ui.base.mvp.BaseMvp

/**
 * Created by kosh on 23/07/2017.
 */

interface NotificationSoundMvp {
    interface View : BaseMvp.FAView {
        fun onAddSound(sound: NotificationSoundModel)
        fun onCompleted()
    }

    interface Presenter {
        fun loadSounds(default: String? = null)
    }

    interface NotificationSoundListener {
        fun onSoundSelected(uri: Uri? = null)
    }
}