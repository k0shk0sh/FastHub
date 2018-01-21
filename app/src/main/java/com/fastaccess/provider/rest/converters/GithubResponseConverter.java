package com.fastaccess.provider.rest.converters;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import lombok.AllArgsConstructor;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * call that supports String & Gson and always uses json as its request body
 */
@AllArgsConstructor
public class GithubResponseConverter extends Converter.Factory {
    private Gson gson;

    @Override public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        try {
            if (type == String.class) {
                return new StringResponseConverter();
            }
            return GsonConverterFactory.create(gson).responseBodyConverter(type, annotations, retrofit);
        } catch (OutOfMemoryError ignored) {
            return null;
        }
    }

    @Override public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                                    Annotation[] methodAnnotations, Retrofit retrofit) {
        return GsonConverterFactory.create(gson).requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }

    private static class StringResponseConverter implements Converter<ResponseBody, String> {
        @Override public String convert(@NonNull ResponseBody value) throws IOException {
            return value.string();
        }
    }
}
