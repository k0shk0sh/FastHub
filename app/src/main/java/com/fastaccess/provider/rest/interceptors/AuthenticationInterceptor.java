package com.fastaccess.provider.rest.interceptors;

import java.io.IOException;

import lombok.AllArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@AllArgsConstructor
public class AuthenticationInterceptor implements Interceptor {

    private String authToken;

    @Override public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder().header("Authorization", authToken);
        Request request = builder.build();
        return chain.proceed(request);
    }
}