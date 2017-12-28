package ru.noties.markwon.il;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifDrawable;
import ru.noties.markwon.spans.AsyncDrawable;

public class AsyncDrawableLoader implements AsyncDrawable.Loader {

    public static AsyncDrawableLoader create() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_SVG = "image/svg+xml";
    private static final String CONTENT_TYPE_GIF = "image/gif";

    private static final String FILE_ANDROID_ASSETS = "android_asset";

    private final OkHttpClient client;
    private final Resources resources;
    private final ExecutorService executorService;
    private final Handler mainThread;
    private final Drawable errorDrawable;

    private final Map<String, Future<?>> requests;

    AsyncDrawableLoader(Builder builder) {
        this.client = builder.client;
        this.resources = builder.resources;
        this.executorService = builder.executorService;
        this.mainThread = new Handler(Looper.getMainLooper());
        this.errorDrawable = builder.errorDrawable;
        this.requests = new HashMap<>(3);
    }


    @Override
    public void load(@NonNull String destination, @NonNull AsyncDrawable drawable) {
        // if drawable is not a link -> show loading placeholder...
        requests.put(destination, execute(destination, drawable));
    }

    @Override
    public void cancel(@NonNull String destination) {

        final Future<?> request = requests.remove(destination);
        if (request != null) {
            request.cancel(true);
        }

        final List<Call> calls = client.dispatcher().queuedCalls();
        if (calls != null) {
            for (Call call : calls) {
                if (!call.isCanceled()) {
                    if (destination.equals(call.request().tag())) {
                        call.cancel();
                    }
                }
            }
        }
    }

    private Future<?> execute(@NonNull final String destination, @NonNull AsyncDrawable drawable) {
        final WeakReference<AsyncDrawable> reference = new WeakReference<AsyncDrawable>(drawable);
        // todo, if not a link -> show placeholder
        return executorService.submit(new Runnable() {
            @Override
            public void run() {

                final Item item;

                final Uri uri = Uri.parse(destination);
                if ("file".equals(uri.getScheme())) {
                    item = fromFile(uri);
                } else {
                    item = fromNetwork(destination);
                }

                Drawable result = null;

                if (item != null
                        && item.inputStream != null) {
                    try {
                        if (CONTENT_TYPE_SVG.equals(item.type)) {
                            result = handleSvg(item.inputStream);
                        } else if (CONTENT_TYPE_GIF.equals(item.type)) {
                            result = handleGif(item.inputStream);
                        } else {
                            result = handleSimple(item.inputStream);
                        }
                    } finally {
                        try {
                            item.inputStream.close();
                        } catch (IOException e) {
                            // no op
                        }
                    }
                }

                // if result is null, we assume it's an error
                if (result == null) {
                    result = errorDrawable;
                }

                if (result != null) {
                    final Drawable out = result;
                    mainThread.post(new Runnable() {
                        @Override
                        public void run() {
                            final AsyncDrawable asyncDrawable = reference.get();
                            if (asyncDrawable != null && asyncDrawable.isAttached()) {
                                asyncDrawable.setResult(out);
                            }
                        }
                    });
                }

                requests.remove(destination);
            }
        });
    }

