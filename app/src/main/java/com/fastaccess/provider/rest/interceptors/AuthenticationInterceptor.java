package com.fastaccess.provider.rest.interceptors;

import com.fastaccess.data.service.NotificationService;
import com.fastaccess.helper.InputHelper;

import java.io.IOException;
import java.net.URI;

import lombok.AllArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@AllArgsConstructor
public class AuthenticationInterceptor implements Interceptor {

    private String authToken;
    private String otp;

    @Override public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        if (original.url() != HttpUrl.get(URI.create(NotificationService.SUBSCRIPTION_URL))) {
            Request.Builder builder = original.newBuilder();
            if (!InputHelper.isEmpty(authToken)) {
                builder.header("Authorization", authToken.startsWith("Basic") ? authToken : "token " + authToken);
            }
            if (!InputHelper.isEmpty(otp)) {
                builder.addHeader("X-GitHub-OTP", otp.trim());
            }
            Request request = builder.build();
            return chain.proceed(request);
        }
        return chain.proceed(original);
    }
}