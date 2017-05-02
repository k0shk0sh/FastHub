package com.fastaccess.provider.timeline.handler;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.fastaccess.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.lang.ref.WeakReference;

/**
 * Created by Kosh on 22 Apr 2017, 7:44 PM
 */

public class DrawableGetter implements Html.ImageGetter {
    private WeakReference<TextView> container;

    @Nullable TextView get() {
        return container != null ? container.get() : null;
    }

    public DrawableGetter(TextView t) {
        this.container = new WeakReference<>(t);
    }

    @Override public Drawable getDrawable(String source) {
        TextView textView = get();
        if (textView == null) return null;
        UrlImageDownloader urlDrawable = new UrlImageDownloader(textView.getResources(), source);
        urlDrawable.drawable = ContextCompat.getDrawable(textView.getContext(), R.drawable.ic_github_dark);
        ImageLoader.getInstance().loadImage(source, new SimpleListener(urlDrawable));
        return urlDrawable;
    }

    private class SimpleListener extends SimpleImageLoadingListener {
        UrlImageDownloader urlImageDownloader;

        SimpleListener(UrlImageDownloader downloader) {
            super();
            urlImageDownloader = downloader;
        }

        @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            int width = loadedImage.getWidth();
            int height = loadedImage.getHeight();
            int newWidth = (int) (width / 1.5);
            int newHeight = (int) (height / 1.5);
            TextView textView = get();
            if (textView == null) return;
            Drawable result = new BitmapDrawable(textView.getResources(), loadedImage);
            result.setBounds(0, 0, newWidth, newHeight);
            urlImageDownloader.setBounds(0, 0, newWidth, newHeight);
            urlImageDownloader.drawable = result;
            textView.invalidate();
            textView.setText(textView.getText());

        }
    }

    private class UrlImageDownloader extends BitmapDrawable {
        public Drawable drawable;

        UrlImageDownloader(Resources res, String filepath) {
            super(res, filepath);
            drawable = new BitmapDrawable(res, filepath);
        }

        @Override public void draw(Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }
    }
}
