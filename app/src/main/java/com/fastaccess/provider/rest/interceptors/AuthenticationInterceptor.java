package com.fastaccess.provider.rest.interceptors;

import android.support.annotation.NonNull;

import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.scheme.LinkParserHelper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {
    private boolean isScrapping;
    private String token;
    private String otp;

    public AuthenticationInterceptor(String token, String otp) {
        this.token = token;
        this.otp = otp;
    }

    public AuthenticationInterceptor() {
        this(false);
    }

    public AuthenticationInterceptor(boolean isScrapping) {
        this.isScrapping = isScrapping;
    }

    @Override public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();
        boolean isEnterprise = PrefGetter.isEnterprise() && LinkParserHelper.isNotEnterprise(original.url() != null ? original.url().host() : null);
        Logger.e(isEnterprise);
        String authToken = InputHelper.isEmpty(token) ? isEnterprise ? PrefGetter.getEnterpriseToken() : PrefGetter.getToken() : token;
        String otpCode = InputHelper.isEmpty(otp) ? isEnterprise ? PrefGetter.getEnterpriseOtpCode() : PrefGetter.getOtpCode() : otp;
        if (!InputHelper.isEmpty(authToken)) {
            builder.header("Authorization", authToken.startsWith("Basic") ? authToken : "token " + authToken);
        }
        if (!InputHelper.isEmpty(otpCode)) {
            builder.addHeader("X-GitHub-OTP", otpCode.trim());
        }
        if (!isScrapping) builder.addHeader("User-Agent", "FastHub");

        Request request = builder.build();
        return chain.proceed(request);
    }
}