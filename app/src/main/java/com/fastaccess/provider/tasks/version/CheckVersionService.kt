package com.fastaccess.provider.tasks.version

import android.app.IntentService
import android.content.Intent
import android.widget.Toast
import com.fastaccess.BuildConfig
import com.fastaccess.R
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.RestProvider

/**
 * Created by Kosh on 09 Jun 2017, 9:02 PM
 */
class CheckVersionService : IntentService("CheckVersionService") {

    override fun onHandleIntent(p0: Intent?) {
        RxHelper.getObserver(RestProvider.getRepoService()
                .getLatestRelease("k0shk0sh", "FastHub"))
                .subscribe({
                    if (it != null) {
                        Toast.makeText(this, if (BuildConfig.VERSION_NAME.contains(it.tagName))
                            R.string.up_to_date else R.string.new_version, Toast.LENGTH_LONG).show()
                    }
                }, { throwable -> throwable.printStackTrace() })
    }
}