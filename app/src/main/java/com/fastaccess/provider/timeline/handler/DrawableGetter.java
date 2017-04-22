package com.fastaccess.provider.timeline.handler;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URL;

/**
 * Created by Kosh on 22 Apr 2017, 7:44 PM
 */

public class DrawableGetter implements Html.ImageGetter {
    TextView container;
    URI baseUri;
    boolean matchParentWidth;

    public DrawableGetter(TextView textView) {
        this.container = textView;
        this.matchParentWidth = false;
    }

    public DrawableGetter(TextView textView, String baseUrl) {
        this.container = textView;
        if (baseUrl != null) {
            this.baseUri = URI.create(baseUrl);
        }
    }

    public DrawableGetter(TextView textView, String baseUrl, boolean matchParentWidth) {
        this.container = textView;
        this.matchParentWidth = matchParentWidth;
        if (baseUrl != null) {
            this.baseUri = URI.create(baseUrl);
        }
    }

    public Drawable getDrawable(String source) {
        UrlDrawable urlDrawable = new UrlDrawable();
        ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable, this, container, matchParentWidth);
        asyncTask.execute(source);
        return urlDrawable;
    }

    private static class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        private final WeakReference<UrlDrawable> drawableReference;
        private final WeakReference<DrawableGetter> imageGetterReference;
        private final WeakReference<View> containerReference;
        private final WeakReference<Resources> resources;
        private String source;
        private boolean matchParentWidth;
        private float scale;

        public ImageGetterAsyncTask(UrlDrawable d, DrawableGetter imageGetter, View container, boolean matchParentWidth) {
            this.drawableReference = new WeakReference<>(d);
            this.imageGetterReference = new WeakReference<>(imageGetter);
            this.containerReference = new WeakReference<>(container);
            this.resources = new WeakReference<>(container.getResources());
            this.matchParentWidth = matchParentWidth;
        }

        @Override protected Drawable doInBackground(String... params) {
            source = params[0];

            if (resources.get() != null) {
                return fetchDrawable(resources.get(), source);
            }

            return null;
        }

        @Override protected void onPostExecute(Drawable result) {
            if (result == null) {
                return;
            }
            final UrlDrawable urlDrawable = drawableReference.get();
            if (urlDrawable == null) {
                return;
            }
            if (matchParentWidth) {
                urlDrawable.setBounds(0, 0, (int) (result.getIntrinsicWidth() * scale), (int) (result.getIntrinsicHeight() * scale));
            } else {
                urlDrawable.setBounds(0, 0, (int) (result.getIntrinsicWidth() * 2.5), (int) (result.getIntrinsicHeight() * 2.5));
            }
            urlDrawable.drawable = result;

            final DrawableGetter imageGetter = imageGetterReference.get();
            if (imageGetter == null) {
                return;
            }
            imageGetter.container.invalidate();
            imageGetter.container.setText(imageGetter.container.getText());
        }

        Drawable fetchDrawable(Resources res, String urlString) {
            try {
                InputStream is = fetch(urlString);
                Drawable drawable = new BitmapDrawable(res, is);
                if (matchParentWidth) {
                    scale = getScale(drawable);
                    drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * scale), (int) (drawable.getIntrinsicHeight() * scale));
                } else {
                    drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * 2.5), (int) (drawable.getIntrinsicHeight() * 2.5));
                }
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }

        private float getScale(Drawable drawable) {
            View container = containerReference.get();
            if (!matchParentWidth || container == null) {
                return 1f;
            }
            float maxWidth = container.getWidth();
            float originalDrawableWidth = drawable.getIntrinsicWidth();
            return maxWidth / originalDrawableWidth;
        }

        private InputStream fetch(String urlString) throws IOException {
            URL url;
            final DrawableGetter imageGetter = imageGetterReference.get();
            if (imageGetter == null) {
                return null;
            }
            if (imageGetter.baseUri != null) {
                url = imageGetter.baseUri.resolve(urlString).toURL();
            } else {
                url = URI.create(urlString).toURL();
            }

            return (InputStream) url.getContent();
        }
    }

    @SuppressWarnings("deprecation")
    public class UrlDrawable extends BitmapDrawable {
        protected Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }
    }
} 