    private Item fromFile(Uri uri) {

        final List<String> segments = uri.getPathSegments();
        if (segments == null
                || segments.size() == 0) {
            // pointing to file & having no path segments is no use
            return null;
        }

        final Item out;
        final String type;
        final InputStream inputStream;

        final boolean assets = FILE_ANDROID_ASSETS.equals(segments.get(0));
        final String lastSegment = uri.getLastPathSegment();

        if (lastSegment.endsWith(".svg")) {
            type = CONTENT_TYPE_SVG;
        } else if (lastSegment.endsWith(".gif")) {
            type = CONTENT_TYPE_GIF;
        } else {
            type = null;
        }

        if (assets) {
            final StringBuilder path = new StringBuilder();
            for (int i = 1, size = segments.size(); i < size; i++) {
                if (i != 1) {
                    path.append('/');
                }
                path.append(segments.get(i));
            }
            // load assets
            InputStream inner = null;
            try {
                inner = resources.getAssets().open(path.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = inner;
        } else {
            InputStream inner = null;
            try {
                inner = new BufferedInputStream(new FileInputStream(new File(uri.getPath())));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            inputStream = inner;
        }

        if (inputStream != null) {
            out = new Item(type, inputStream);
        } else {
            out = null;
        }

        return out;
    }

    private Item fromNetwork(String destination) {

        Item out = null;

        final Request request = new Request.Builder()
                .url(destination)
                .tag(destination)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            final ResponseBody body = response.body();
            if (body != null) {
                final InputStream inputStream = body.byteStream();
                if (inputStream != null) {
                    final String type;
                    final String contentType = response.header(HEADER_CONTENT_TYPE);
                    if (!TextUtils.isEmpty(contentType)
                            && contentType.startsWith(CONTENT_TYPE_SVG)) {
                        type = CONTENT_TYPE_SVG;
                    } else {
                        type = contentType;
                    }
                    out = new Item(type, inputStream);
                }
            }
        }

        return out;
    }

    private Drawable handleSvg(InputStream stream) {

        final Drawable out;

        SVG svg = null;
        try {
            svg = SVG.getFromInputStream(stream);
        } catch (SVGParseException e) {
            e.printStackTrace();
        }

        if (svg == null) {
            out = null;
        } else {

            final float w = svg.getDocumentWidth();
            final float h = svg.getDocumentHeight();
            final float density = resources.getDisplayMetrics().density;

            final int width = (int) (w * density + .5F);
            final int height = (int) (h * density + .5F);

            final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            final Canvas canvas = new Canvas(bitmap);
            canvas.scale(density, density);
            svg.renderToCanvas(canvas);

            out = new BitmapDrawable(resources, bitmap);
            DrawableUtils.intrinsicBounds(out);
        }

        return out;
    }

    private Drawable handleGif(InputStream stream) {

        Drawable out = null;

        final byte[] bytes = readBytes(stream);
        if (bytes != null) {
            try {
                out = new GifDrawable(bytes);
                DrawableUtils.intrinsicBounds(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return out;
    }

    private Drawable handleSimple(InputStream stream) {

        final Drawable out;

        final Bitmap bitmap = BitmapFactory.decodeStream(stream);
        if (bitmap != null) {
            out = new BitmapDrawable(resources, bitmap);
            DrawableUtils.intrinsicBounds(out);
        } else {
            out = null;
        }

        return out;
    }

    private static byte[] readBytes(InputStream stream) {

        byte[] out = null;

        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final int length = 1024 * 8;
            final byte[] buffer = new byte[length];
            int read;
            while ((read = stream.read(buffer, 0, length)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            out = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    public static class Builder {

        private OkHttpClient client;
        private Resources resources;
        private ExecutorService executorService;
        private Drawable errorDrawable;

        public Builder client(@NonNull OkHttpClient client) {
            this.client = client;
            return this;
        }

        public Builder resources(@NonNull Resources resources) {
            this.resources = resources;
            return this;
        }

        public Builder executorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public Builder errorDrawable(Drawable errorDrawable) {
            this.errorDrawable = errorDrawable;
            return this;
        }

        public AsyncDrawableLoader build() {
            if (client == null) {
                client = new OkHttpClient();
            }
            if (resources == null) {
                resources = Resources.getSystem();
            }
            if (executorService == null) {
                // we will use executor from okHttp
                executorService = client.dispatcher().executorService();
            }
            return new AsyncDrawableLoader(this);
        }
    }

    private static class Item {
        final String type;
        final InputStream inputStream;

        Item(String type, InputStream inputStream) {
            this.type = type;
            this.inputStream = inputStream;
        }
    }
}
