package com.fastaccess.provider.changelog;

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

public class ChangelogProvider {

    @SuppressWarnings("ResultOfMethodCallIgnored") public static Observable<String> getChangelog(@NonNull Context context) {
        return Observable.fromCallable(() -> {
            InputStream is = context.getResources().openRawResource(R.raw.changelog);
            try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                byteStream.write(buffer);
                byteStream.close();
                is.close();
                return byteStream.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
