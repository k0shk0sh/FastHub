package com.fastaccess.data.service;

import android.support.annotation.Nullable;

import com.fastaccess.data.dao.ImgurReponseModel;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import io.reactivex.Observable;

/**
 * Created by Kosh on 15 Apr 2017, 8:06 PM
 */

public interface ImgurService {
    @POST("image")
    Observable<ImgurReponseModel> postImage(@Nullable @Query("title") String title, @Body RequestBody body);
}
