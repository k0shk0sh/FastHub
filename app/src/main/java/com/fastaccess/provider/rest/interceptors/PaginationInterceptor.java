package com.fastaccess.provider.rest.interceptors;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.fastaccess.helper.InputHelper;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PaginationInterceptor implements Interceptor {

    @Override public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        Headers headers = chain.request().headers();
        if (headers != null) {
            if ((headers.values("Accept").contains("application/vnd.github.html") ||
                    headers.values("Accept").contains("application/vnd.github.VERSION.raw"))) {
                return response;//return them as they are.
            }
        }
        if (response.isSuccessful()) {
            if (response.peekBody(1).string().equals("[")) {
                String json = "{";
                String link = response.header("link");
                if (link != null) {
                    String[] links = link.split(",");
                    for (String link1 : links) {
                        String[] pageLink = link1.split(";");
                        String page = Uri.parse(pageLink[0].replaceAll("[<>]", "")).getQueryParameter("page");
                        String rel = pageLink[1].replaceAll("\"", "").replace("rel=", "");
                        if (page != null) json += String.format("\"%s\":\"%s\",", rel.trim(), page);
                    }
                }
                json += String.format("\"items\":%s}", response.body().string());
                return response.newBuilder().body(ResponseBody.create(response.body().contentType(), json)).build();
            } else if (response.header("link") != null) {
                String link = response.header("link");
                String pagination = "";
                String[] links = link.split(",");
                for (String link1 : links) {
                    String[] pageLink = link1.split(";");
                    String page = Uri.parse(pageLink[0].replaceAll("[<>]", "")).getQueryParameter("page");
                    String rel = pageLink[1].replaceAll("\"", "").replace("rel=", "");
                    if (page != null) pagination += String.format("\"%s\":\"%s\",", rel.trim(), page);
                }
                if (!InputHelper.isEmpty(pagination)) {//hacking for search pagination.
                    String body = response.body().string();
                    return response.newBuilder().body(ResponseBody.create(response.body().contentType(),
                            "{" + pagination + body.substring(1, body.length()))).build();
                }
            }
        }
        return response;
    }

}
