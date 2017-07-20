package com.fastaccess.provider.gson;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fastaccess.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;

/**
 * Created by Kosh on 26 Mar 2017, 10:07 PM
 */

public class ToGsonProvider {

    public static Observable<String> getChangelog(@NonNull Context context) {
        return Observable.fromCallable(() -> {
            try (InputStream is = context.getResources().openRawResource(R.raw.changelog)) {
                try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[is.available()];
                    int read = is.read(buffer);//ignore lint
                    byteStream.write(buffer);
                    return byteStream.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }
}
