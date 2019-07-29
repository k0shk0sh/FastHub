package com.fastaccess.github.ui.modules.editor.dialog

import androidx.lifecycle.MutableLiveData
import com.fastaccess.data.model.FastHubErrors
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.repository.services.ImgurService
import com.fastaccess.github.base.BaseViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

/**
 * Created by Kosh on 2019-07-29.
 */
class UploadPictureViewModel @Inject constructor(
    private val service: ImgurService,
    private val scheduler: SchedulerProvider
) : BaseViewModel() {

    val uploadedFileLiveData = MutableLiveData<String>()

    fun upload(
        title: String,
        path: String
    ) {
        val image = File(path).asRequestBody("image/*".toMediaTypeOrNull())
        justSubscribe(
            service.postImage(title, image)
                .subscribeOn(scheduler.ioThread())
                .observeOn(scheduler.uiThread())
                .doOnNext {
                    val link = it.data?.link
                    if (!link.isNullOrEmpty()) {
                        uploadedFileLiveData.postValue(link)
                    } else {
                        error.postValue(FastHubErrors(FastHubErrors.ErrorType.OTHER))
                    }
                }
        )
    }
}