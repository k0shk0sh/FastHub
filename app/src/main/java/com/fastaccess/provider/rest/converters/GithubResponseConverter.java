package com.fastaccess.provider.rest.converters;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nostra13.universalimageloader.utils.IoUtils.DEFAULT_BUFFER_SIZE;

/**
 * call that supports String & Gson and always uses json as its request body
 */
@AllArgsConstructor
public class GithubResponseConverter extends Converter.Factory {
    private Gson gson;

    @Override public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type == String.class) {
            return new StringResponseConverter();
        }
        try {
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
        @Override public String convert(ResponseBody value) throws IOException {
            try {
                return value.string();
            } catch (OutOfMemoryError ignored) {
                return getString(value.charStream());
            }
        }

        @NonNull private String getString(@Nullable Reader reader) {
            if (reader == null) return "";
            StringWriter sw = new StringWriter();
            try {
                copy(reader, sw);
                return sw.toString();
            } catch (Exception ignored) {
                return "";
            }
        }

        @SuppressWarnings("UnusedReturnValue") private int copy(Reader input, Writer output) throws IOException {
            long count = copyLarge(input, output);
            if (count > Integer.MAX_VALUE) {
                return -1;
            }
            return (int) count;
        }


        private long copyLarge(Reader input, Writer output) throws IOException {
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            long count = 0;
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        }

    }
}
