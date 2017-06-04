package com.fastaccess.provider.rest.interceptors;

import android.support.annotation.NonNull;

import com.fastaccess.data.service.NotificationService;
import com.fastaccess.helper.InputHelper;

import java.io.IOException;
import java.net.URI;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {

    private String authToken;
    private String otp;
    private boolean isScrapping;

    public AuthenticationInterceptor(String authToken, String otp) {
        this.authToken = authToken;
        this.otp = otp;
    }

    public AuthenticationInterceptor(String authToken, String otp, boolean isScrapping) {
        this.authToken = authToken;
        this.otp = otp;
        this.isScrapping = isScrapping;
    }

    @Override public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        if (original.url() != HttpUrl.get(URI.create(NotificationService.SUBSCRIPTION_URL))) {
            Request.Builder builder = original.newBuilder();
            if (!InputHelper.isEmpty(authToken)) {
                builder.header("Authorization", authToken.startsWith("Basic") ? authToken : "token " + authToken);
            }
            if (!InputHelper.isEmpty(otp)) {
                builder.addHeader("X-GitHub-OTP", otp.trim());
            }
            if (!isScrapping) builder.addHeader("User-Agent", "FastHub");
            Request request = builder.build();
            return chain.proceed(request);
        }
        return chain.proceed(original);
    }
}