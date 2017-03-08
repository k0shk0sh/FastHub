package com.fastaccess.provider.uil;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.fastaccess.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.InputStream;

public class UILImageGetter implements Html.ImageGetter {
    private TextView container;

    public UILImageGetter(TextView view) {
        this.container = view;
    }

    @Override public Drawable getDrawable(String source) {
        UrlImageDownloader urlDrawable = new UrlImageDownloader(container.getResources(), source);
        urlDrawable.drawable = ContextCompat.getDrawable(container.getContext(), R.drawable.ic_image);
        ImageLoader.getInstance().loadImage(source, new SimpleListener(urlDrawable));
        return urlDrawable;
    }

    private class SimpleListener extends SimpleImageLoadingListener {
        UrlImageDownloader urlImageDownloader;

        public SimpleListener(UrlImageDownloader downloader) {
            super();
            urlImageDownloader = downloader;
        }

        @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            int width = loadedImage.getWidth();
            int newWidth = width;
            if (width > container.getWidth()) {
                newWidth = container.getWidth();
            }
            BitmapDrawable result = new BitmapDrawable(container.getResources(), loadedImage);
            result.setBounds(0, 0, newWidth, loadedImage.getHeight());
            urlImageDownloader.setBounds(0, 0, newWidth, result.getIntrinsicHeight());
            urlImageDownloader.drawable = result;
            container.requestLayout();
            container.invalidate();
        }
    }

    private class UrlImageDownloader extends BitmapDrawable {
        public Drawable drawable;

        public UrlImageDownloader(Resources res, InputStream is) {
            super(res, is);
        }

        public UrlImageDownloader(Resources res, String filepath) {
            super(res, filepath);
            drawable = new BitmapDrawable(res, filepath);
        }

        public UrlImageDownloader(Resources res, Bitmap bitmap) {
            super(res, bitmap);
        }

        @Override public void draw(Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }
    }
}