package com.fastaccess.provider.uil;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.fastaccess.BuildConfig;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

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
        return new DisplayImageOptions.Builder()
                .delayBeforeLoading(0)
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
}
