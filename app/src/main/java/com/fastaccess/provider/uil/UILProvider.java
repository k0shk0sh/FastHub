package com.fastaccess.provider.uil;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.fastaccess.BuildConfig;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kosh on 26 Nov 2016, 10:48 AM
 */

public class UILProvider {

    private final static long ONE_DAY = 24 * (60 * 60);

    private UILProvider() {}

    public static void initUIL(@NonNull Context context) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context)
                    .imageDownloader(new GithubAuthDownloader(context))
                    .defaultDisplayImageOptions(getOptions())
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCache(new LimitedAgeDiskCache(context.getCacheDir(), ONE_DAY));
            if (BuildConfig.DEBUG) {
                builder.writeDebugLogs();
            }
            imageLoader.init(builder.build());
        }
    }

    private static DisplayImageOptions getOptions() {
        Map<String, String> headers = new HashMap<>();
        if (PrefGetter.isEnterprise()) {
            String authToken = PrefGetter.getEnterpriseToken();
            headers.put("Authorization", authToken.startsWith("Basic") ? authToken : "token " + authToken);
        }
        return new DisplayImageOptions.Builder()
                .delayBeforeLoading(0)
                .extraForDownloader(headers)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public static void destoryUIL(@NonNull Context context) {
        if (ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().destroy();
            initUIL(context);
        }
    }

    private static class GithubAuthDownloader extends BaseImageDownloader {
        public GithubAuthDownloader(Context context) {super(context);}

        @SuppressWarnings("unchecked") @Override protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
            HttpURLConnection conn = super.createConnection(url, extra);
            Map<String, String> headers = (Map<String, String>) extra;
            if (headers != null && !headers.isEmpty()) {
                Logger.e(headers.keySet(), headers.values());
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            return conn;
        }
    }
}
