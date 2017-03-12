package com.fastaccess.data;

import android.support.annotation.NonNull;
import com.fastaccess.data.dao.AccessTokenModel;
import com.fastaccess.data.dao.AuthModel;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface LoginRestService {

  @FormUrlEncoded
  @POST("access_token")
  Observable<AccessTokenModel> getAccessToken(@NonNull @Field("code") String code,
      @NonNull @Field("client_id") String clientId,
      @NonNull @Field("client_secret") String clientSecret,
      @NonNull @Field("state") String state,
      @NonNull @Field("redirect_uri") String redirectUrl);

  @PUT("authorizations/clients/{clientId}/{fingerprint}") Observable<AccessTokenModel> login(@NonNull @Path("clientId") String clientId,
      @NonNull @Path("clientId") String fingerprint,
      @NonNull @Body AuthModel authModel);

  @PUT("authorizations/clients/{clientId}/{fingerprint}") Observable<AccessTokenModel> login(@NonNull @Path("clientId") String clientId,
      @NonNull @Path("clientId") String fingerprint,
      @NonNull @Body AuthModel authModel,
      @NonNull @Header("X-GitHub-OTP") String otpCode);

  @DELETE("authorizations/{id}") Observable<Response<Boolean>> deleteToken(@Path("id") long id);
}
