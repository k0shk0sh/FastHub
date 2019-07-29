package com.fastaccess.domain.repository.services

import com.fastaccess.domain.response.ImgureResponseModel
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query


/**
 * Created by Kosh on 2019-07-29.
 */
interface ImgurService {
    @POST("image")
    fun postImage(@Query("title") title: String, @Body body: RequestBody): Observable<ImgureResponseModel>

}