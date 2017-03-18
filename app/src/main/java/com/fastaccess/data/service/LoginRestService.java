package com.fastaccess.data.service;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.AccessTokenModel;
import com.fastaccess.data.dao.AuthModel;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface LoginRestService {

    @POST("authorizations") Observable<AccessTokenModel> login(@NonNull @Body AuthModel authModel);
}
