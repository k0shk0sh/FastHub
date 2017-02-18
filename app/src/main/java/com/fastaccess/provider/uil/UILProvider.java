package com.fastaccess.provider.uil;

import android.app.AlarmManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.fastaccess.R;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by Kosh on 26 Nov 2016, 10:48 AM
 */

public class UILProvider {

    private UILProvider() {}

    public static void initUIL(@NonNull Context context) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .writeDebugLogs()
                    .defaultDisplayImageOptions(getOptions())
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCache(new LimitedAgeDiskCache(context.getCacheDir(), AlarmManager.INTERVAL_DAY))
                    .build();
            imageLoader.init(config);
        }
    }

    private static DisplayImageOptions getOptions() {
        return new DisplayImageOptions.Builder()
                .delayBeforeLoading(0)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .showImageForEmptyUri(R.drawable.ic_github_black)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }
}
